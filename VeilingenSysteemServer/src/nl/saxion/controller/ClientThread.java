package nl.saxion.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
				}
			} catch (BadInputException bie){
				sendResponseMessage(200);
			} catch (JSONException e) {
				sendResponseMessage(201);
			} catch (IOException e) {
				sendResponseMessage(201);
			} catch (ForbiddenException e) {
				sendResponseMessage(202);
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
	
	private void createAuction(JSONObject json) throws BadInputException, ForbiddenException, JSONException{
		String token = json.getString("accesstoken");
		String item = json.getString("itemname");
		double minBid = json.getDouble("minimumbid");
		if (!token.isEmpty()) {
			if(!item.isEmpty() && !Double.isNaN(minBid) && minBid > 0){
				
			}else{
				throw new BadInputException();
			}
		} else {
			throw new ForbiddenException();
		}
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
			JSONObject jsonErrorMessage = new JSONObject();
			jsonErrorMessage.put("action", "response");

			JSONObject jsonErrorMessageMessage = new JSONObject();
			jsonErrorMessageMessage.put("status_code", statuscode);

			jsonErrorMessage.put("Message with statuscode " + Integer.toString(statuscode) + " is send", jsonErrorMessageMessage);

			sendToClient(jsonErrorMessage.toString());
			System.out.println("Erorr ");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}