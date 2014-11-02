package nl.saxion.model;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.controller.exceptions.AllreadyExistsException;
import nl.saxion.controller.exceptions.BidTooLowException;

/**
 * The singleton Model containing the current users and auctions
 * @author eelcokruders
 *
 */
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

	/**
	 * Used to fill the model with dummy data
	 */
	private void loadDummyData() {
		users.add(new User("bob", "wachtwoord"));
		users.add(new User("rendy", "1234"));
		users.add(new User("tim", "planken"));
		
		try {
			addAuction("bicicle", 0.0, 1414959852L);
			addAuction("candy bar", 20.0, 1414959852L);
		} catch (AllreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to check if the username and password is valid
	 * @param username
	 * @param password
	 * @return an accesstoken
	 */
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

	/**
	 * this method generates a new unique accesstoken
	 * @return
	 */
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
	
	/**
	 * This method checks if the accesstoken is valid and active
	 * @param token
	 * @return
	 */
	public boolean isValidAccessToken(String token){
		for (User user : users) {
			if(user.getAccesstoken().equals(token)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * this method adds an auction
	 * @param name
	 * @param minBid
	 * @param endTime
	 * @throws AllreadyExistsException
	 */
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
	
	/**
	 * this method adds an bid to a running auction
	 * @param auctionId
	 * @param bid
	 * @param accesstoken
	 * @return
	 * @throws BidTooLowException
	 */
	public boolean addBid(int auctionId, double bid, String accesstoken) throws BidTooLowException{
		for (Auction auction : currentAuctions) {
			if(auction.getId() == auctionId){
				if(bid > auction.getHighestBid()){
					auction.setHighestBid(bid, accesstoken);
				}else{
					throw new BidTooLowException();
				}
			}
		}
		
		return false;
	}
	
	/**
	 * @return a new unique auction id
	 */
	public int generateUniqueAuctionId(){
		return uniqueIdCounter++;
	}
	
	/**
	 * @return a list of current running auctions
	 */
	public List<Auction> getCurrentAuctions(){
		return currentAuctions;
	}
}
