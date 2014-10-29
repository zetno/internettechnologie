package nl.saxion.model;

import org.json.JSONObject;

public class Message {
	private String action;
	private JSONObject message;
	
	public Message(String action, JSONObject message){
		this.action = action;
		this.message = message;
	}
	
	public String getAction(){
		return action;
	}
	
	public JSONObject getMessage(){
		return message;
	}
}
