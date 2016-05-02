package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.permission.folder.FolderPermissionLevel;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.property.StandardUser;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.FolderPermission;
import microsoft.exchange.webservices.data.property.complex.MimeContent;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.FolderView;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;

public class EmailGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private final List<String> folders = new ArrayList<>();
	private final NameGenerator nameGenerator;
	private final List<EmailMessage> generatedEmails;
	private boolean loadStateAfterSave = false;

	public static final double READED_PROBABILITY = 0.95;
	public static final double EXTERNAL_SENDER_PROBABILITY = 0.15;
	public static final double ATTACHMENT_PROBABILITY = 0.05;
	public static final double FLAGS_PROBABILITY = 0.05;
	public static final double SHARED_FOLDER_PROBABILITY = 0.15;
	public static final String DEFAULT_ATTACHMENT_PATH = "/attachments/";

	public EmailGenerator(String exchangeUrl, List<GeneratedUser> users, String domain)
			throws IOException, URISyntaxException {

		this.exchangeUrl = exchangeUrl;
		this.users = users;
		this.domain = domain;
		this.nameGenerator = new NameGenerator(null, null);
		setFoldersMap();
		generatedEmails = Collections.synchronizedList(new ArrayList<EmailMessage>());
	}

	/**
	 *
	 * @param count	count of emails to generate
	 * @param foldersProbability	probality of user is using folders
	 * @param flags	generate emails with flags (read, unread, flags, replied, fwd)
	 * @param randCharsets	generate emails with random charset
	 * @param attachments	emails with attachments
	 * @param externalSender	set external sender in email headers
	 * @return list of generated messages
	 * @throws Exception
	 */
	public List<EmailMessage> generateAndSave(int count, double foldersProbability, boolean flags,
			boolean randCharsets, boolean attachments, boolean externalSender) throws Exception {
		if (foldersProbability < 0 && foldersProbability > 1) {
			throw new IllegalArgumentException("Folders Probability must be set between  0 and 1 inclusive");
		}

		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<Exception>> results = new ArrayList<>();

		for (GeneratedUser user : users) {
			Future<Exception> res = threadPool.submit(() -> {
				try (ExchangeService service = ExchangeServiceFactory.create(exchangeUrl, user, domain)) {
					List<FolderId> usersFolders = createFolders(foldersProbability, service);

					for (int i = 0; i < count; i++) {
						EmailMessage msg = createMessage(
								service, user, flags, randCharsets, attachments, externalSender);
						msg.save(usersFolders.get(i % usersFolders.size()));

						if (loadStateAfterSave) {
							msg.load();
						}
						generatedEmails.add(msg);
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

		return generatedEmails;
	}

	private Folder findFolderByName(String f, ArrayList<Folder> currentFolders) throws ServiceLocalException {
		for (Folder cf : currentFolders) {
			if (cf.getDisplayName().equals(f)) {
				return cf;
			}
		}
		return null;
	}

	private String getSender(boolean externalSender) {
		if (externalSender && Math.random() < EXTERNAL_SENDER_PROBABILITY) {
			//external sender
			return nameGenerator.getRandomLogin() + "@example.com";
		}
		int recipientOffset = (int) (Math.random() * users.size());
		return users.get(recipientOffset).getUsername() + "@" + domain;
	}

	private void setFoldersMap() {
		folders.add("Important");
		folders.add("Social Networks");
		folders.add("Personal");
		folders.add("Work");
		folders.add("Invoices");
		folders.add("Hobby");
		folders.add("Family");
	}

	private List<FolderId> createFolders(double foldersProbability, ExchangeService service) throws Exception {
		List<FolderId> usersFolders = new ArrayList<>();
		usersFolders.add(new FolderId(WellKnownFolderName.Inbox));
		usersFolders.add(new FolderId(WellKnownFolderName.JunkEmail));

		if (Math.random() <= foldersProbability) {
			ArrayList<Folder> currentFolders = Folder.bind(service, WellKnownFolderName.Inbox)
					.findFolders(new FolderView(30)).getFolders();

			for (String f : folders) {
				Folder current = findFolderByName(f, currentFolders);
				if (current == null) {
					Folder folder = new Folder(service);
					folder.setDisplayName(f);
					folder.save(WellKnownFolderName.Inbox);

					setSharing(folder);

					usersFolders.add(folder.getId());
				} else {
					usersFolders.add(current.getId());
				}
			}
		}
		return usersFolders;
	}

	private EmailMessage createMessage(ExchangeService service, GeneratedUser user, boolean flags,
			boolean nationalChars, boolean attachments, boolean externalSender) throws Exception {

		MultiPartEmail email = new MultiPartEmail();
		email.setHostName("smtp." + domain); //lib just need this param set

		email.setFrom(getSender(externalSender));
		email.setSubject(getSubject(nationalChars));
		email.setMsg(getEmailText(nationalChars));
		email.addTo(user.getUserAddr(domain));
		email.buildMimeMessage();

		if (attachments && Math.random() < ATTACHMENT_PROBABILITY) {
			email.attach(createAttachment());
		}

		EmailMessage msg = new EmailMessage(service);
		msg.setMimeContent(new MimeContent("UTF-8", emailToBytes(email)));
		if (Math.random() < READED_PROBABILITY) {
			msg.setIsRead(true);
		}

		if (flags && Math.random() < FLAGS_PROBABILITY) {
			setFlasgs(msg);
		}

		return msg;
	}

	private byte[] emailToBytes(Email email) throws IOException, MessagingException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		email.getMimeMessage().writeTo(stream);
		return stream.toByteArray();
	}

	private String getSubject(boolean nationalChars) {
		if (nationalChars) {
			return "Тема письма (ru)";
		}
		return "Default email subject";
	}

	private String getEmailText(boolean nationalChars) {
		if (nationalChars) {
			return "давным-давно. был дед Мраз. Конец. (ru)";
		}
		return "This is a test mail ... :-)";
	}

	private EmailAttachment createAttachment() throws MalformedURLException {
		EmailAttachment attachment = new EmailAttachment();

		List<String> attachments = new ArrayList<>();
		attachments.add("files.zip");
		attachments.add("example.jpg");
		attachments.add("img.png");

		String randAttach = attachments.get((int) (Math.random() * attachments.size()));

		attachment.setPath(getClass().getResource(DEFAULT_ATTACHMENT_PATH + randAttach).getPath());
		attachment.setDisposition(EmailAttachment.ATTACHMENT);
		attachment.setDescription("Attachment");
		attachment.setName(randAttach);

		return attachment;
	}

	private void setFlasgs(EmailMessage email) throws Exception {
		ExtendedPropertyDefinition flagStatus = new ExtendedPropertyDefinition(
				0x1090, MapiPropertyType.Integer);
		email.setExtendedProperty(flagStatus, 2);

		ExtendedPropertyDefinition flagFlags = new ExtendedPropertyDefinition(
				0x0E2B, MapiPropertyType.Integer);
		email.setExtendedProperty(flagFlags, 1);
	}

	private void setSharing(Folder folder) throws Exception {

		//sharing - must be set as update
		if (Math.random() < SHARED_FOLDER_PROBABILITY) {
			folder.getPermissions().add(
					new FolderPermission(StandardUser.Anonymous, FolderPermissionLevel.Contributor));
		}
		if (Math.random() < SHARED_FOLDER_PROBABILITY) {
			for (int i = 0; i < (int) (Math.random() * 5); i++) {
				folder.getPermissions().add(
						new FolderPermission(getSender(false), FolderPermissionLevel.Contributor));
			}
		}
		folder.update();
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
