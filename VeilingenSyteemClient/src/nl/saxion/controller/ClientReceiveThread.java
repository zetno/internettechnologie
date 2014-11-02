package nl.saxion.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import nl.saxion.model.Model;

public class ClientReceiveThread extends Thread {
	private Socket socket;

	public ClientReceiveThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		while (true) {
			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = in.readLine();
				Model.getInstance().parseAction(message);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	

}