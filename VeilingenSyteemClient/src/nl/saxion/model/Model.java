package nl.saxion.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
	private static Model model;
	private List<Auction>auctions = new ArrayList<>();
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
	/**
	 * Get a auction
	 * @param auctionID unique id number
	 * @return null if not exist, otherwise the auction object.
	 */
	public Auction getAuction(int auctionID){
		for (Auction auction : auctions) {
			if (auctionID == auction.getId()) {
				return auction;
			}
		}
		return null;
	}
	
	
	
	public List<Auction>getAllAuction(){
		return auctions;
	}
	
	
	public String getToken(){
		return user.getAccesstoken();
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
		if (user!=null && user.getAccesstoken() != null && !user.getAccesstoken().equals("")){
			return true;
		}
		return false;
	}
}