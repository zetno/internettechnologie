package nl.saxion.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import nl.saxion.controller.exceptions.AllreadyExistsException;
import nl.saxion.controller.exceptions.BadInputException;
import nl.saxion.controller.exceptions.BidTooLowException;
import nl.saxion.controller.exceptions.ForbiddenException;
import nl.saxion.model.Auction;
import nl.saxion.model.Message;
import nl.saxion.model.Model;
import nl.saxion.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author eelcokruders
 *
 */
public class ClientThread extends Thread {
	private Socket clientSocket;
	private Model model;
	private User userOnThread;

	/**
	 * A new connection has been established
	 * @param clientSocket
	 */
	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.model = Model.getInstance();
		System.out.println("A new connection with a client has been made!");
	}

	/**
	 * Run is called once a connection has been made.
	 * This method keeps looping for incoming messages and directs them 
	 * towards an action to be activated.
	 */
	public void run() {
		while (true) {
			try {
				Message message = ClientThreadHandler.messageToAction(clientSocket);
				
				//connection has been quit
				if(null == message){
					Model.getInstance().removeClientThread(this);
					return;
				}
				
				System.out.println(message.getAction());
				switch (message.getAction()) {
					case "authorize": authorize(message.getJSONObjectMessage()); break;
					case "postnewbid": createAuction(message.getJSONObjectMessage());break;
					case "getauctions": sendCurrentAuctions(); break;
					case "postbid": addBid(message.getJSONObjectMessage()); break;
					default : sendResponseMessage(203); break;
				}
			} catch (BadInputException bie){
				sendResponseMessage(200);
			} catch (JSONException e) {
				sendResponseMessage(201);
			} catch (IOException e) {
				sendResponseMessage(201);
			} catch (ForbiddenException e) {
				sendResponseMessage(202);
			} catch (AllreadyExistsException e) {
				sendResponseMessage(204);
			} catch (BidTooLowException e) {
				sendResponseMessage(205);
			}
		}
	}

	/**
	 * This method checks if the username and valid is valid.
	 * an new unique accesstoken is send back to the client.
	 * @param json
	 * @throws BadInputException
	 * @throws JSONException
	 */
	private void authorize(JSONObject json) throws BadInputException, JSONException {
		String username = json.getString("username");
		String password = json.getString("password");
		if (!username.isEmpty() && !password.isEmpty()) {
			String token = model.authorizeUser(username, password);
			
			if (!token.isEmpty() && token != "") {
				userOnThread = model.getUserByAccessToken(token);
				sendAccessToken(token);
			} else {
				throw new BadInputException();
			}
		} else {
			throw new BadInputException();
		}
	}
	
	/**
	 * This method creates an auctions
	 * @param json
	 * @throws BadInputException
	 * @throws ForbiddenException
	 * @throws JSONException
	 * @throws AllreadyExistsException
	 */
	private void createAuction(JSONObject json) throws BadInputException, ForbiddenException, JSONException, AllreadyExistsException{
		String token = json.getString("accesstoken");
		String name = json.getString("itemname");
		long endTime = ClientThreadHandler.createEpochDate(json.getInt("endhours"));
		double minBid = json.getDouble("mininumbid");

		System.out.println("token:= " + token);
		if (token != null && token != "" && !token.isEmpty() && model.isValidAccessToken(token)) {
			if(!name.isEmpty() && minBid >= 0 && endTime > 0){
				model.addAuction(name, minBid, endTime);
				sendResponseMessage(100);
			}else{
				throw new BadInputException();
			}
		} else {
			throw new ForbiddenException();
		}
	}
	
	/**
	 * This method adds a bid to a running auction
	 * @param json
	 * @throws JSONException
	 * @throws ForbiddenException
	 * @throws BadInputException
	 * @throws BidTooLowException
	 */
	private void addBid(JSONObject json) throws JSONException, ForbiddenException, BadInputException, BidTooLowException{
		String token = json.getString("accesstoken");
		double bid = json.getDouble("bid");
		int auctionId = json.getInt("auctionsID");
		
		if (token != null && token != "" && !token.isEmpty() && model.isValidAccessToken(token)) {
			if( bid >= 0 && auctionId >= 0 && System.currentTimeMillis() <= model.getAuctionById(auctionId).getEndTime() && model.addBid(auctionId, bid, token)){
				sendResponseMessage(100);
			}else{
				throw new BadInputException();
			}
		} else {
			throw new ForbiddenException();
		}
	}

	/**
	 * this method sends all current running auctions to the client
	 * @throws JSONException
	 */
	private void sendCurrentAuctions() throws JSONException{
		System.out.println("sendcurrentauctions");
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("action", "postauctions");
		
		JSONArray jsonAuctions = new JSONArray();
		for (Auction auction : model.getCurrentAuctions()) {
			if(!auction.hasEnded()){
				JSONObject jsonAuction = new JSONObject();
				jsonAuction.put("auctionid", auction.getId());
				jsonAuction.put("name", auction.getName());	 
				jsonAuction.put("endtime", auction.getEndTime());	
				jsonAuction.put("highestbid", auction.getHighestBid());	
				jsonAuctions.put(jsonAuction);           
			}
		}                                                
		jsonMessage.put("message", jsonAuctions);        
		System.out.println(jsonMessage.toString());
		sendToClient(jsonMessage.toString());
	}
	
	/**
	 * this method sends the accesstoken to the client
	 * @param token
	 * @throws JSONException
	 */
	private void sendAccessToken(String token) throws JSONException {
		JSONObject jsonAccessToken = new JSONObject();
		jsonAccessToken.put("action", "accesstoken");

		JSONObject jsonAccessTokenMessage = new JSONObject();
		jsonAccessTokenMessage.put("token", token);

		jsonAccessToken.put("message", jsonAccessTokenMessage);

		sendToClient(jsonAccessToken.toString());
		System.out.println("A new accesstoken has been logged: " + token);
	}
	
	/**
	 * This method sends a response message with status code to the client
	 * @param statuscode
	 */
	private void sendResponseMessage(int statuscode) {
		System.out.println("a response was requested! code: " + statuscode);
		try {
			JSONObject jsonResponseMessage = new JSONObject();
			jsonResponseMessage.put("action", "response");

			JSONObject jsonResponseMessageMessage = new JSONObject();
			jsonResponseMessageMessage.put("status_code", statuscode);

			jsonResponseMessage.put("message", jsonResponseMessageMessage);

			sendToClient(jsonResponseMessage.toString());
		} catch (JSONException e) {
			System.out.println("internal server error: SendErrorMessage");
		}
	}

	/**
	 * this method sends json to the client
	 * @param json
	 */
	private void sendToClient(String json) {
		try {
			OutputStream ops = clientSocket.getOutputStream();
			PrintWriter p = new PrintWriter(ops, true);
			p.println(json);
		} catch (IOException e) {
			System.out.println("internal server error: SendErrorMessage");
		}
	}
	
	public User getUserOnThread(){
		return userOnThread;
	}
	
	public void sendClientWonAuction(Auction auction){
		try {
			JSONObject jsonPostWinner = new JSONObject();
			jsonPostWinner.put("action", "postwinner");

			JSONObject jsonPostWinnerMessage = new JSONObject();
			jsonPostWinnerMessage.put("itemname", auction.getName());
			jsonPostWinnerMessage.put("price", auction.getHighestBid());
			
			jsonPostWinner.put("message", jsonPostWinnerMessage);

			sendToClient(jsonPostWinner.toString());
		} catch (JSONException e) {
			System.out.println("internal server error: SendErrorMessage");
		}
	}
}