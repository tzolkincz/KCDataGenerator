package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import java.util.List;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.Test;
import static org.junit.Assert.*;

public class ContactGeneratorTest extends BaseTest {

	public ContactGeneratorTest() throws IOException {
		super();
	}

	/**
	 * Test of generateAndSave method, of class ContactGenerator.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGenerateAndSave() throws Exception {
		ContactGenerator generator = new ContactGenerator(
				testProperties.getProperty("ewsUrl"),
				mockGeneratedUsersList(),
				testProperties.getProperty("domain"));
		generator.setLoadStateAfterSave(true);

		List<Contact> contacts = generator.generateAndSave(1);

		assertThat(contacts.get(0).getDisplayName().length(), greaterThan(2));
		assertThat(contacts.get(0).getEmailAddresses().toString().length(), greaterThan(2));
		assertThat(contacts.get(0).getSurname().length(), greaterThan(2));
	}

}
