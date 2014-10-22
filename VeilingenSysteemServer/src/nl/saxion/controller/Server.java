package nl.saxion.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import nl.saxion.model.Model;

public class Server {
	private static Model model;

	private Server(){
		model = model.getInstance();
	}
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8081);

			while (true) {
				System.out.println("Server is waiting for client...");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Server has accepted a new client!");
				
				model.getClientSockets().add(clientSocket);

				ClientThread ct = new ClientThread(clientSocket);
				ct.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
