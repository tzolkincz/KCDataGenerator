package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import microsoft.exchange.webservices.data.core.ExchangeService;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class BaseTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	Properties testProperties = new Properties();

	public BaseTest() throws IOException {

		//read config
		testProperties.load(getClass().getClassLoader().getResourceAsStream("test.properties"));

	}

	public ExchangeService getExchangeService() throws URISyntaxException {
		return ExchangeServiceFactory.create(
				testProperties.getProperty("ewsUrl"),
				testProperties.getProperty("adminUserLogin"),
				testProperties.getProperty("adminUserPassword"));
	}

	public ApiClient getConnectApiClient() {
		ApiClient client = new ApiClient();
		client.login(
				testProperties.getProperty("connectApiUrl"),
				testProperties.getProperty("adminUserLogin"),
				testProperties.getProperty("adminUserPassword"));
		return client;
	}

	public String getDomainId() {
		Domain[] domains = getConnectApiClient().getApi(Domains.class).get(new SearchQuery()).getList();

		for (Domain domain : domains) {
			if (domain.getName().equals(testProperties.getProperty("domain"))) {
				return domain.getId();
			}
		}

		throw new IllegalStateException("domain specified as a test domain is "
				+ "not present at Kerio Connect Server");
	}

	public NameGenerator mockNameGenerator() throws IOException {
		final String testName = "TEST";

		File tempFile = folder.newFile("names");
		PrintStream out = new PrintStream(new FileOutputStream(tempFile));
		out.print(testName);

		return new NameGenerator(tempFile.toPath(), tempFile.toPath());
	}

	public List<GeneratedUser> mockGeneratedUsersList() throws IOException {
		UsersGenerator ug = new UsersGenerator(getConnectApiClient(), getDomainId(), mockNameGenerator());
		ug.generate(1);
		ug.save();
		return ug.getUsers();
	}
}
