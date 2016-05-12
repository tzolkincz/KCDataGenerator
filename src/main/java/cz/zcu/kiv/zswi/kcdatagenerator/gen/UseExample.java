package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import cz.zcu.kiv.zswi.kcdatagenerator.imp.Serializer;
import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;
import java.util.List;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

public class UseExample {

	public static void main(String[] args) throws Exception {

		// pripojeni api clienta
		ApiClient client = new ApiClient();
		client.login("http://localhost:4040", "admin", "xxxxxx");
//		client.login("https://10.0.0.104:4040", "admin", "xxxxxx");

		//vyber domeny
		Domain[] domains = client.getApi(Domains.class).get(new SearchQuery()).getList();

		String domainId = "";
		for (Domain domain : domains) {
			System.out.println(" => " + domain.getName());
			System.out.println(" => " + domain.getId());
			domainId = domain.getId();
		}

		NameGenerator nameGenerator = new NameGenerator(null, null);

		//generovani uzivatelu
		//NameGenerator prijma jako argumenty cesty ke slovnikum (fist, last names).
		//  Kdyz je null, pouziji se implicitni slovniky
		UsersGenerator g = new UsersGenerator(client, domainId, nameGenerator);
		g.generate(1);

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
		String ewsUrl = "http://localhost:8800/Ews/Exchange.asmx";
//		String ewsUrl = "http://10.0.0.104:8800/Ews/Exchange.asmx";

//		users.clear();
//		users.add(new GeneratedUser("a", "b", "u", "dawsonevelyn30"));

		EmailGenerator eg = new EmailGenerator(ewsUrl, users, domainName);
		eg.generateAndSave(10, 1.0, true, true, true, true);

		ContactGenerator cg = new ContactGenerator(ewsUrl, users, domainName, nameGenerator);
		cg.generateAndSave(1);

		EventGenerator ceg = new EventGenerator(ewsUrl, users, domainName);
		ceg.generateAndSave(100, true, true, true, true, true, true, true);

		TaskGenerator tg = new TaskGenerator(ewsUrl, users, domainName);
		tg.generateAndSave(10, true);

		NotesGenerator ng = new NotesGenerator(ewsUrl, users, domainName);
		ng.generateAndSave(5, true);

		//serializace
		String emlFile = "Date: Wed, 11 May 2016 09:54:36 +0200\n"
				+ "Message-ID: <652427351.155.1462953276031.JavaMail.v@jlj>\n"
				+ "Subject: Imported eml email\n"
				+ "MIME-Version: 1.0\n"
				+ "Importance: Normal\n"
				+ "X-Priority: 3\n"
				+ "Thread-Index: AZ2x3tU+NzM1OTc0YWZmODIxZTFkNw==\n"
				+ "From: \"michalasvobodov23@localhost\" <michalasvobodov23@localhost>\n"
				+ "To: \"michalaern17@localhost\" <michalaern17@localhost>\n"
				+ "Content-Type: text/plain; charset=\"utf-8\"\n"
				+ "Content-Transfer-Encoding: base64\n"
				+ "\n"
				+ "0LTQsNCy0L3Ri9C8LdC00LDQstC90L4uINCx0YvQuyDQtNC10LQg0JzRgNCw0LcuINCa0L7QvdC1\n"
				+ "0YYuIChydSk=";

		Serializer serializer = new Serializer();
		serializer.sendEmlToServer(emlFile, users, ewsUrl, domainName, WellKnownFolderName.Inbox);
	}

}
