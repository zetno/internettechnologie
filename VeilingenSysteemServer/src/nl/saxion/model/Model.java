package nl.saxion.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Model {
	private static Model model;
	
	public static Model getInstance(){
		if(model == null){
			model = new Model();
		}
		
		return model;
	}
	
	private Model(){
		loadDummyData();
	}
	
	private List<Socket> clientSockets = new ArrayList<Socket>();
	private List<Auction> currentAuctions = new ArrayList<Auction>();
	private List<User> users = new ArrayList<User>();
	
	private void loadDummyData(){
		users.add(new User("bob", "wachtwoord"));
		users.add(new User("rendy", "1234"));
		users.add(new User("tim", "planken"));
		
		currentAuctions.add(new Auction(1, "Bike", 0, 0, 1415404800));
		currentAuctions.add(new Auction(2, "Bike", 0, 0, 1415404800));
	}
	
	public String authorizeUser(String username, String password) {
		String token = "";
		
		for (User user : users) {
			if(username.equals(user.getUsername())
					&& password.equals(user.getPassword())){
				token = generateAccessToken();
				user.setAccesstoken(token);
			}
		}
		
		return token;
	}
	
	public String generateAccessToken(){
		String token;
		
		boolean isNotUnique = true;
		do {
			token = Integer.toString((int) (Math.random() * 1000000));
			
			for (User user : users) {
				if(token.equals(user.getAccesstoken())){
					isNotUnique = false;
				}
			}
		} while(isNotUnique);
		
		return token;
	}
	
	public List<Socket> getClientSockets(){
		return clientSockets;
	}
}
