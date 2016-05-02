package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ApiClient;

public class LoginData {

    public ApiClient client;
    public String domainId;
    public String username;
    public String password;

    public LoginData(ApiClient client, String domainId, String username, String password) {
        this.client = client;
        this.domainId = domainId;
        this.username = username;
        this.password = password;
    }
}
