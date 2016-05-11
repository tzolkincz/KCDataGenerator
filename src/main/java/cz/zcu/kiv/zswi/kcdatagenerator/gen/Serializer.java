package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.random;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MimeContent;

public class Serializer {

	public static final double EXTERNAL_SENDER_PROB = 0.2;

	/**
	 * gets an eml message, change headers and save througth ews to exchange server
	 *
	 * @param emlFileContent - eml as string
	 * @param users - users generated in previous step
	 * @param ewsUrl - connection url for ews
	 * @param domain - domain
	 * @param folder - which folder to import this message
	 * @throws Exception
	 */
	public void sendEmlToServer(String emlFileContent, List<GeneratedUser> users,
			String ewsUrl, String domain, WellKnownFolderName folder)
			throws Exception {

		GeneratedUser to = getTo(users);

		MimeMessage javaMailMessage = parseToJavaMailMessage(emlFileContent);
		javaMailMessage.setFrom(getFrom(users, domain));
		javaMailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to.getUserAddr(domain)));

		EmailMessage ewsMessage = createEwsMessage(javaMailMessage, ewsUrl, to, domain);

		ewsMessage.save(folder);
	}

	private MimeMessage parseToJavaMailMessage(String emlFileContent) throws MessagingException, IOException {

		Session s = Session.getDefaultInstance(new Properties());
		InputStream iss = new ByteArrayInputStream(emlFileContent.getBytes());
		MimeMessage message = new MimeMessage(s, iss);

		return message;
	}

	private String getFrom(List<GeneratedUser> users, String domain) {
		if (random() < EXTERNAL_SENDER_PROB) {
			return "external@example.com";
		}
		return users.get((int) (random() * users.size())).getUserAddr(domain);
	}

	private GeneratedUser getTo(List<GeneratedUser> users) {
		return users.get((int) (random() * users.size()));
	}

	private EmailMessage createEwsMessage(MimeMessage message, String ewsUrl, GeneratedUser user, String domain)
			throws Exception {

		ByteArrayOutputStream oss = new ByteArrayOutputStream();
		message.writeTo(oss);

		ExchangeService service = ExchangeServiceFactory.create(ewsUrl, user, domain);

		EmailMessage email = new EmailMessage(service);
		email.setMimeContent(new MimeContent("UTF-8", oss.toByteArray()));

		return email;
	}

}
