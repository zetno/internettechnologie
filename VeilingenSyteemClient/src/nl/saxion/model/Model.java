package nl.saxion.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
	private static Model model;
	private List<Auction>auctions = new ArrayList<>();
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
	
	public void login(String username, String password){
		
	}
	
}