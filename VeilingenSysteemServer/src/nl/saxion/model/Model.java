package nl.saxion.model;

import java.util.ArrayList;
import java.util.List;

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
	private List<User> users = new ArrayList<User>();

	private void loadDummyData() {
		users.add(new User("bob", "wachtwoord"));
		users.add(new User("rendy", "1234"));
		users.add(new User("tim", "planken"));

		currentAuctions.add(new Auction(1, "Bike", 0, 0, 1415404800));
		currentAuctions.add(new Auction(2, "Bike", 0, 0, 1415404800));
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
	
	public void addAuction(int id, String name, int minPrice, int highestBid, int endTime) {
		//Check if the auction allready excists
	/*	for (Auction auction : auctions) {
			
		}*/
	}
}
