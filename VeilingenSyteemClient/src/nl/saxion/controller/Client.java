package nl.saxion.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import nl.saxion.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Client {
	
	public static void main(String[] args) throws JSONException {
		
		String username = "";
		String password = "";
		
		System.out.println("Type in your username:");
		username = vraagString();
		System.out.println("Type in your password:");
		password = vraagString();

		JSONObject authorize = new JSONObject();
		authorize.put("action", "authorize");

		JSONObject msg = new JSONObject();
		msg.put("username", username);
		msg.put("password", password);
		authorize.put("message", msg);

		Model.getInstance().setUsername(username);
		Model.getInstance().setPassword(password);
		
		
		try{
			Socket socket = new Socket("localhost", 8081);
			ClientReceiveThread crt = new ClientReceiveThread(socket);
			crt.start();
			
			ClientSendThread cstAuthorize = new ClientSendThread(socket,authorize.toString());
			cstAuthorize.start();
		
			boolean hasToken = Model.getInstance().hasToken();
			boolean end = true;
			
			String input = "";
			while (!hasToken && end) {
				System.out.println("Choose your action:\n1. Make auction\n2. All auction\n3. Bid\n4. exit ");
				System.out.println("Your choice: ");
					input = vraagString();
				switch (input) {
				case "1":
					JSONObject makeAuction = new JSONObject();
					makeAuction.put("action", "postnewbid");
					
					System.out.println("You choose make auction.");
					System.out.println("What do you want to offer?");
					String thing = vraagString();
					
					System.out.println("What is the minimun price?");
					double price = vraagDouble();
					
					System.out.println("how many hours do you want to act your offer?");
					int time = vraagNummer();
					
					JSONObject makeMsg = new JSONObject();
					makeMsg.put("accesstoken", Model.getInstance().getToken());
					makeMsg.put("itemname", thing);
					makeMsg.put("mininumbid", price);
					makeMsg.put("endhours", time);
					makeAuction.put("message", makeMsg);
					ClientSendThread citMakeAuction = new ClientSendThread(socket, makeAuction.toString());
					citMakeAuction.start();
					System.out.println(thing + " is added to Veiling.nl");
					break;
					
				case "2":
					JSONObject getauctions = new JSONObject();
					getauctions.put("action", "getauctions");
					getauctions.put("message", "null");
					ClientSendThread citGetAuction = new ClientSendThread(socket, getauctions.toString());
					citGetAuction.start();
				
					break;

				case "3":
					JSONObject postBid = new JSONObject();
					postBid.put("action", "postbid");
					System.out.println("Bid");
					System.out.println("Which case do you want to bid?\nEnter auction Id:");
					String nameOfThing = vraagString();
					System.out.println("How much money do you want to offer?");
					double priceOfThing = vraagDouble();

					JSONObject bidMsg = new JSONObject();
					bidMsg.put("accesstoken", Model.getInstance().getToken());
					bidMsg.put("bid", priceOfThing);
					bidMsg.put("auctionsID", nameOfThing);

					postBid.put("message", bidMsg);

					ClientSendThread citPostBid = new ClientSendThread(socket,postBid.toString());
					citPostBid.start();
					System.out.println("Your bid is sended.");
					break;
				case  "4":
					end= false;
					System.out.println("Goodbye");
					break;
				default:
						System.out.println("Please choose a valid number.");
					
					break;
				}
			}
		} catch (UnknownHostException e) {
			System.out.println("can't find the server.");
		} catch (IOException e) {
			System.out.println("failed to connect with server.");
		}finally{
		
		}
	
	}
	
	/**
	 * Vraagt de gebruiker om een nummer in te voeren. Als hij geen nummer invoer volgt de 
	 * vraag opnieuw, net zolang tot er nummer is ingevoerd.
	 * @return Het ingevoerd nummer
	 */
	private static int vraagNummer() {
		int ingevoerdNummer = -1;
		boolean doorvragen = true;
		while (doorvragen) {
			Scanner invoer = new Scanner(System.in);
			try {
				ingevoerdNummer = Integer.parseInt(invoer.nextLine());
				doorvragen = false;
			} catch (NumberFormatException e) {
				System.out.print("FOUT: Give a number: ");
				doorvragen = true;
			}
		}
		return ingevoerdNummer;
	}
	
	/**
	 * Deze methode vraagt de gebruiker om een tekst in te voeren. Als hij een lege
	 * String invoert volgt de vraag opnieuw.
	 * @return De ingevoerde tekst als String
	 */
	public static String vraagString() {
		String ingevoerdeString = "";
		boolean doorvragen = true;
		while (doorvragen) {
			Scanner invoer = new Scanner(System.in);
			ingevoerdeString = invoer.nextLine();
			if (ingevoerdeString.length() != 0) {
				doorvragen = false;
			} else {
				System.out.print("FOUT: Enter at least a character: ");
			}
		}
		return ingevoerdeString;
	}
	
	/**
	 * Deze methode vraagt de gebruiker om een double in te voeren. Als hij een lege
	 * String invoert volgt de vraag opnieuw.
	 * @return De ingevoerde tekst als doyble
	 */
	public static double vraagDouble() {
		double ingevoerdNummer = -1;
		boolean doorvragen = true;
		while (doorvragen) {
			Scanner invoer = new Scanner(System.in);
			try {
				ingevoerdNummer = Double.parseDouble(invoer.nextLine());
				doorvragen = false;
			} catch (NumberFormatException e) {
				System.out.print("FOUT: Give a number: ");
				doorvragen = true;
			}
		}
		return ingevoerdNummer;
	}
	
	/**
	 * Deze methode vraagt de gebruiker om een getal in te voeren. Als hij een lege
	 * String invoert volgt de vraag opnieuw.
	 * @return De ingevoerde tekst als long
	 */
	public static long vraagLong() {
		long ingevoerdNummer = -1;
		boolean doorvragen = true;
		while (doorvragen) {
			Scanner invoer = new Scanner(System.in);
			try {
				ingevoerdNummer = Long.parseLong(invoer.nextLine());
				doorvragen = false;
			} catch (NumberFormatException e) {
				System.out.print("FOUT: Give a number: ");
				doorvragen = true;
			}
		}
		return ingevoerdNummer;
	}
	
}
