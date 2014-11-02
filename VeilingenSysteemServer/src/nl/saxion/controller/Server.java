package nl.saxion.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import nl.saxion.model.Model;

public class Server {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8081);
			
			AuctionThread at = new AuctionThread();
			at.start();
			
			//Keep checking for new clients connecting
			while (true) {
				System.out.println("Server is waiting for client...");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Server has accepted a new client!");
				
				ClientThread ct = new ClientThread(clientSocket);
				ct.start();
				
				Model.getInstance().addClientThread(ct);
			}
			
		} catch (IOException e) {
		}
	}
}
