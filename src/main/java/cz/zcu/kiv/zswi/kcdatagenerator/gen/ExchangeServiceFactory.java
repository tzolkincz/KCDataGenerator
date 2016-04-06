package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.net.URI;
import java.net.URISyntaxException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.credential.WebCredentials;

public class ExchangeServiceFactory {

	public static ExchangeService create(String exchangeUrl, String userAddress, String passwd) throws URISyntaxException {
		ExchangeService service = new ExchangeService();
		service.setUrl(new URI(exchangeUrl));
		service.setCredentials(new WebCredentials(userAddress, passwd));
		return service;
	}

	public static ExchangeService create(String exchangeUrl, GeneratedUser u, String domain) throws URISyntaxException {
		return create(exchangeUrl, u.getUserAddr(domain), u.getPassword());
	}

}
