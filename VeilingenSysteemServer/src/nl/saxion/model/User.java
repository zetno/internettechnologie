package nl.saxion.model;

public class User {
	private String accesstoken, username, password;

	/**
	 * Make a user
	 * 
	 * @param accesstoken
	 *            accesstoken of user
	 * @param username
	 *            unique username
	 * @param password
	 *            password
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void setAccesstoken(String accesstoken){
		this.accesstoken = accesstoken;
	}
	
	public String getAccesstoken() {
		return accesstoken;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String toString(){
		return "accesstoken: "+accesstoken + " with usename "+ username;
	}

}
