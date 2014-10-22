package nl.saxion.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

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
			
			
			System.out.println("Choose your action:\n1. Make acution\n2. All acution ");
			int input = s.nextInt();
			
			switch (input) {
			case 1:
				
				break;
			case 2:
				JSONObject getauctions = new JSONObject();
				getauctions.put("action", "getauctions");
				getauctions.put("message", "null");
				
				ClientSendThread cit = new ClientSendThread(socket, getauctions.toString());
				cit.start();
				break;

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
