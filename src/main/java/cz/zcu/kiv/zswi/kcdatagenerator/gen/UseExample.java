package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;
import java.util.List;

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
		g.generate(3);

		//ulozeni a vypis chyb
		for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : g.save()) {
			System.out.println(e);
		}

		//vypis uzivatelu
		List<GeneratedUser> users = g.getUsers();
		for (GeneratedUser gu : users) {
			System.out.println(gu.getFirstName() + " " + gu.getLastName() + " " + " " + gu.getUsername() + ":" + gu.getPassword());
		}

		//generovani emailu (WIP) - uz to funguje, ale pozor na port

		String domainName = "localhost";
		EmailGenerator eg = new EmailGenerator("http://localhost:81/Ews/Exchange.asmx", users, domainName);
		eg.generateAndSave(5, 1, true, true, true, true);
		eg.generateAndSave(5, 1, true, true, true, true);

	}

}
