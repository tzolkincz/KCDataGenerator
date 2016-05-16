package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ContactGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.EmailGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.EventGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.NameGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.NotesGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.TaskGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.UsersGenerator;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

/**
 * Class takes care of actions in main window.
 * @author Daniel Holubář
 *
 */
public class WindowController implements Initializable {

	/**
	 * Main window pane.
	 */
	@FXML
	public BorderPane windowRootPane;

	/**
	 * Box for choosing domain.
	 */
	@FXML
	public ChoiceBox<String> domainBox;

	/*********** USERS **********/

	/**
	 * Table with generated users.
	 */
	@FXML
	private GridPane usersTable;

	/**
	 * Number of users to generate.
	 */
	@FXML
	public TextField userCountData;

	/**
	 * Label for firstnames file choose button.
	 */
	@FXML
	private Label firstNamesLabel;

	/**
	 * Label for lastnames file choose button.
	 */
	@FXML
	private Label lastNamesLabel;

	/*********** EMAILS *********/

	/**
	 * Number of emails to generate.
	 */
	@FXML
	public TextField emailCountData;

	/**
	 * Checkbox for attachments of emails.
	 */
	@FXML
	public CheckBox attachment;

	/**
	 * Checkbox for random encoding of emails.
	 */
	@FXML
	public CheckBox randomEncoding;

	/**
	 * Checkbox for flags of emails.
	 */
	@FXML
	public CheckBox flag;

	/**
	 * Checkbox for externalSender of emails.
	 */
	@FXML
	public CheckBox externalSender;

	/**
	 * Slider for choosing how big percentage of emails will be in folders.
	 */
	@FXML
	public Slider emailFoldersSlider;

	/**
	 * Label for slider.
	 */
	@FXML
	private Label emailFoldersSliderLabel;

	/*********** TASKS *********/

	/**
	 * Number of tasks to generate.
	 */
	@FXML
	public TextField tasksCountData;

	/**
	 * Checkbox for national characters in Others tab.
	 */
	@FXML
	public CheckBox nationalChars;

	/********** EVENTS *********/

	/**
	 * Number of events to generate.
	 */
	@FXML
	public TextField eventsCountData;

	/**
	 * Checkbox for whether event will be whole day.
	 */
	@FXML
	public CheckBox fullDay;

	/**
	 * Checkbox for whether event will be for multiple day.
	 */
	@FXML
	public CheckBox multipleDays;

	/**
	 * Checkbox for whether event will be repeatable.
	 */
	@FXML
	public CheckBox repeatable;

	/**
	 * Checkbox for whether event will be private.
	 */
	@FXML
	public CheckBox privates;

	/**
	 * Checkbox for whether event will have attachment.
	 */
	@FXML
	public CheckBox eventAttachment;

	/**
	 * Checkbox for whether event will have invite.
	 */
	@FXML
	public CheckBox invite;

	/********* CONTACTS ********/

	/**
	 * Number of contacts to generate.
	 */
	@FXML
	public TextField contactsCountData;

	/*********** NOTES *********/

	/**
	 * NUmber of notes to generate.
	 */
	@FXML
	public TextField notesCountData;


	/*********** TABS **********/

	/**
	 * TabPane to hold content in logical whole.
	 */
	@FXML
	private TabPane tabs;

	/*********** MAIN **********/

	/**
	 * Progress of whole process.
	 */
	@FXML
	private ProgressBar progressBar;

	/**
	 * Constant.
	 */
	private static final int ZERO_CONSTANT = 0;

	/**
	 * Constant.
	 */
	private static final String DEFAULT_VALUE = "10";

	/**
	 * Constant.
	 */
	private static final String DEFAULT_EVENTS_VALUE = "80";

	/**
	 * Constant.
	 */
	private static final double DEFAULT_FOLDERS_VALUE = 38.5;

	/**
	 * Number of users to generate.
	 */
	private static int userCount;

	/**
	 * Number of emails to generate.
	 */
	private static int emailCount;

