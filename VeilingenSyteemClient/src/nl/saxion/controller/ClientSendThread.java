package nl.saxion.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSendThread extends Thread{
	private Socket socket;
	private String nickname;
	
	public ClientSendThread(Socket socket){
		this.socket = socket;
		this.nickname = "derp";
	}
	
	@Override
	public void run(){
		while(true){
			Scanner sc = new Scanner(System.in);
			String message = sc.nextLine();
			
			try {
				OutputStream ops = socket.getOutputStream();
				PrintWriter p = new PrintWriter(ops,true);
				
				p.println(nickname + ": " + message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
