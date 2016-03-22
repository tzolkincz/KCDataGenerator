package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;
import java.net.URI;
import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

public class UseExample {

	public static void main(String[] args) throws Exception {

		// pripojeni api clienta
		ApiClient client = new ApiClient();
		client.login("http://localhost:4040", "admin", "xxxxxx");


		//vyber domeny
		Domain[] domains = client.getApi(Domains.class).get(new SearchQuery()).getList();

		String domainId = "";
		for (Domain domain : domains) {
			System.out.println(" => " + domain.getName());
			System.out.println(" => " + domain.getId());
			domainId = domain.getId();
		}

		//generovani uzivatelu
		//NameGenerator prijma jako argumenty cesty ke slovnikum (fist, last names).
		//  Kdyz je null, pouziji se implicitni slovniky
		UsersGenerator g = new UsersGenerator(client, domainId, new NameGenerator(null, null));
		g.generate(5);

		//ulozeni a vypis chyb
		for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : g.save()) {
			System.out.println(e);
		}

		//vypis uzivatelu
		List<GeneratedUser> users = g.getUsers();
		for(GeneratedUser gu: users) {
			System.out.println(gu.getFirstName() + " " + gu.getLastName() + " " + gu.getPassword());
		}


		//generovani emailu (WIP) - uz to funguje, ale pozor na port
		/*

		List<String> userLogins = g.getUserLogins();

		ExchangeService service = new ExchangeService();
		ExchangeCredentials credentials = new WebCredentials("admin@localhost", "xxxxxx");
		service.setUrl(new URI("http://localhost:81/Ews/Exchange.asmx"));
		service.setCredentials(credentials);

		String domainName = "localhost";
		EmailGenerator eg = new EmailGenerator(service, userLogins, domainName);
		eg.generateAndSend(2);

		 */
	}

}
