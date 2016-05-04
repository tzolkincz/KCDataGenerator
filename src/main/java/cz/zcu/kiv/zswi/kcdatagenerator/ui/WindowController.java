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

public class WindowController implements Initializable {

    @FXML
    private BorderPane windowRootPane;

    @FXML
    private ChoiceBox<String> domainBox;

    /*********** USERS **********/

    @FXML
    private GridPane usersTable;

    @FXML
    private TextField userCountData;

    @FXML
    private Label firstNamesLabel;

    @FXML
    private Label lastNamesLabel;

    /*********** EMAILS *********/

    @FXML
    private TextField emailCountData;

    @FXML
    private CheckBox attachment;

    @FXML
    private CheckBox randomEncoding;

    @FXML
    private CheckBox flag;

    @FXML
    private CheckBox externalSender;

    @FXML
    private GridPane emailsTable;

    @FXML
    private Slider emailFoldersSlider;

    @FXML
    private Label emailFoldersSliderLabel;

    /*********** TASKS *********/

    @FXML
    private GridPane tasksTable;

    @FXML
    private TextField tasksCountData;
    
    @FXML
    private CheckBox tasksNationalChars;

    /********** EVENTS *********/

    @FXML
    private GridPane eventsTable;

    @FXML
    private TextField eventsCountData;

    @FXML
    private CheckBox fullDay;

    @FXML
    private CheckBox multipleDays;

    @FXML
    private CheckBox repeatable;

    @FXML
    private CheckBox privates;

    @FXML
    private CheckBox eventAttachment;

    @FXML
    private CheckBox invite;

    /********* CONTACTS ********/

    @FXML
    private GridPane contactsTable;

    @FXML
    private TextField contactsCountData;
    
    @FXML
    private CheckBox contactsNationalChars;

    /*********** NOTES *********/

    @FXML
    private GridPane notesTable;

    @FXML
    private TextField notesCountData;
    
    @FXML
    private CheckBox notesNationalChars;

    /********* DOMAINS *********/
    
    @FXML
    private GridPane domainsTable;
    
    /*********** TABS **********/
    
    @FXML
    private GridPane tabsContainer;
    
    @FXML
    private TabPane tabs;
    
    /*********** MAIN **********/

    @FXML
    private ProgressBar progressBar;

    private static final int DEFAULT_USER_COUNT = 10;
    private static final int ZERO_CONSTANT = 0;
    private static int userCount, emailCount, contactCount, eventCount, taskCount, noteCount;
    private Stage windowStage;
    private File firstnamesFile, lastnamesFile;
    
    private List<Contact> contacts = new ArrayList<>();
    private List<Appointment> events = new ArrayList<>();
    private List<EmailMessage> emails = new ArrayList<>();
    private List<EmailMessage> notes = new ArrayList<>();
    private List<microsoft.exchange.webservices.data.core.service.item.Task> tasks = new ArrayList<>();

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

