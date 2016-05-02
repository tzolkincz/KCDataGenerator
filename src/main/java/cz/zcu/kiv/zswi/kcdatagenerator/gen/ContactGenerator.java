package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.EmailAddressKey;
import microsoft.exchange.webservices.data.core.enumeration.property.PhysicalAddressKey;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.PhysicalAddressEntry;

public class ContactGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private final NameGenerator nameGenerator;
	private final List<Contact> generatedContacts;
	private boolean loadStateAfterSave = false;

	@Deprecated
	public ContactGenerator(String exchangeUrl, List<GeneratedUser> users, String domain) throws IOException {
		this(exchangeUrl, users, domain, new NameGenerator(null, null));
	}

	public ContactGenerator(String exchangeUrl, List<GeneratedUser> users,
			String domain, NameGenerator nameGenerator) throws IOException {

		this.exchangeUrl = exchangeUrl;
		this.users = users;
		this.domain = domain;
		this.nameGenerator = nameGenerator;
		generatedContacts = new ArrayList<>();
	}

	public List<Contact> generateAndSave(int count) throws URISyntaxException, Exception {
		for (GeneratedUser user : users) {
			try (ExchangeService service = ExchangeServiceFactory.create(exchangeUrl, user, domain)) {
				for (int i = 0; i < count; i++) {
					Contact contact = new Contact(service);
					String firstname = nameGenerator.getFirstName();
					String lastname = nameGenerator.getLastName();

					EmailAddress email = new EmailAddress();
					email.setAddress(nameGenerator.getLogin(firstname + lastname) + "@example.com");

					contact.setGivenName(firstname);
					contact.setSurname(lastname);
					contact.setSubject("Contact Details");
					contact.getEmailAddresses().setEmailAddress(EmailAddressKey.EmailAddress1, email);

					// Specify the company name.
					contact.setCompanyName("technolgies");
					PhysicalAddressEntry paEntry1 = new PhysicalAddressEntry();
					paEntry1.setStreet("12345 Main Street");
					paEntry1.setCity("Seattle");
					paEntry1.setState("orissa");
					paEntry1.setPostalCode("11111");
					paEntry1.setCountryOrRegion("INDIA");
					contact.getPhysicalAddresses().setPhysicalAddress(PhysicalAddressKey.Home, paEntry1);
					contact.save();

					if (loadStateAfterSave) {
						contact.load();
					}

					generatedContacts.add(contact);
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return generatedContacts;
	}

	/**
	 * Load state for unit tests. Cant access generated properties without that. Load state needs active ews
	 * service connection
	 *
	 * @param loadStateAfterSave
	 */
	public void setLoadStateAfterSave(boolean loadStateAfterSave) {
		this.loadStateAfterSave = loadStateAfterSave;
	}

}
