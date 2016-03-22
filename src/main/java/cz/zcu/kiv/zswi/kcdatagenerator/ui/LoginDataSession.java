package cz.zcu.kiv.zswi.kcdatagenerator.ui;

public class LoginDataSession {

	private LoginData loginData;

	private static LoginDataSession instance;
	
	private LoginDataSession() {
		
	}
	
	/**
	 * @return the loginData
	 */
	public LoginData getLoginData() {
		return loginData;
	}

	/**
	 * @param loginData the loginData to set
	 */
	public void setLoginData(LoginData loginData) {
		this.loginData = loginData;
	}
	
	public static LoginDataSession getInstance() {
		if(instance == null) {
			instance = new LoginDataSession();
		}
		
		return instance;
	}
}
