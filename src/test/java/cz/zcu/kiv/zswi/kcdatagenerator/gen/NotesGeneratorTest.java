package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import java.util.List;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.Test;
import static org.junit.Assert.*;

public class NotesGeneratorTest extends BaseTest {

	public NotesGeneratorTest() throws IOException {
		super();
	}

	/**
	 * Test of generateAndSave method, of class NotesGenerator.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGenerateAndSave() throws Exception {
		NotesGenerator generator = new NotesGenerator(
				testProperties.getProperty("ewsUrl"),
				mockGeneratedUsersList(),
				testProperties.getProperty("domain"));

		List<EmailMessage> tasks = generator.generateAndSave(1);
		assertThat(tasks.get(0).getSubject().length(), greaterThan(2));
		assertThat(tasks.get(0).getMimeContent().toString().length(), greaterThan(2));
	}

}
