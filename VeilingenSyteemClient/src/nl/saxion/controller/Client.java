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
		Scanner s = new Scanner(System.in);
		String username = "";
		String password = "";
		System.out.println("Type in your username");
		username = s.nextLine();
		System.out.println("Type in your password");
		password = s.nextLine();
		
		JSONObject authorize = new JSONObject();
		authorize.put("action", "authorize");
		
		JSONObject msg = new JSONObject();
		msg.put("username", username);
		msg.put("password", password);
		
		authorize.put("message", msg);
		try {
			Socket socket = new Socket("127.0.0.1", 8081);

			ClientReceiveThread crt = new ClientReceiveThread(socket);
			crt.start();
			
			
			System.out.println("Choose your action:\n1. Make acution\n2. All acution\n3. Bid ");
			int input = s.nextInt();
			
			switch (input) {
			case 1:
				JSONObject makeAuction = new JSONObject();
				makeAuction.put("action", "postnewbid");
				
				System.out.println("What do you want to offer?");
				String thing = s.nextLine();
				
				System.out.println("What is the minimun price?");
				double price = s.nextDouble();
				
				JSONObject makeMsg = new JSONObject();
				makeMsg.put("accesstoken", Model.getInstance().getToken());
				makeMsg.put("itemname", thing);
				makeMsg.put("mininumbid", price);
				
				makeAuction.put("message", makeMsg);
				
				ClientSendThread citMakeAuction = new ClientSendThread(socket, makeAuction.toString());
				citMakeAuction.start();
				break;
			case 2:
				
				JSONObject getauctions = new JSONObject();
				getauctions.put("action", "getauctions");
				getauctions.put("message", "null");
				
				
				ClientSendThread citGetAuction = new ClientSendThread(socket, getauctions.toString());
				citGetAuction.start();
				break;
				
			case 3:
				JSONObject postBid = new JSONObject();
				postBid.put("action", "postbid");
				
				System.out.println("Which case(Enter auction Id)?");
				String nameOfThing = s.nextLine();
				System.out.println("Which price?");
				double priceOfThing = s.nextDouble();
				
				JSONObject bidMsg = new JSONObject();
				bidMsg.put("accesstoken", Model.getInstance().getToken());
				bidMsg.put("bid",priceOfThing);
				bidMsg.put("auctionsID",nameOfThing);
				
				postBid.put("message", bidMsg);
				
				ClientSendThread citPostBid = new ClientSendThread(socket, postBid.toString());
				citPostBid.start();
				
				
			default:
				break;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			s.close();
		}
	}
}
