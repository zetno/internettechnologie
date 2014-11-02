package nl.saxion.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import nl.saxion.controller.Client;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Model {
	private static Model model;
	private User user;
	private String username;
	private String password;
	private String token;
	
	private static ArrayList<String> winnings;

	public static Model getInstance() {
		if (model == null) {
			model = new Model();
			winnings = new ArrayList<String>();
		}

		return model;
	}

	public String getToken() {
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

	public boolean hasToken() {
		if (user != null && !user.getAccesstoken().equals("")
				&& user.getAccesstoken() != null) {
			return true;
		}
		return false;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Leest json String en parsed 
	 * 
	 * @param json 
	 */
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
					int response = jsonMessage.getJSONObject("message").getInt(
							"status_code");
					if (response == 100) {
						//Correct call
					} else if (response == 200) {
						if(user == null){
							System.out.println("Wrong username or password.");
							Client.logIn();
						}else{
							System.out.println("Bad input was entered.");
						}
					} else if (response == 202) {
						System.out.println("First log in");
					} else if (response == 204) {
						System.out.println("Auction had already made.");
					} else if (response == 205) {
						System.out.println("Your bid is too low.");
					}
					
					if(user != null){
						Client.continueProgram();
					}
				} else if (action.equals("postauctions")) {
					System.out.println("All auctions:");
					JSONArray messageContent = (JSONArray) jsonMessage
							.get("message");

					if(messageContent.length() > 0){
						for (int i = 0; i < messageContent.length(); i++) {
							int auctionid = messageContent.getJSONObject(i).getInt(
									"auctionid");
							System.out.println("Auction ID: " + auctionid);
							String name = messageContent.getJSONObject(i)
									.getString("name");
							System.out.println("Item name: " + name);
							long endtime = messageContent.getJSONObject(i).getLong(
									"endtime");
							System.out.println("End time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm 'o''clock'").format(new Date(endtime)));
							double highestbid = messageContent.getJSONObject(i)
									.getDouble("highestbid");
							System.out.println("highest bid: €" + String.format("%1$,.2f", highestbid)
									+ "\n------------");
						}
					}else{
						System.out.println("There are no running auctions");
					}
					
					Client.continueProgram();

				} else if (action.equals("postwinner")) {
					String itemName = jsonMessage.getJSONObject("message")
							.getString("itemname");
					double price = jsonMessage.getJSONObject("message")
							.getDouble("price");

					winnings.add("Congratulations! You won a(n) " + itemName+ " with a price of: " + price);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void resetLoggedInUser(){
		user = null;
	}
	
	public ArrayList<String> getWinnings(){
		return winnings;
	}

	public void clearWinnings(){
		winnings.clear();
	}
}