    @FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }

    @FXML
    public void handleChooseFirstNameFileAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        firstnamesFile = chooser.showOpenDialog(new Stage());

        if (firstnamesFile != null) {
            firstNamesLabel.setText(firstnamesFile.getName());
        }
    }

    @FXML
    public void handleChooseLastNameFileAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        lastnamesFile = chooser.showOpenDialog(new Stage());

        if (lastnamesFile != null) {
            lastNamesLabel.setText(lastnamesFile.getName());
        }
    }

    private void showNumberError() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Chyba - generování uživatelů");
        alert.setHeaderText("Formát čísla");
        alert.setContentText("Musíte zadat celé nezáporné číslo");
        alert.showAndWait();
    }

    private void showNumberError(int min, int max) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Chyba - generování uživatelů");
        alert.setHeaderText("Formát čísla");
        alert.setContentText("Musíte zadat celé nezáporné číslo v rozsahu " + min + "-" + max);
        alert.showAndWait();
    }

    private void generate() throws IOException, URISyntaxException {

        checkInput();

        String domainName = "localhost";
        String ewsUrl = "http://localhost:8800/Ews/Exchange.asmx";
        LoginData loginData = LoginDataSession.getInstance().getLoginData();

        userCount = Integer.parseInt(userCountData.getText());
        emailCount = Integer.parseInt(emailCountData.getText());

        NameGenerator nameGenerator = new NameGenerator((firstnamesFile == null) ? null : firstnamesFile.toPath(),
                (lastnamesFile == null) ? null : lastnamesFile.toPath());
        final UsersGenerator usersGenerator = new UsersGenerator(loginData.client, loginData.domainId, nameGenerator);

        final List<GeneratedUser> generatedUsers = usersGenerator.getUsers();

        EmailGenerator emailGenerator = new EmailGenerator(ewsUrl, generatedUsers, domainName);
        ContactGenerator contactGenerator = new ContactGenerator(ewsUrl, generatedUsers, domainName, nameGenerator);
        EventGenerator eventGenerator = new EventGenerator(ewsUrl, generatedUsers, domainName);
        TaskGenerator taskGenerator = new TaskGenerator(ewsUrl, generatedUsers, domainName);
        NotesGenerator noteGenerator = new NotesGenerator(ewsUrl, generatedUsers, domainName);

        Task<Void> task = new Task<Void>() {


            @Override
            public Void call() throws URISyntaxException, FileNotFoundException, XMLStreamException, Exception {
            	
        		usersGenerator.generate(1);

        		//ulozeni a vypis chyb
        		for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : usersGenerator.save()) {
        			System.out.println(e);
        		}

        		//vypis uzivatelu
        		List<GeneratedUser> users = usersGenerator.getUsers();
        		for (GeneratedUser gu : users) {
        			System.out.println(gu.getFirstName() + " " + gu.getLastName() + " " + " " + gu.getUsername() + ":" + gu.getPassword());
        		}

        		String domainName = "localhost";
        		String ewsUrl = "http://localhost:8800/Ews/Exchange.asmx";

        		emails = emailGenerator.generateAndSave(emailCount, emailFoldersSlider.getValue(), flag.isSelected(), randomEncoding.isSelected(), attachment.isSelected(), externalSender.isSelected());
        		updateProgress(emailCount, userCount + emailCount + contactCount + noteCount + taskCount);
        		
        		contacts = contactGenerator.generateAndSave(contactCount);
        		updateProgress(emailCount + contactCount, userCount + emailCount + contactCount + noteCount + taskCount);

        		events = eventGenerator.generateAndSave(eventCount, fullDay.isSelected(), multipleDays.isSelected(), repeatable.isSelected(), privates.isSelected(), eventAttachment.isSelected(), invite.isSelected(), contactsNationalChars.isSelected());
        		updateProgress(emailCount + contactCount + eventCount, userCount + emailCount + contactCount + noteCount + taskCount);
        		
        		tasks = taskGenerator.generateAndSave(taskCount, tasksNationalChars.isSelected());
        		updateProgress(emailCount + contactCount + eventCount + taskCount, userCount + emailCount + contactCount + noteCount + taskCount);

        		notes = noteGenerator.generateAndSave(noteCount, notesNationalChars.isSelected());
        		updateProgress(emailCount + contactCount + eventCount + taskCount + noteCount, userCount + emailCount + contactCount + noteCount + taskCount);
            	 
                return null;
            }

            @Override
            protected void succeeded() {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UsersTable.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    GeneratedUsersController generatedUsersController = fxmlLoader
                            .<GeneratedUsersController> getController();

                    generatedUsersController.setData(generatedUsers, emails, contacts, events, notes, tasks, ewsUrl, loginData);
                   
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        LoginData loginData = LoginDataSession.getInstance().getLoginData();
        Domain[] domainsRaw = loginData.client.getApi(Domains.class).get(new SearchQuery()).getList();


        for (int i = 0; i < domainsRaw.length; i++) {
            domainBox.getItems().add(domainsRaw[i].getName());
        }

        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setProgress(0);

        emailFoldersSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                emailFoldersSliderLabel.setText("Folders: " + newValue.intValue() + "%");
            }
        });
        
        tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    // TODO rozsahy
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

    public void setStage(Stage stage) {
        this.windowStage = stage;
    }
}