	/**
	 * Number of contacts to generate.
	 */
	private static int contactCount;

	/**
	 * Number of events to generate.
	 */
	private static int eventCount;

	/**
	 * Number of tasks to generate.
	 */
	private static int taskCount;

	/**
	 * Number of notes to generate.
	 */
	private static int noteCount;

	/**
	 * External dictionary with firstnames.
	 */
	private File firstnamesFile;

	/**
	 * External dictionary with lastnames.
	 */
	private File lastnamesFile;

	/**
	 * List of generated contacts.
	 */
	private List<Contact> contacts = new ArrayList<>();

	/**
	 * List of generated events.
	 */
	private List<Appointment> events = new ArrayList<>();

	/**
	 * List of generated emails.
	 */
	private List<EmailMessage> emails = new ArrayList<>();

	/**
	 * List of generated notes.
	 */
	private List<EmailMessage> notes = new ArrayList<>();

	/**
	 * List of generated tasks.
	 */
	private List<microsoft.exchange.webservices.data.core.service.item.Task> tasks = new ArrayList<>();

	/**
	 * Manager for saving and loading properties.
	 */
	private PropertiesManager propertiesManager = null;

	/**
	 * Action will generate all datas.
	 * @param event click event on generate button
	 */
	@FXML
	private void handleUserGenerationAction(ActionEvent event) {

		try {
			generate();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Action will close application.
	 * @param event click event on exit option
	 */
	@FXML
	private void handleExitAction(ActionEvent event) {
		System.exit(0);
		Platform.exit();
	}

	/**
	 * Action will open file chooser for firstnames dictionary file.
	 */
	@FXML
	public void handleChooseFirstNameFileAction() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open File");
		firstnamesFile = chooser.showOpenDialog(new Stage());

		if (firstnamesFile != null) {
			firstNamesLabel.setText(firstnamesFile.getName());
		}
	}

	/**
	 * Action will open file chooser for lastnames dictionary file.
	 */
	@FXML
	public void handleChooseLastNameFileAction() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open File");
		lastnamesFile = chooser.showOpenDialog(new Stage());

		if (lastnamesFile != null) {
			lastNamesLabel.setText(lastnamesFile.getName());
		}
	}

	/**
	 * Action will save filled data.
	 */
	@FXML
	public void handleSavePropertiesAction() {
		if (propertiesManager == null) {
			propertiesManager = new PropertiesManager(this);
		}
		propertiesManager.saveProperties();
	}

	/**
	 * Action will load data from file and fill form.
	 */
	@FXML
	public void handleLoadPropertiesAction() {
		if (propertiesManager == null) {
			propertiesManager = new PropertiesManager(this);
		}
		propertiesManager.loadProperties();
	}

