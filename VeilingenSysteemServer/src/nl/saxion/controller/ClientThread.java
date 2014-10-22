package nl.saxion.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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
		while (true) {
			try {
				// read incoming message
				Scanner s = new Scanner(clientSocket.getInputStream());

				if (s.hasNext()) {
					String message = s.nextLine();

					System.out.println("incoming message: " + message);
					try {
						parseAction(message);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void parseAction(String json) throws JSONException {
		if (!json.isEmpty() && isJSONValid(json)) {
			JSONObject jsonMessage = new JSONObject(json);
			String action = jsonMessage.getString("action");

			switch (action) {
			case "authorize":
				System.out.println("authorize");
				authorize(jsonMessage);
				break;
			}

		}else{
			sendErrorMessage(400);
		}
	}

	private void authorize(JSONObject json) {
		System.out.println("authorizing");
		try {
			System.out.println("try");
			String username = json.getJSONObject("message").getString("username");
			String password = json.getJSONObject("message").getString("password");
			System.out.println("username: " + username + " password: " + password);
			if (!username.isEmpty() && !password.isEmpty()) {
				System.out.println("1token = ");
				String token = model.authorizeUser(username, password);
				System.out.println("2token = " + token);
				if (!token.isEmpty()) {
					System.out.println("sendAccessToken");
					sendAccessToken(token);
				} else {
					sendErrorMessage(204);
				}
			} else {
				sendErrorMessage(400);
			}
		} catch (JSONException e) {
			sendErrorMessage(400);
		}
	}

	private void sendAccessToken(String token) throws JSONException {
		System.out.println("generate json object");
		JSONObject jsonAccessToken = new JSONObject();
		jsonAccessToken.put("action", "accesstoken");

		JSONObject jsonAccessTokenMessage = new JSONObject();
		jsonAccessTokenMessage.put("token", token);

		jsonAccessToken.put("message", jsonAccessTokenMessage);
		System.out.println("sendToClient");
		sendToClient(jsonAccessToken.toString());
	}
	
	private void sendErrorMessage(int statuscode) {
		try {
			System.out.println("sendErrorMessage");
			JSONObject jsonAccessToken = new JSONObject();
			jsonAccessToken.put("action", "accesstoken");

			JSONObject jsonAccessTokenMessage = new JSONObject();
			jsonAccessTokenMessage.put("statuscode", statuscode);

			jsonAccessToken.put("message", jsonAccessTokenMessage);

			sendToClient(jsonAccessToken.toString());
		} catch (JSONException e) {
			System.out.println("internal server error: SendErrorMessage");
		}
	}

	private void sendToClient(String json) {
		try {
			System.out.println("printing to client");
			OutputStream ops = clientSocket.getOutputStream();
			PrintWriter p = new PrintWriter(ops, true);
			p.println(json);
			System.out.println("authorize send");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
}