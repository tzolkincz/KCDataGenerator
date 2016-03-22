package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ApiClient;

public class LoginData {
	
	public ApiClient client;
	public String domainId;
	
	public LoginData(ApiClient client, String domainId) {
		this.client = client;
		this.domainId = domainId;
	}
}
