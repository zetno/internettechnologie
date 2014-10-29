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
				
				switch (message.getAction()) {
					case "authorize": authorize(message.getMessage()); break;
					case "postnewbid": createAuction(message.getMessage()); break;
				}
			} catch (BadInputException bie){
				sendErrorMessage(200);
			} catch (JSONException e) {
				sendErrorMessage(201);
			} catch (IOException e) {
				sendErrorMessage(200);
			}
		}
	}

	
	private void authorize(JSONObject json) throws BadInputException {
		try {
			String username = json.getString("username");
			String password = json.getString("password");
			if (!username.isEmpty() && !password.isEmpty()) {
				String token = model.authorizeUser(username, password);

				if (!token.isEmpty()) {
					sendAccessToken(token);
				} else {
					throw new BadInputException();
				}
			} else {
				throw new BadInputException();
			}
		} catch (JSONException e) {
			throw new BadInputException();
		}
	}
	
	private void createAuction(JSONObject json){
		/*try {
			String username = json.getString("username");
			String password = json.getString("password");
			if (!username.isEmpty() && !password.isEmpty()) {
				String token = model.authorizeUser(username, password);

				if (!token.isEmpty()) {
					sendAccessToken(token);
				} else {
					throw new BadInputException();
				}
			} else {
				throw new BadInputException();
			}
		} catch (JSONException e) {
			throw new BadInputException();
		}*/
	}

	private void sendAccessToken(String token) throws JSONException {
		JSONObject jsonAccessToken = new JSONObject();
		jsonAccessToken.put("action", "accesstoken");

		JSONObject jsonAccessTokenMessage = new JSONObject();
		jsonAccessTokenMessage.put("token", token);

		jsonAccessToken.put("message", jsonAccessTokenMessage);

		sendToClient(jsonAccessToken.toString());
	}
	
	private void sendErrorMessage(int statuscode) {
		try {
			System.out.println("sendErrorMessage");
			JSONObject jsonErrorMessage = new JSONObject();
			jsonErrorMessage.put("action", "response");

			JSONObject jsonErrorMessageMessage = new JSONObject();
			jsonErrorMessageMessage.put("statuscode", statuscode);

			jsonErrorMessage.put("message", jsonErrorMessageMessage);

			sendToClient(jsonErrorMessage.toString());
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