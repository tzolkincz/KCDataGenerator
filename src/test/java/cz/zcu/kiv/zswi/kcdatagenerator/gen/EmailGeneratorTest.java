package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import java.util.List;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmailGeneratorTest extends BaseTest {

	public EmailGeneratorTest() throws IOException {
		super();
	}

	/**
	 * Test of generateAndSave method, of class EmailGenerator.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGenerateAndSave() throws Exception {
		EmailGenerator generator = new EmailGenerator(
				testProperties.getProperty("ewsUrl"),
				mockGeneratedUsersList(),
				testProperties.getProperty("domain"));

		generator.setLoadStateAfterSave(true);

		List<EmailMessage> emails = generator.generateAndSave(1, 1.0, true, true, true, true);

		EmailMessage email = emails.get(0);
		assertThat(email.getSubject().length(), greaterThan(2));
		assertThat(email.getFrom().toString().length(), greaterThan(2));
		assertThat(email.getToRecipients().getCount(), greaterThan(0));
		assertThat(email.getMimeContent().toString().length(), greaterThan(2));
	}

}
