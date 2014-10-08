package nl.saxion.model;

import java.util.ArrayList;

public class Model {
	private ArrayList<Auction>auctions;
	
	public Model(){
		auctions = new ArrayList<>();
	}
	
	
	public Auction getAuction(int auctionID){
		for (Auction auction : auctions) {
			if (auctionID == auction.getId()) {
				return auction;
			}
		}
		return null;
	}
	
	
	
	
}