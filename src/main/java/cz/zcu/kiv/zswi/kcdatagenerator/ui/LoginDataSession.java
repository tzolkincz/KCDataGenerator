package cz.zcu.kiv.zswi.kcdatagenerator.ui;

/**
 * Container for login data of user.
 * @author Daniel Holubář
 */
public class LoginDataSession {

	/**
	 * Crate with login data
	 */
	private LoginData loginData;

	/**
	 * Holds instance of this class.
	 */
	private static LoginDataSession instance;
	
	/**
	 * Privates constructor.
	 */
	private LoginDataSession() {
		
	}
	
	/**
	 * Getter for login data.
	 * @return the loginData
	 */
	public LoginData getLoginData() {
		return loginData;
	}

	/**
	 * Setter for login data.
	 * @param loginData the loginData to set.
	 */
	public void setLoginData(LoginData loginData) {
		this.loginData = loginData;
	}
	
	/**
	 * Returns instance of this class.
	 * @return instance of LoginDataSession
	 */
	public static LoginDataSession getInstance() {
		if(instance == null) {
			instance = new LoginDataSession();
		}
		
		return instance;
	}
}
