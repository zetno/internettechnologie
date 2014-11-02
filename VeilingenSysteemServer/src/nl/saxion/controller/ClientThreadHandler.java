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

/**
 * A class with statis methods that helps the clientthread
 * 
 * @author eelcokruders
 *
 */
public class ClientThreadHandler {

	/**
	 * A incoming message parser
	 * @param socket is the socket to be parsed the message from
	 * @return a new Message containing the action and message content.
	 * @throws JSONException
	 * @throws IOException
	 * @throws BadInputException
	 */
	public static Message messageToAction(Socket socket) throws JSONException, IOException, BadInputException{
		BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		String content = in.readLine();
		
		if(content == null){
			return null;
		}
		
		return parseJson(content);
	}
	
	/**
	 * Json parser to Message
	 * @param json
	 * @return a new Message containing the action and message content.
	 * @throws JSONException
	 * @throws BadInputException
	 */
	private static Message parseJson(String json) throws JSONException, BadInputException {
		if (json != null && !json.isEmpty() && isJSONValid(json)) {
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
	
	/**
	 * A json checker if it's valid json
	 * @param test is the String to be validied
	 * @return true if it's valid json
	 */
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
	
	/**
	 * An epoch converter
	 * @param hours
	 * @return
	 */
	public static long createEpochDate(int hours){
		return System.currentTimeMillis() + 3600L * (long) hours;
	}
}