package nl.saxion.model;

public class Auction {
	private int id;
	private String name;
	private double minPrice;
	private double highestBid;
	private int endTime; //in epoch
	
	/**
	 * @param id
	 * @param name
	 * @param minPrice
	 * @param highestBid
	 */
	public Auction(int id, String name, double highestBid, int endTime) {
		this.id = id;
		this.name = name;
		this.highestBid = highestBid;
	}
	
	public Auction(int id, String name, double minPrice, double highestBid, int endTime) {
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
	
	public double getMinPrice() {
		return minPrice;
	}
	
	public double getHighestBid() {
		return highestBid;
	}
	
	public void setHighestBid(double highestBid) {
		this.highestBid = highestBid;
	}
	
	public int getEndTime() {
		return endTime;
	}
}
