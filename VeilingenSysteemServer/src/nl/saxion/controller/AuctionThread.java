package nl.saxion.controller;

import java.net.Socket;

import nl.saxion.model.Auction;
import nl.saxion.model.Model;

/**
 * @author eelcokruders
 */
public class AuctionThread extends Thread {
	private Model model;

	public AuctionThread() {
		this.model = Model.getInstance();
	}

	/**
	 * Run is called once a connection has been made.
	 * This method keeps looping and checks if an auction has ended
	 */
	public void run() {
		while (true) { 
			for (Auction auction : model.getCurrentAuctions()) {
				if(null != auction && !auction.hasEnded() && auction.getEndTime() < System.currentTimeMillis()){
					for (ClientThread ct : model.getClientThreads()) {
						System.out.println("checking active client thread");
						if(ct.getUserOnThread() != null
								&& ct.getUserOnThread().getAccesstoken() != null
								&& auction.getAccessTokenHighestBid() != null
								&& ct.getUserOnThread().getAccesstoken().equals(auction.getAccessTokenHighestBid())){
							System.out.println("There is an active online client that we need to notify as winner! " + auction.getAccessTokenHighestBid() + " with " + auction.getHighestBid());
							ct.sendClientWonAuction(auction);
						}
					}
					System.out.println("an auction has ended.");
					auction.setHasEnded();
				}
			}
		}
	}

}