package nl.saxion.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import nl.saxion.model.Model;

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
		if (!json.isEmpty()) {
			JSONObject jsonMessage = new JSONObject(json);
			String action = jsonMessage.getString("action");
			
			switch (action) {
			case "authorize":
				authorize(jsonMessage);
				break;
			}
			
		}
	}
	
	private void authorize(JSONObject json) throws JSONException{
		String username = json.getJSONObject("message").getString("username");
		String password = json.getJSONObject("message").getString("password");
		
		String token = model.authorizeUser(username, password);
		
		sendAccessToken(token);
	}
	
	private void sendAccessToken(String token) throws JSONException{
		JSONObject jsonAccessToken = new JSONObject();
		jsonAccessToken.put("action", "accesstoken");
		
		JSONObject jsonAccessTokenMessage = new JSONObject();
		jsonAccessTokenMessage.put("token", token);
		
		jsonAccessToken.put("message", jsonAccessTokenMessage);
		
		sendToClient(jsonAccessToken.toString());
	}
	
	private void sendToClient(String json){
		try {
			OutputStream ops = clientSocket.getOutputStream();
			PrintWriter p = new PrintWriter(ops,true);
			p.println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}