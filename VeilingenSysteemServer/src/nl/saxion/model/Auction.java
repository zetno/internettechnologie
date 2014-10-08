package nl.saxion.model;

public class Auction {
	private int id;
	private String name;
	private int minPrice;
	private int highestBid;
	
	/**
	 * @param id
	 * @param name
	 * @param minPrice
	 * @param highestBid
	 */
	public Auction(int id, String name, int highestBid) {
		this.id = id;
		this.name = name;
		this.highestBid = highestBid;
	}
	
	public Auction(int id, String name, int minPrice, int highestBid) {
		this.id = id;
		this.name = name;
		this.minPrice = minPrice;
		this.highestBid = highestBid;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMinPrice() {
		return minPrice;
	}
	
	public int getHighestBid() {
		return highestBid;
	}
	
	public void setHighestBid(int highestBid) {
		this.highestBid = highestBid;
	}
}