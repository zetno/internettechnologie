package nl.saxion.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
	private String action;
	private String jsonMessage;
	
	public Message(String action, String message){
		this.action = action;
		this.jsonMessage = message;
	}
	
	public String getAction(){
		return action;
	}
	
	public JSONObject getJSONObjectMessage() throws JSONException{
		return new JSONObject(jsonMessage);
	}
	
	public String getStringMessage(){
		return jsonMessage;
	}
}
