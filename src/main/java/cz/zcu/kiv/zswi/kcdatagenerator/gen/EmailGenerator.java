package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.MimeContent;
import microsoft.exchange.webservices.data.search.FolderView;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;

public class EmailGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private final List<String> folders = new ArrayList<>();

	public static final double READED_PROBABILITY = 0.95;
	public static final double ATTACHMENT_PROBABILITY = 0.05;
	public static final String DEFAULT_ATTACHMENT_PATH = "/attachments/";

	public EmailGenerator(String exchangeUrl, List<GeneratedUser> users, String domain) {
		this.exchangeUrl = exchangeUrl;
		this.users = users;
		this.domain = domain;
		setFoldersMap();
	}

	/**
	 *
	 * @param count	count of emails to generate
	 * @param foldersProbability	probality of user is using folders
	 * @param flags	generate emails with flags (read, unread, flags, replied, fwd)
	 * @param randCharsets	generate emails with random charset
	 * @param attachments	emails with attachments
	 * @param externalSender	set external sender in email headers
	 * @throws Exception
	 */
	public void generateAndSave(int count, double foldersProbability, boolean flags,
			boolean randCharsets, boolean attachments, boolean externalSender) throws Exception {
		if (foldersProbability < 0 && foldersProbability > 1) {
			throw new IllegalArgumentException("Folders Probability must be set between  0 and 1 inclusive");
		}

		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<Exception>> results = new ArrayList<>();

		for (GeneratedUser user : users) {
			Future<Exception> res = threadPool.submit(() -> {
				try (ExchangeService service = new ExchangeService()) {
					service.setUrl(new URI(exchangeUrl));
					service.setCredentials(new WebCredentials(getUserAddr(user), user.getPassword()));

					List<FolderId> usersFolders = createFolders(foldersProbability, service);

					for (int i = 0; i < count; i++) {
						EmailMessage msg = createMessage(
								service, user, flags, randCharsets, attachments, externalSender);
						msg.save(usersFolders.get(i % usersFolders.size()));
					}
				} catch (Exception e) {
					return e;
				}
				return null;
			});
			results.add(res);
		}

		long ts = System.currentTimeMillis();
		threadPool.shutdown();
		threadPool.awaitTermination(10, TimeUnit.DAYS);

		for (Future<Exception> f : results) {
			if (f.get() != null) {
				throw new Exception(f.get());
			}
		}
		System.out.println("Emails saved in: " + (System.currentTimeMillis() - ts) + " ms");

	}

	private Folder findFolderByName(String f, ArrayList<Folder> currentFolders) throws ServiceLocalException {
		for (Folder cf : currentFolders) {
			if (cf.getDisplayName().equals(f)) {
				return cf;
			}
		}
		return null;
	}

	private String getSender() {
		int recipientOffset = (int) (Math.random() * users.size());
		return users.get(recipientOffset).getUsername() + "@" + domain;
	}

	private void setFoldersMap() {
		folders.add("Important");
		folders.add("Social Networks");
//		folders.add("Personal");
//		folders.add("Work");
//		folders.add("Invoices");
//		folders.add("Hobby");
//		folders.add("Family");
	}

	private List<FolderId> createFolders(double foldersProbability, ExchangeService service) throws Exception {
		List<FolderId> usersFolders = new ArrayList<>();
		usersFolders.add(new FolderId(WellKnownFolderName.Inbox));

		if (Math.random() <= foldersProbability) {
			ArrayList<Folder> currentFolders = Folder.bind(service, WellKnownFolderName.Inbox)
					.findFolders(new FolderView(30)).getFolders();

			for (String f : folders) {
				Folder current = findFolderByName(f, currentFolders);
				if (current == null) {
					Folder folder = new Folder(service);
					folder.setDisplayName(f);

					folder.save(WellKnownFolderName.Inbox);
					usersFolders.add(folder.getId());
				} else {
					usersFolders.add(current.getId());
				}
			}
		}
		return usersFolders;
	}

	private EmailMessage createMessage(ExchangeService service, GeneratedUser user, boolean flags,
			boolean randCharsets, boolean attachments, boolean externalSender) throws Exception {

		MultiPartEmail email = new MultiPartEmail();
		email.setHostName("smtp." + domain); //lib just need this param set

		email.setFrom(getSender());
		email.setSubject(getSubject());
		email.setMsg(getEmailText());
		email.addTo(getUserAddr(user));
		email.buildMimeMessage();

		if (attachments) {
			if (Math.random() < ATTACHMENT_PROBABILITY) {
				email.attach(createAttachment());
			}
		}

		EmailMessage msg = new EmailMessage(service);
		msg.setMimeContent(new MimeContent("utf-8", emailToBytes(email)));
		if (Math.random() < READED_PROBABILITY) {
			msg.setIsRead(true);
		}

		return msg;
	}

	private String getUserAddr(GeneratedUser u) {
		return u.getUsername() + "@" + domain;
	}

	private byte[] emailToBytes(Email email) throws IOException, MessagingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		email.getMimeMessage().writeTo(stream);
		return stream.toByteArray();
	}

	private String getSubject() {
		return "Default email subject";
	}

	private String getEmailText() {
		return "This is a test mail ... :-)";
	}

	private EmailAttachment createAttachment() throws MalformedURLException {
		EmailAttachment attachment = new EmailAttachment();

		List<String> attachments = new ArrayList<>();
		attachments.add("files.zip");
		attachments.add("example.jpg");
		attachments.add("img.png");

		String randAttach = attachments.get((int)(Math.random() * attachments.size()));

		attachment.setPath(getClass().getResource(DEFAULT_ATTACHMENT_PATH + randAttach).getPath());
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		attachment.setDescription("Attachment");
		attachment.setName(randAttach);

		return attachment;
	}
}
