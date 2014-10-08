package nl.saxion.model;

public class User {
	private String name, username, password;

	/**
	 * Make a user
	 * 
	 * @param name
	 *            name of user
	 * @param username
	 *            unique username
	 * @param password
	 *            password
	 */
	public User(String name, String username, String password) {
		super();
		this.name = name;
		this.username = username;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String toString(){
		return "User: "+name + " with usename "+ username;
	}

}
