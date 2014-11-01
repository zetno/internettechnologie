package nl.saxion.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import nl.saxion.model.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Client {

	public static void main(String[] args) throws JSONException {
		Scanner s = new Scanner(System.in);
		String username = "";
		String password = "";
		System.out.println("Type in your username:");
		username = s.nextLine();
		System.out.println("Type in your password:");
		password = s.nextLine();

		JSONObject authorize = new JSONObject();
		authorize.put("action", "authorize");

		JSONObject msg = new JSONObject();
		msg.put("username", username);
		msg.put("password", password);
		authorize.put("message", msg);

		Model.getInstance().setUsername(username);
		Model.getInstance().setPassword(password);
		try {
			Socket socket = new Socket("localhost", 8081);
			ClientReceiveThread crt = new ClientReceiveThread(socket);
			crt.start();

			ClientSendThread cstAuthorize = new ClientSendThread(socket,authorize.toString());
			cstAuthorize.start();
			
			boolean hasToken = Model.getInstance().hasToken();
			String input = "";
			while (!input.equals("4") && !hasToken) {
				System.out.println("Choose your action:\n1. Make auction\n2. All auction\n3. Bid\n4. exit ");
				System.out.print("Your choice: ");
					input = vraagString();
				switch (input) {
				case "1":
					JSONObject makeAuction = new JSONObject();
					makeAuction.put("action", "postnewbid");
					
					System.out.println("You choose make auction.");
					System.out.println("What do you want to offer?");
					String thing = s.nextLine();
					
					System.out.println("What is the minimun price?");
					double price = vraagNummer();
					
					System.out.println("how many hours do you want to act your offer?");
					int time = s.nextInt();
					
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
					Date date = new Date(System.currentTimeMillis()+ TimeUnit.HOURS.toMillis(time));
					System.out.println(dateFormat.format(date));
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
					
				default:
						if (input.equals("4")) {
							System.out.println("Bye.");
						}else
						System.out.println("Please choose a valid number.");
					
					break;
				}
			}
		} catch (UnknownHostException e) {
			System.out.println("can't find the server.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("failed to connect with server.");
			e.printStackTrace();
		}finally{
			s.close();
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
				invoer.close();
			} catch (NumberFormatException e) {
				System.out.print("FOUT: Geef een nummer: ");
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
				invoer.close();
			} else {
				System.out.print("FOUT: Voer in ieder geval 1 karakter in: ");
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
				invoer.close();
			} catch (NumberFormatException e) {
				System.out.print("FOUT: Geef een nummer: ");
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
				invoer.close();
			} catch (NumberFormatException e) {
				System.out.print("FOUT: Geef een nummer: ");
				doorvragen = true;
			}
		}
		return ingevoerdNummer;
	}

}
