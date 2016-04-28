package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExchangeServiceFactoryIT extends BaseTest {

	public ExchangeServiceFactoryIT() throws IOException {
		super();
	}

	/**
	 * Test of create method, of class ExchangeServiceFactory.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testCreate() throws Exception {
		ExchangeService service = ExchangeServiceFactory.create(
				testProperties.getProperty("ewsUrl"),
				testProperties.getProperty("adminUserLogin"),
				testProperties.getProperty("adminUserPassword"));

		service.validate();

		assertNotNull(service);
	}

	/**
	 * Test of create method, of class ExchangeServiceFactory.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testCreateUserInstanceParam() throws Exception {
		ExchangeService service = ExchangeServiceFactory.create(
				testProperties.getProperty("ewsUrl"),
				new GeneratedUser("First", "Last",
						testProperties.getProperty("adminUserPassword"),
						testProperties.getProperty("adminUserLogin")),
				testProperties.getProperty("domain"));

		service.validate();

		assertNotNull(service);
	}
}
