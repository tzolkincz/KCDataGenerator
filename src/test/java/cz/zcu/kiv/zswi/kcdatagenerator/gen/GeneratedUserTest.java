package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GeneratedUserTest {

	GeneratedUser user;
	private static final String USERNAME = "username";
	private static final String FIRSTNAME = "firstname";
	private static final String LASTNAME = "lastname";
	private static final String PASSWORD = "passoword";

	@Before
	public void setUp() {
		user = new GeneratedUser(FIRSTNAME, LASTNAME, PASSWORD, USERNAME);
	}

	/**
	 * Test of getFirstName method, of class GeneratedUser.
	 */
	@Test
	public void testGetFirstName() {
		assertEquals(user.getFirstName(), FIRSTNAME);
	}

	/**
	 * Test of getLastName method, of class GeneratedUser.
	 */
	@Test
	public void testGetLastName() {
		assertEquals(user.getLastName(), LASTNAME);
	}

	/**
	 * Test of getPassword method, of class GeneratedUser.
	 */
	@Test
	public void testGetPassword() {
		assertEquals(user.getPassword(), PASSWORD);
	}

	/**
	 * Test of getUsername method, of class GeneratedUser.
	 */
	@Test
	public void testGetUsername() {
		assertEquals(user.getUsername(), USERNAME);
	}

	/**
	 * Test of getUserAddr method, of class GeneratedUser.
	 */
	@Test
	public void testGetUserAddr() {
		assertEquals(user.getUserAddr("domain"), USERNAME + "@domain");
	}

}
