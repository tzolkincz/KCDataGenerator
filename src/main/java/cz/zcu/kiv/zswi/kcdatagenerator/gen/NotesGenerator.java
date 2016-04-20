package cz.zcu.kiv.zswi.kcdatagenerator.gen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.MapiPropertyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.SendInvitationsMode;
import microsoft.exchange.webservices.data.core.enumeration.service.error.ServiceErrorHandling;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.exception.service.remote.ServiceRequestException;
import microsoft.exchange.webservices.data.core.request.CreateItemRequest;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.MimeContent;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;

public class NotesGenerator {

	private final String exchangeUrl;
	private final List<GeneratedUser> users;
	private final String domain;
	private final List<String> subjects = new ArrayList<>();
	private final List<MessageBody> bodys = new ArrayList<>();
	private final List<EmailMessage> generatedNotes;

	public static final int STICKY_NOTES_COLOURS_COUNT = 5;
	private static final String PROPERTY_DEFINITION_UUID = "0006200E-0000-0000-C000-000000000046"; //shitty api

	public NotesGenerator(String exchangeUrl, List<GeneratedUser> users, String domain) throws IOException {
		this.exchangeUrl = exchangeUrl;
		this.users = users;
		this.domain = domain;
		initSubjects();
		initBodys();
		generatedNotes = new ArrayList<>();
	}

	public List<EmailMessage> generateAndSave(int count) throws Exception {
		for (GeneratedUser user : users) {
			try (ExchangeService service = ExchangeServiceFactory.create(exchangeUrl, user, domain)) {

				CreateItemRequest createItemsRequest = new CreateItemRequest(service, ServiceErrorHandling.ThrowOnError);
				createItemsRequest.setSendInvitationsMode(SendInvitationsMode.SendToNone);

				List<Item> itemList = new ArrayList<>();
				for (int i = 0; i < count; i++) {

					EmailMessage stickyNote = new EmailMessage(service);
					stickyNote.setBody(getMessageBody());
					stickyNote.setItemClass("IPM.StickyNote");
					stickyNote.setSubject(getSubject());

					//set color - wtf? UUID?
					UUID guid = UUID.fromString(PROPERTY_DEFINITION_UUID);
					ExtendedPropertyDefinition prop = new ExtendedPropertyDefinition(guid, 0x8B00,
							MapiPropertyType.Integer);
					stickyNote.setExtendedProperty(prop, (int) (Math.random() * STICKY_NOTES_COLOURS_COUNT));
					//end set color

					stickyNote.setMimeContent(new MimeContent("utf-8", "foo".getBytes()));

					itemList.add(stickyNote);
					createItemsRequest.setItems(itemList);
					createItemsRequest.setParentFolderId(new FolderId(WellKnownFolderName.Notes));

					generatedNotes.add(stickyNote);
				}

				try {
					createItemsRequest.execute();
				} catch (ServiceRequestException e) {
					if (e.getCause() instanceof ServiceLocalException) {
						/*
						throwing: "microsoft.exchange.webservices.data.core.exception.service.local.
							ServiceLocalException: The type of the object in the store
							(PostItem) does not match that of the local object (Message)."
						is "ok". It works, but throws this error.
						 */
					} else {
						throw e; //propagate error
					}
				}
			}
		}

		return generatedNotes;
	}

	private void initSubjects() {
		subjects.add("Buy a house");
		subjects.add("Plant a tree");
		subjects.add("Find lost dog");
		subjects.add("Earn $1M");
		subjects.add("Earn $10M");
		subjects.add("Earn $1B");
		subjects.add("Commit suicide");
		subjects.add("Invent something cool");
		subjects.add("Go to the ZOO");
		subjects.add("Create family tree");
		subjects.add("Write book");
		subjects.add("Write CV");
	}

	private void initBodys() {
		bodys.add(new MessageBody("~msg~"));
	}

	private String getSubject() {
		return subjects.get((int) (Math.random() * subjects.size()));
	}

	private MessageBody getMessageBody() {
		return bodys.get((int) (Math.random() * bodys.size()));
	}
}
