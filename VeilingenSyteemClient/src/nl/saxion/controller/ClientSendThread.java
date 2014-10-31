package nl.saxion.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSendThread extends Thread{
	private Socket socket;
	private String json;
	
	public ClientSendThread(Socket socket, String json){
		this.socket = socket;
		this.json = json;
	}
	
	@Override
	public void run(){

			Scanner sc = new Scanner(System.in);
			
			try {
				 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				 out.println(json);
				 System.out.println(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				   
		
	}
}
