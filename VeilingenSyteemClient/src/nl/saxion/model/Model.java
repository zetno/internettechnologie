package nl.saxion.model;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import nl.saxion.controller.Client;
import nl.saxion.controller.ClientReceiveThread;
import nl.saxion.controller.ClientSendThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Model {
	private static Model model;
	private User user;
	private String username;
	private String password;
	private String token;

	public static Model getInstance(){
		if(model == null){
			model = new Model();
		}
		
		return model;
	}

	public String getToken(){
		return token;
	} 
	
	public void userLoggedIn(String username, String password, String accesstoken) throws JSONException{
		user = new User(username, password);
		user.setAccesstoken(accesstoken);
		Client.program();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean hasToken(){
		if (user!=null  && !user.getAccesstoken().equals("") && user.getAccesstoken() !=null){
			return true;
		}
		return false;
	}

	public void setToken(String token) {
		this.token = token;
	}
	

	public void parseAction(String json) {
		if (!json.isEmpty()) {
			JSONObject jsonMessage;
			try {
				jsonMessage = new JSONObject(json);

				String action = jsonMessage.getString("action");

				if (action.equals("accesstoken")) {
					token = jsonMessage.getJSONObject("message").getString("token");
					String username = getUsername();
					String password = getPassword();
					userLoggedIn(username, password, token);

				} else if (action.equals("response")) {
					int response = jsonMessage.getJSONObject("message").getInt("status_code");
					if (response == 100) {
						System.out.println("You are logged in as user");
					} else if (response == 200) {
						System.out.println("Wrong username or password.");
						if(user == null){
							Client.logIn();
						}
					} else if (response == 202) {
						System.out.println("First log in");
					} else if (response == 204) {
						System.out.println("Auction had already made.");
					}else if (response == 205){
						System.out.println("Your bid is too low.");
					}

				} else if (action.equals("postauctions")) {
					System.out.println("All auctions:");
					JSONArray messageContent = (JSONArray) jsonMessage.get("message");

					for (int i = 0; i < messageContent.length(); i++) {
						int auctionid = messageContent.getJSONObject(i).getInt("auctionid");
						System.out.println("Auction ID: "+auctionid);
						String name = messageContent.getJSONObject(i).getString("name");
						System.out.println("Item name: "+name);
						long endtime = messageContent.getJSONObject(i).getLong("endtime");
						System.out.println("End time: "+endtime);
						double highestbid = messageContent.getJSONObject(i).getDouble("highestbid");
						System.out.println("highest bid: "+highestbid + "\n------------");
					}
					
					Client.continueProgram();

				}else if(action.equals("postwinner")){
					String itemName = jsonMessage.getJSONObject("message").getString("itemname");
					double price = jsonMessage.getJSONObject("message").getDouble("price");

					System.out.println("Congratulation you won " + itemName+ " with price: " + price);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkToken(){
		if (token !=null){
			return true;
		}
		return false;
	}
	
	public void login() throws JSONException, UnknownHostException, IOException {
		Scanner scan = new Scanner(System.in);
		while (!checkToken()) {
			String username = "";
			String password = "";

			System.out.println("Type in your username:");
			username = scan.nextLine();
			System.out.println("Type in your password:");
			password = scan.nextLine();

			JSONObject authorize = new JSONObject();
			authorize.put("action", "authorize");

			JSONObject msg = new JSONObject();
			msg.put("username", username);
			msg.put("password", password);
			authorize.put("message", msg);

			setUsername(username);
			setPassword(password);

			Socket socket = new Socket("localhost", 8081);
			ClientSendThread cstAuthorize = new ClientSendThread(socket,authorize.toString());
			cstAuthorize.start();
			ClientReceiveThread crt = new ClientReceiveThread(socket);
			crt.start();

		}
	}
	
	public void resetLoggedInUser(){
		user = null;
	}
}