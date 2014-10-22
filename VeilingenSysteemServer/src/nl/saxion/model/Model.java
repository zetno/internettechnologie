package nl.saxion.model;

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
	
	private List<Auction> currentAuctions = new ArrayList<Auction>();
	private List<User> users = new ArrayList<User>();
	
	private void loadDummyData(){
		users.add(new User("Bobbie van Dalen", "bob", "wachtwoord"));
		users.add(new User("Rendy Hoog", "rendy", "1234"));
		users.add(new User("Tom Timmerman", "tim", "planken"));
		
		currentAuctions.add(new Auction(0, "Bike", 0, 0, 1415404800));
		currentAuctions.add(new Auction(0, "Bike", 0, 0, 1415404800));
	}
}
