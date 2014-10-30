package nl.saxion.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
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

			ClientSendThread cstAuthorize = new ClientSendThread(socket,
					authorize.toString());
			cstAuthorize.start();
			boolean hasToken = Model.getInstance().hasToken();
			int input = 0;
			while (input != 4 && !hasToken) {
				System.out.println("Choose your action:\n1. Make auction\n2. All auction\n3. Bid\n4. exit ");
				input = s.nextInt();
				switch (input) {
				case 1:
					JSONObject makeAuction = new JSONObject();
					makeAuction.put("action", "postnewbid");

					System.out.println("What do you want to offer?");
					String thing = s.next();
					System.out.println("What is the minimun price?");
					double price = s.nextDouble();
					System.out.println("How many hours do you want action to last?");
					long time = s.nextInt();

					Date date = new Date(System.currentTimeMillis()
							+ TimeUnit.HOURS.toMillis(time));

					JSONObject makeMsg = new JSONObject();
					makeMsg.put("accesstoken", Model.getInstance().getToken());
					makeMsg.put("itemname", thing);
					makeMsg.put("mininumbid", price);
					makeMsg.put("enddate", time);
					makeAuction.put("message", makeMsg);
					
					ClientSendThread citMakeAuction = new ClientSendThread(socket, makeAuction.toString());
					citMakeAuction.start();
					System.out.println("Done.");
					break;
				case 2:

					JSONObject getauctions = new JSONObject();
					getauctions.put("action", "getauctions");
					getauctions.put("message", "null");

					ClientSendThread citGetAuction = new ClientSendThread(
							socket, getauctions.toString());
					citGetAuction.start();
					break;

				case 3:
					JSONObject postBid = new JSONObject();
					postBid.put("action", "postbid");

					System.out.println("Which case(Enter auction Id)?");
					String nameOfThing = s.next();
					System.out.println("Which price?");
					double priceOfThing = s.nextDouble();

					JSONObject bidMsg = new JSONObject();
					bidMsg.put("accesstoken", Model.getInstance().getToken());
					bidMsg.put("bid", priceOfThing);
					bidMsg.put("auctionsID", nameOfThing);

					postBid.put("message", bidMsg);

					ClientSendThread citPostBid = new ClientSendThread(socket,
							postBid.toString());
					citPostBid.start();

				default:
					break;
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			s.close();
		}
	}
}
