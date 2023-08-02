package model;

public class User {
	private String username;
	private String password;
	private String chatName;

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		this.chatName = "";
	}

	public User(String username, String password, String chatName) {
		super();
		this.username = username;
		this.password = password;
		this.chatName = chatName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getChatName() {
		return chatName;
	}

	public void setChatName(String chatName) {
		this.chatName = chatName;
	}

}
