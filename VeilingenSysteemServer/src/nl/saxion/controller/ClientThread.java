package nl.saxion.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import nl.saxion.controller.exceptions.AllreadyExistsException;
import nl.saxion.controller.exceptions.BadInputException;
import nl.saxion.controller.exceptions.ForbiddenException;
import nl.saxion.model.Auction;
import nl.saxion.model.Message;
import nl.saxion.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientThread extends Thread {
	private Socket clientSocket;
	private Model model;

	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.model = Model.getInstance();
		System.out.println("A new connection with a client has been made!");
	}

	public void run() {
		//wait for incoming message
		while (true) {
			try {
				Message message = ClientThreadHandler.messageToAction(clientSocket);
				System.out.println(message.getAction());
				switch (message.getAction()) {
					case "authorize": authorize(message.getMessage()); break;
					case "postnewbid": createAuction(message.getMessage()); break;
					case "getauctions": sendCurrentAuctions(); break;
					case "postbid": /*TODO: add bid to auction */; break;
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
			}
		}
	}

	
	private void authorize(JSONObject json) throws BadInputException, JSONException {
		String username = json.getString("username");
		String password = json.getString("password");
		if (!username.isEmpty() && !password.isEmpty()) {
			String token = model.authorizeUser(username, password);
			
			if (!token.isEmpty() && token != "") {
				sendAccessToken(token);
			} else {
				throw new BadInputException();
			}
		} else {
			throw new BadInputException();
		}
	}
	
	private void createAuction(JSONObject json) throws BadInputException, ForbiddenException, JSONException, AllreadyExistsException{
		String token = json.getString("accesstoken");
		String name = json.getString("itemname");
		int endDate = json.getInt("enddate");
		double minBid = json.getDouble("minimumbid");
		
		if (!token.isEmpty()) {
			if(!name.isEmpty() && !Double.isNaN(minBid) && minBid >= 0 && endDate > 0){
				model.addAuction(name, minBid, endDate);
				sendResponseMessage(100);
			}else{
				throw new BadInputException();
			}
		} else {
			throw new ForbiddenException();
		}
	}

	private void sendCurrentAuctions() throws JSONException{
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.put("action", "postauctions");
		
		JSONArray jsonAuctions = new JSONArray();
		for (Auction auction : model.getCurrentAuctions()) {
			JSONObject jsonAuction = new JSONObject();
			jsonAuction.put("auctionid", auction.getId());
			jsonAuction.put("name", auction.getName());	
			jsonAuction.put("endtime", auction.getEndTime());	
			jsonAuction.put("highestbid", auction.getHighestBid());	
			jsonAuctions.put(jsonAuction);
		}
		jsonMessage.put("message", jsonAuctions);
		
		sendToClient(jsonMessage.toString());
	}
	
	private void sendAccessToken(String token) throws JSONException {
		JSONObject jsonAccessToken = new JSONObject();
		jsonAccessToken.put("action", "accesstoken");

		JSONObject jsonAccessTokenMessage = new JSONObject();
		jsonAccessTokenMessage.put("token", token);

		jsonAccessToken.put("message", jsonAccessTokenMessage);

		sendToClient(jsonAccessToken.toString());
		System.out.println("A new accesstoken has been logged: " + token);
	}
	
	private void sendResponseMessage(int statuscode) {
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

	private void sendToClient(String json) {
		try {
			OutputStream ops = clientSocket.getOutputStream();
			PrintWriter p = new PrintWriter(ops, true);
			p.println(json);
		} catch (IOException e) {
			System.out.println("internal server error: SendErrorMessage");
		}
	}
	
	
}