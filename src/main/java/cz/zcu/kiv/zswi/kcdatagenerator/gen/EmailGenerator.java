package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.MimeContent;

public class EmailGenerator {

	private final ExchangeService service;
	private final List<String> userLogins;
	private final String domain;

	public EmailGenerator(ExchangeService service, List<String> userLogins, String domain) {
		this.service = service;
		this.userLogins = userLogins;
		this.domain = domain;
	}

	public void generateAndSend(int count) throws Exception {

		for (String userLogin : userLogins) {
			EmailAddress sender = new EmailAddress(userLogin + "@" + domain);

			for (int i = 0; i < count; i++) {
				EmailMessage msg = new EmailMessage(service);
				msg.setSubject("Hello world!");
				msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Java API."));
				msg.getToRecipients().add(getRecipient());
				msg.setSender(sender);
				msg.setFrom(sender);

				String mime = ""
						+ "From: Administrator <admin@localhost>\n"
						+ "To: <" + getRecipient() + ">\n"
						+ "MIME-Version: 1.0\n"
						+ "Content-Type: multipart/mixed;\n"
						+ "        boundary=\"XXXXboundary text\"\n"
						+ "\n"
						+ "This is a multipart message in MIME format.\n"
						+ "\n"
						+ "--XXXXboundary text \n"
						+ "Content-Type: text/plain\n"
						+ "\n"
						+ "this is the body text\n"
						+ "\n"
						+ "--XXXXboundary text \n"
						+ "Content-Type: text/plain;\n"
						+ "Content-Disposition: attachment;\n"
						+ "        filename=\"test.txt\"\n"
						+ "\n"
						+ "this is the attachment text\n"
						+ "\n"
						+ "--XXXXboundary text--\n";

				msg.setMimeContent(new MimeContent("utf-8", mime.getBytes()));
				msg.sendAndSaveCopy();

			}
		}

		service.close();
	}

	private String getRecipient() {
		int recipientOffset = (int) (Math.random() * userLogins.size());
		return userLogins.get(recipientOffset) + "@" + domain;
	}
}
