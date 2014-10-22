package nl.saxion.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientReceiveThread extends Thread{
	private Socket socket;
	
	public ClientReceiveThread(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run(){
		while(true){
			try {
				Scanner s = new Scanner(socket.getInputStream());
				
				if(s.hasNext()){
					System.out.println(s.nextLine());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
