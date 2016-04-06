package cz.zcu.kiv.zswi.kcdatagenerator.gen;

public class GeneratedUser {

	private final String firstName;
	private final String lastName;
	private final String password;
	private final String username;

	public GeneratedUser(String firstName, String lastName, String password, String username) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public String getUserAddr(String domain) {
		return getUsername() + "@" + domain;
	}
}
