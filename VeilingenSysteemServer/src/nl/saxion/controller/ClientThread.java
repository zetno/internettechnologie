package nl.saxion.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientThread extends Thread {
	private Socket clientSocket;
	
	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		System.out.println("A new connection with a client has been made!");
	}
	
	public void run(){
		while (true) {
			try {
				//read incoming message
				Scanner s = new Scanner(clientSocket.getInputStream());
				
				if(s.hasNext()){
					String message  = s.nextLine();
					
					try {
						parseJson(message);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
					//send incoming message to other clients
					for (Socket socket : Server.getInstance().getClientSockets()) {
						if(!socket.equals(clientSocket)){
							OutputStream ops = socket.getOutputStream();
							PrintWriter p = new PrintWriter(ops,true);
							p.println(message);
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parseJson(String json) throws JSONException{
		if(!json.isEmpty()){
			JSONObject jsonMessage = new JSONObject(json);
			jsonMessage.getString("action");
		}
	}
}