	/**
	 * Triggers alter for bad numbers.
	 */
	private void showNumberError() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Error - user generation");
		alert.setHeaderText("Number format");
		alert.setContentText("You have to fill not negative and not decimal number.");
		alert.showAndWait();
	}

	/**
	 * Triggers alter for bad numbers when out of range.
	 * @param min minimal value of range
	 * @param max maximal value of range
	 */
	private void showNumberError(int min, int max) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Error - user generation");
		alert.setHeaderText("Number format");
		alert.setContentText("You have to fill not negative and not decimal number in range " + min + "-" + max + ".");
		alert.showAndWait();
	}

	/**
	 * Generates all data from filled form informations.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void generate() throws IOException, URISyntaxException {

		checkInput();

		LoginData loginData = LoginDataSession.getInstance().getLoginData();
		String domainName = domainBox.getValue();
		String ewsUrl = "http://" + loginData.domainName + ":8800/Ews/Exchange.asmx";

		Domain[] domains = loginData.client.getApi(Domains.class).get(new SearchQuery()).getList();

		String domainId = "";
		for (Domain domain : domains) {
			if(domain.getName().contains(domainName)) {
				domainId = domain.getId();
			}
		}

		System.out.println("domain: " + domainId);

		userCount = Integer.parseInt(userCountData.getText());
		emailCount = Integer.parseInt(emailCountData.getText());

		NameGenerator nameGenerator = new NameGenerator((firstnamesFile == null) ? null : firstnamesFile.toPath(),
				(lastnamesFile == null) ? null : lastnamesFile.toPath());
		final UsersGenerator usersGenerator = new UsersGenerator(loginData.client, domainId, nameGenerator);

		final List<GeneratedUser> generatedUsers = usersGenerator.getUsers();

		EmailGenerator emailGenerator = new EmailGenerator(ewsUrl, generatedUsers, domainName);
		ContactGenerator contactGenerator = new ContactGenerator(ewsUrl, generatedUsers, domainName, nameGenerator);
		EventGenerator eventGenerator = new EventGenerator(ewsUrl, generatedUsers, domainName);
		TaskGenerator taskGenerator = new TaskGenerator(ewsUrl, generatedUsers, domainName);
		NotesGenerator noteGenerator = new NotesGenerator(ewsUrl, generatedUsers, domainName);

		//must be in task so update of GUI progress bar will work
		Task<Void> task = new Task<Void>() {

			@Override
			public Void call() throws URISyntaxException, FileNotFoundException, XMLStreamException, Exception {
				int allCount = userCount + emailCount + contactCount + noteCount + taskCount;
				int currentCount = 0;

				if (userCount > 0) {
					usersGenerator.generate(userCount);
					currentCount += userCount;
					updateProgress(currentCount, allCount);
				}

				for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : usersGenerator.save()) {
					System.out.println(e);
				}

				List<GeneratedUser> users = usersGenerator.getUsers();
				for (GeneratedUser gu : users) {
					System.out.println(gu.getFirstName() + " " + gu.getLastName() + " " + " " + gu.getUsername() + ":"
							+ gu.getPassword());
				}

				if (emailCount > 0) {
					emails = emailGenerator.generateAndSave(emailCount, emailFoldersSlider.getValue(), flag.isSelected(),
							randomEncoding.isSelected(), attachment.isSelected(), externalSender.isSelected());
					currentCount += emailCount;
					updateProgress(currentCount, allCount);
				}

				if (contactCount > 0) {
					contacts = contactGenerator.generateAndSave(contactCount);
					currentCount += contactCount;
					updateProgress(currentCount, allCount);
				}

				if (eventCount > 0) {
					events = eventGenerator.generateAndSave(eventCount, fullDay.isSelected(), multipleDays.isSelected(),
							repeatable.isSelected(), privates.isSelected(), eventAttachment.isSelected(),
							invite.isSelected(), nationalChars.isSelected());
					currentCount += eventCount;
					updateProgress(currentCount, allCount);
				}

				if (taskCount > 0) {
					tasks = taskGenerator.generateAndSave(taskCount, nationalChars.isSelected());
					currentCount += taskCount;
					updateProgress(currentCount, allCount);
				}

				if (noteCount > 0) {
					notes = noteGenerator.generateAndSave(noteCount, nationalChars.isSelected());
					currentCount += noteCount;
					updateProgress(currentCount, allCount);
				}
				return null;
			}

			@Override
			protected void succeeded() {
				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UsersTable.fxml"));
					Parent root1 = (Parent) fxmlLoader.load();
					GeneratedUsersController generatedUsersController = fxmlLoader
							.<GeneratedUsersController> getController();

					generatedUsersController.setData(generatedUsers, emails, contacts, events, notes, tasks, ewsUrl,
							loginData, domainBox.getValue());

					Stage stage = new Stage();
					stage.setTitle("Generated users");
					stage.setScene(new Scene(root1));

					stage.setOnCloseRequest(e -> {
						updateProgress(0, userCount);
					});

					stage.show();

				} catch (Exception e) {
					e.printStackTrace();
				}
				updateProgress(userCount, userCount);
			}
		};

		progressBar.progressProperty().bind(task.progressProperty());
		new Thread(task).start();
	}

	/**
	 * Start method of controller is called when new instance of WindowController is made.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		LoginData loginData = LoginDataSession.getInstance().getLoginData();
		Domain[] domainsRaw = loginData.client.getApi(Domains.class).get(new SearchQuery()).getList();

		for (int i = 0; i < domainsRaw.length; i++) {
			domainBox.getItems().add(domainsRaw[i].getName());
		}

		domainBox.setValue(domainsRaw[0].getName());

		progressBar.setMaxWidth(Double.MAX_VALUE);
		progressBar.setProgress(0);

		emailFoldersSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				emailFoldersSliderLabel.setText("Folders: " + newValue.intValue() + "%");
			}
		});

		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
	
		userCountData.setText(DEFAULT_VALUE);
		emailCountData.setText(DEFAULT_VALUE);
		attachment.setSelected(true);
		randomEncoding.setSelected(true);
		flag.setSelected(false);
		externalSender.setSelected(true);
		emailFoldersSlider.setValue(DEFAULT_FOLDERS_VALUE);
		tasksCountData.setText(DEFAULT_VALUE);
		nationalChars.setSelected(false);
		eventsCountData.setText(DEFAULT_EVENTS_VALUE);
		fullDay.setSelected(true);
		multipleDays.setSelected(true);
		repeatable.setSelected(false);
		privates.setSelected(true);
		eventAttachment.setSelected(true);
		invite.setSelected(false);
		contactsCountData.setText(DEFAULT_VALUE);
		notesCountData.setText(DEFAULT_VALUE);
	}

	/**
	 * Checks inputs before generation of data.
	 */
	public void checkInput() {
		if (this.userCountData.getText().isEmpty()) {
			this.userCountData.setText(Integer.toString(ZERO_CONSTANT));
		} else {
			userCount = Integer.parseInt(this.userCountData.getText());
			if (Math.signum((double) userCount) == -1.0) {
				showNumberError();
				this.userCountData.setText("");
			}
		}

		if (this.emailCountData.getText().isEmpty()) {
			this.emailCountData.setText(Integer.toString(ZERO_CONSTANT));
		} else {
			emailCount = Integer.parseInt(this.emailCountData.getText());
			if (Math.signum((double) emailCount) == -1.0) {
				showNumberError();
				this.emailCountData.setText("");
			}
		}

		if (this.contactsCountData.getText().isEmpty()) {
			this.contactsCountData.setText(Integer.toString(ZERO_CONSTANT));
		} else {
			contactCount = Integer.parseInt(this.contactsCountData.getText());
			if (Math.signum((double) contactCount) == -1.0) {
				showNumberError();
				this.emailCountData.setText("");
			}
			if (contactCount < 0 && contactCount > Math.min(1000000, userCount)) {
				showNumberError(0, Math.min(1000000, userCount));
			}
		}

		if (this.eventsCountData.getText().isEmpty()) {
			this.eventsCountData.setText(Integer.toString(ZERO_CONSTANT));
		} else {
			eventCount = Integer.parseInt(this.eventsCountData.getText());
			if (eventCount > 1000000) {
				showNumberError(0, 1000000);
				this.eventsCountData.setText("");
			}
		}

		if (this.tasksCountData.getText().isEmpty()) {
			this.tasksCountData.setText(Integer.toString(ZERO_CONSTANT));
		} else {
			taskCount = Integer.parseInt(this.tasksCountData.getText());
			if (taskCount > 1000000) {
				showNumberError(0, 1000000);
				this.tasksCountData.setText("");
			}
		}

		if (this.notesCountData.getText().isEmpty()) {
			this.notesCountData.setText(Integer.toString(ZERO_CONSTANT));
		} else {
			noteCount = Integer.parseInt(this.notesCountData.getText());
			if (noteCount > 1000000) {
				showNumberError(0, 1000000);
				this.notesCountData.setText("");
			}
		}
	}

	/**
	 * sets stage of controller
	 * @param stage stage of controller
	 */
	public void setStage(Stage stage) {
	}
}