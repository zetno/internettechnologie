package nl.saxion.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;

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
				BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
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
					System.out.println(token);
					Model.getInstance().setToken(token);
					
				}else if (action.equals("response")) {
					String response = jsonMessage.getJSONObject("message").getString("response");
					if (response.equals("100")) {
						System.out.println("You are logged in as user");
					}else if (response.equals("200")) {
						System.out.println("Wrong username or password.");
					}else if(response.equals("202")){
						System.out.println("First log in");
					}
					
				}	
				else if(action.equals("postauctions")){
//					
					JSONArray messageContent = (JSONArray) jsonMessage.get("message");
			       
					for (int i = 0; i < messageContent.length(); i++) {
						String auctionid = messageContent.getJSONObject(i).getString("auctionid");
						System.out.println(auctionid);
						String name = messageContent.getJSONObject(i).getString("name");
						System.out.println(name);
						String endtime = messageContent.getJSONObject(i).getString("endtime");
						System.out.println(endtime);
						String highestbid = messageContent.getJSONObject(i).getString("highestbid");
						System.out.println(highestbid+"\n------------");
					}
			          
				}		
					
					
				
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
