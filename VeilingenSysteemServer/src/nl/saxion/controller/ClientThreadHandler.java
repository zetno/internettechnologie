package nl.saxion.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.saxion.model.Message;

public class ClientThreadHandler {

	public static Message messageToAction(Socket socket) throws JSONException, IOException, BadInputException{
		Scanner s = new Scanner(socket.getInputStream());
		
		if (s.hasNext()) {
			String content = s.nextLine();
			return parseJson(content);
		}
		
		return null;
	}
	
	private static Message parseJson(String json) throws JSONException, BadInputException {
		if (!json.isEmpty() && isJSONValid(json)) {
			JSONObject jsonMessage = new JSONObject(json);
			String action = jsonMessage.getString("action");
			JSONObject content = jsonMessage.getJSONObject("message");
			
			return new Message(action, content);
		}else{
			throw new BadInputException();
		}
	}
	
	private static boolean isJSONValid(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException ex) {
	        try {
	            new JSONArray(test);
	        } catch (JSONException ex1) {
	            return false;
	        }
	    }
	    return true;
	}
}