package nl.saxion.model;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.controller.exceptions.AllreadyExistsException;

public class Model {
	private static Model model;

	public static Model getInstance() {
		if (model == null) {
			model = new Model();
		}

		return model;
	}

	private Model() {
		loadDummyData();
	}

	private List<Auction> currentAuctions = new ArrayList<Auction>();
	private int uniqueIdCounter = 1;
	
	private List<User> users = new ArrayList<User>();

	private void loadDummyData() {
		users.add(new User("bob", "wachtwoord"));
		users.add(new User("rendy", "1234"));
		users.add(new User("tim", "planken"));

		currentAuctions.add(new Auction(1, "Bike", 0, 0, 1415404800));
		currentAuctions.add(new Auction(2, "Car", 0, 0, 1415404800));
	}

	public String authorizeUser(String username, String password) {
		String token = "";

		for (User user : users) {
			if (username.equals(user.getUsername())
					&& password.equals(user.getPassword())) {
				token = generateAccessToken();
				user.setAccesstoken(token);
			}
		}
		return token;
	}

	public String generateAccessToken() {
		String token = "";

		Boolean check = true;

		do {
			token = Integer.toString((int) (Math.floor((Math.random() * 100000000))));
			check = false;
			for (User user : users) {
				if (token.equals(user.getAccesstoken())) {
					check = true;	
				}
			}
		} while (check);

		return token;
	}
	
	public boolean isValidAccessToken(String token){
		for (User user : users) {
			if(user.getAccesstoken().equals(token)){
				return true;
			}
		}
		
		return false;
	}
	
	public void addAuction(String name, double minBid, long endTime) throws AllreadyExistsException {
		//Check if the auction allready exists
		for (Auction auction : currentAuctions) {
			if(auction.getName().equals(name)){
				throw new AllreadyExistsException();
			}
		}
		
		//add auction
		currentAuctions.add(new Auction( generateUniqueAuctionId(), name, minBid, endTime));
	}
	
	public boolean addBid(int auctionId, double bid){
		for (Auction auction : currentAuctions) {
			if(auction.getId() == auctionId){
				
			}
		}
		
		return false;
	}
	
	public int generateUniqueAuctionId(){
		return uniqueIdCounter++;
	}
	
	public List<Auction> getCurrentAuctions(){
		return currentAuctions;
	}
}
