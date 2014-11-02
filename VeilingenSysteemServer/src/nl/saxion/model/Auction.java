package nl.saxion.model;

public class Auction {
	private int id;
	private String name;
	private double highestBid;
	private double minPrice;
	private long endTime; //in epoch
	
	/**
	 * @param id
	 * @param name
	 * @param highestBid
	 */
	public Auction(int id, String name, double highestBid, long endTime) {
		this.id = id;
		this.name = name;
		this.highestBid = highestBid;
		this.endTime = endTime;
	}
	
	public Auction(int id, String name, double minPrice, double highestBid, long endTime) {
		this.id = id;
		this.name = name;
		this.highestBid = highestBid;
		this.endTime = endTime;
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
	
	public long getEndTime() {
		return endTime;
	}
}
