package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import java.util.List;
import microsoft.exchange.webservices.data.core.service.item.Task;
import static org.hamcrest.Matchers.greaterThan;
import org.junit.Test;
import static org.junit.Assert.*;

public class TaskGeneratorIT extends BaseTest {

	public TaskGeneratorIT() throws IOException {
		super();
	}

	/**
	 * Test of generateAndSave method, of class TaskGenerator.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGenerateAndSave() throws Exception {
		TaskGenerator generator = new TaskGenerator(
				testProperties.getProperty("ewsUrl"),
				mockGeneratedUsersList(),
				testProperties.getProperty("domain"));

		List<Task> tasks = generator.generateAndSave(1);
		assertThat(tasks.get(0).getSubject().length(), greaterThan(2));
		assertNotNull(tasks.get(0).getDueDate());
	}

}
