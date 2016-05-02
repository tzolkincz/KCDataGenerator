package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class NameGeneratorTest extends BaseTest {

	NameGenerator nameGenerator;

	public NameGeneratorTest() throws IOException {
		super();
	}

	@Before
	public void setUp() throws IOException {
		nameGenerator = new NameGenerator(null, null);
	}

	/**
	 * Test of getLastName method, of class NameGenerator.
	 */
	@Test
	public void testGetLastName() {
		String lastname = nameGenerator.getLastName();
		assertThat(lastname.length(), greaterThan(1));
	}

	/**
	 * Test of getFirstName method, of class NameGenerator.
	 */
	@Test
	public void testGetFirstName() {
		String firstname = nameGenerator.getFirstName();
		assertThat(firstname.length(), greaterThan(1));
	}

	/**
	 * Test of getLogin method, of class NameGenerator.
	 */
	@Test
	public void testGetLogin() {
		String login = nameGenerator.getLogin("foo");
		assertThat(login.length(), greaterThan(2));
	}

	/**
	 * Test of getRandomLogin method, of class NameGenerator.
	 */
	@Test
	public void testGetRandomLogin() {
		String login = nameGenerator.getRandomLogin();
		assertThat(login.length(), greaterThan(2));
	}

	@Test
	public void testExternalDicts() throws IOException {

		final String testName = "TEST";

		File tempFile = folder.newFile("names");
		PrintStream out = new PrintStream(new FileOutputStream(tempFile));
		out.print(testName);

		NameGenerator generator = new NameGenerator(tempFile.toPath(), tempFile.toPath());

		assertEquals(testName, generator.getFirstName());
		assertEquals(testName, generator.getLastName());

	}

}
