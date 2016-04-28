package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import java.util.List;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventGeneratorIT extends BaseTest {

	public EventGeneratorIT() throws IOException {
		super();
	}

	/**
	 * Test of generateAndSave method, of class EventGenerator.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGenerateAndSave() throws Exception {
		EventGenerator generator = new EventGenerator(
				testProperties.getProperty("ewsUrl"),
				mockGeneratedUsersList(),
				testProperties.getProperty("domain"));

		List<Appointment> events = generator.generateAndSave(1, true, true, true, true, true, true);
		assertThat(events.get(0).getSubject().length(), greaterThan(2));
		assertNotNull(events.get(0).getStart());
		assertNotNull(events.get(0).getEnd());
	}

}
