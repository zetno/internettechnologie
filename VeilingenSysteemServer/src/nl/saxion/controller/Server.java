package nl.saxion.controller;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import nl.saxion.model.Model;

public class Server {
	private static Server server;

	private Server(){
		clientSockets = new ArrayList<Socket>();
	}
	
	public static Server getInstance(){
		if(server == null){
			server = new Server();
		}
		
		return server;
	}
	
	private List<Socket> clientSockets;
	
	public List<Socket> getClientSockets(){
		return clientSockets;
	}
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8081);

			while (true) {
				System.out.println("Server is waiting for client...");
				Socket clientSocket = serverSocket.accept();
				System.out.println("Server has accepted a new client!");
				
				Server.getInstance().getClientSockets().add(clientSocket);

				ClientThread ct = new ClientThread(clientSocket);
				ct.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
