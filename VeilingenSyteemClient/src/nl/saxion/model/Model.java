package nl.saxion.model;


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
	
	public void userLoggedIn(String username, String password, String accesstoken){
		user = new User(username, password);
		user.setAccesstoken(accesstoken);
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
					String token = jsonMessage.getJSONObject("message").getString("token");
					String username = Model.getInstance().getUsername();
					String password = Model.getInstance().getPassword();
					userLoggedIn(username, password, token);
					setToken(token);

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
						long endtime = messageContent.getJSONObject(i).getLong("endtime");
						System.out.println("End time: "+endtime);
						double highestbid = messageContent.getJSONObject(i).getDouble("highestbid");
						System.out.println("highest bid: "+highestbid + "\n------------");
					}

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
	
	
}