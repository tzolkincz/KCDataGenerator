package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UsersGeneratorIT extends BaseTest {

	ApiClient connectApiClient;

	public UsersGeneratorIT() throws IOException {
		super();
	}

	@Before
	public void setUp() {
		connectApiClient = getConnectApiClient();
	}

	/**
	 * Test of generate method, of class UsersGenerator.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testGenerate() throws IOException {
		UsersGenerator generator = new UsersGenerator(connectApiClient, getDomainId(), mockNameGenerator());
		generator.generate(1);

		GeneratedUser user = generator.getUsers().get(0);

		assertEquals("TEST", user.getFirstName());
		assertEquals("TEST", user.getLastName());
		assertThat(user.getUsername().length(), Matchers.greaterThan(3));
		assertThat(user.getUserAddr(testProperties.getProperty("domain")).length(), Matchers.greaterThan(5));
	}

	/**
	 * Test of save method, of class UsersGenerator.
	 *
	 * @throws java.io.IOException
	 */
	@Test
	public void testSave() throws IOException {
		UsersGenerator generator = new UsersGenerator(connectApiClient, getDomainId(), mockNameGenerator());
		generator.generate(1);

		for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : generator.save()) {
			assertNull(e);
		}
	}

}
