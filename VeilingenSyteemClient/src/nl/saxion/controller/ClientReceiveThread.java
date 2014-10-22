package nl.saxion.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import nl.saxion.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientReceiveThread extends Thread {
	private Socket socket;

	public ClientReceiveThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Scanner s = new Scanner(socket.getInputStream());
				String message = "";
				if (s.hasNext()) {
					message = s.nextLine();
					parseAction(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void parseAction(String json) {
		if (!json.isEmpty()) {
			JSONObject jsonMessage;
			try {
				jsonMessage = new JSONObject(json);

				String action = jsonMessage.getString("action");
				if (action.equals("accesstoken")) {
					String token = jsonMessage.getJSONObject("message").getString("token");
					Model.getInstance().setToken(token);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
