package nl.saxion.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import nl.saxion.model.Model;

import org.json.JSONArray;
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

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = in.readLine();
				parseAction(message);

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
					String username = Model.getInstance().getUsername();
					String password = Model.getInstance().getPassword();
					Model.getInstance().userLoggedIn(username, password, token);

				} else if (action.equals("response")) {
					int response = jsonMessage.getJSONObject("message").getInt("status_code");
					if (response == 100) {
						System.out.println("You are logged in as user");
					} else if (response == 200) {
						System.out.println("Wrong username or password.");
					} else if (response == 202) {
						System.out.println("First log in");
					} else if (response == 204) {
						System.out.println("Auction had already made.");
					}

				} else if (action.equals("postauctions")) {
					System.out.println("All auctions:");
					JSONArray messageContent = (JSONArray) jsonMessage.get("message");

					for (int i = 0; i < messageContent.length(); i++) {
						int auctionid = messageContent.getJSONObject(i).getInt("auctionid");
						System.out.println("Auction ID: "+auctionid);
						String name = messageContent.getJSONObject(i).getString("name");
						System.out.println("Item name: "+name);
						int endtime = messageContent.getJSONObject(i).getInt("endtime");
						System.out.println("End time: "+endtime);
						double highestbid = messageContent.getJSONObject(i).getDouble("highestbid");
						System.out.println("Hiest bid: "+highestbid + "\n------------");
					}

				}else if(action.equals("postwinner")){
					String itemName = jsonMessage.getJSONObject("message").getString("laptop");
					double price = jsonMessage.getJSONObject("message").getDouble("price");

					System.out.println("Congratulation you won " + itemName+ " with price: " + price);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
