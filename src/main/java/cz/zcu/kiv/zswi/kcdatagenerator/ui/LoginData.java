package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ApiClient;

/**
 * Class holds important data of logged user.
 * @author Daniel Holubář
 *
 */
public class LoginData {

	/**
	 * Connector with ews api.
	 */
    public ApiClient client;
    
    /**
     * Id of domain.
     */
    public String domainName;
    
    /**
     * Username of logged user.
     */
    public String username;
    
    /**
     * Password of logged user.
     */
    public String password;

    /**
     * Constructor.
     * @param client Api client
     * @param domainName domain name
     * @param username username of logged user
     * @param password password of logged user
     */
    public LoginData(ApiClient client, String domainName, String username, String password) {
        this.client = client;
        this.domainName = domainName;
        this.username = username;
        this.password = password;
    }
}
