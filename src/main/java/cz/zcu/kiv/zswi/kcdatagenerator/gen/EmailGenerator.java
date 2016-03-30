package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.MimeContent;
import microsoft.exchange.webservices.data.search.FolderView;

public class EmailGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private final List<String> folders = new ArrayList<>();

	public static final double READED_PROBABILITY = 0.95;

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
		EmailMessage msg = new EmailMessage(service);

		msg.setSubject("Hello world!");
		msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Java API."));
		msg.getToRecipients().add(getSender());

		String mime = ""
				+ "From: <" + getSender() + ">\n"
				+ "To: " + user.getFirstName() + " " + user.getLastName() + " <" + getUserAddr(user) + ">\n"
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
				+ System.currentTimeMillis()
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
		if (Math.random() < READED_PROBABILITY) {
			msg.setIsRead(true);
		}

		return msg;
	}

	private String getUserAddr(GeneratedUser u) {
		return u.getUsername() + "@" + domain;
	}
}
