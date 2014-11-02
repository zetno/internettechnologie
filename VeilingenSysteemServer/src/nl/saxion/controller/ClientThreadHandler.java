package nl.saxion.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.saxion.controller.exceptions.BadInputException;
import nl.saxion.model.Message;

public class ClientThreadHandler {

	public static Message messageToAction(Socket socket) throws JSONException, IOException, BadInputException{
		BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		String content = in.readLine();
		System.out.println(content);
		return parseJson(content);
	}
	
	private static Message parseJson(String json) throws JSONException, BadInputException {
		if (!json.isEmpty() && isJSONValid(json)) {
			JSONObject jsonMessage = new JSONObject(json);
			String action = jsonMessage.getString("action");
			
			if(jsonMessage.optJSONObject("message") != null){
				JSONObject content = jsonMessage.getJSONObject("message");
				return new Message(action, content.toString());
			}else if(jsonMessage.optString("message") != null){
				String content = jsonMessage.optString("message");
				return new Message(action, content.toString());
			}
			
			throw new BadInputException();
			
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
	
	public static int createEpochDate(int hours){
		System.out.println("long: " + System.currentTimeMillis());
		System.out.println("ing: " + (int) System.currentTimeMillis());
		
		
		return hours;
	}
}