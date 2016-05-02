package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.event.ChangeListener;
import javax.xml.stream.XMLStreamException;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ApiClient;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.ContactGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.EmailGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.EventGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.NameGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.NotesGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.TaskGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.UsersGenerator;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Effect;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import microsoft.exchange.webservices.data.core.EwsServiceXmlWriter;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;

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

    /*********** NOTES *********/

    @FXML
    private GridPane notesTable;

    @FXML
    private TextField notesCountData;

    /*********** MAIN **********/

    @FXML
    private ProgressBar progressBar;

    private static final int DEFAULT_USER_COUNT = 10;
    private static final int ZERO_CONSTANT = 0;
    private static int userCount, emailCount, contactCount, eventCount, taskCount, noteCount;
    private Stage windowStage;
    private File firstnamesFile, lastnamesFile;

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

    // @FXML
    // private void handleAdvancedAction() {
    // if (advanced.isSelected()) {
    //
    // } else {
    //
    // }
    // }
    //
    // @FXML
    // public void handleUsersAction() {
    // if (users.isSelected()) {
    // usersTable.setVisible(true);
    // GridPane.setColumnIndex(emailsTable, 1);
    // } else {
    // usersTable.setVisible(false);
    // GridPane.setColumnIndex(emailsTable, 0);
    // }
    // }

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
        UsersGenerator usersGenerator = new UsersGenerator(loginData.client, loginData.domainId, nameGenerator);

        List<GeneratedUser> generatedUsers = usersGenerator.getUsers();
        generatedUsers
                .add(new GeneratedUser(loginData.username, loginData.username, loginData.password, loginData.username));

        EmailGenerator emailGenerator = new EmailGenerator(ewsUrl, generatedUsers, domainName);
        ContactGenerator contactGenerator = new ContactGenerator(ewsUrl, generatedUsers, domainName);
        EventGenerator eventGenerator = new EventGenerator(ewsUrl, generatedUsers, domainName);
        TaskGenerator taskGenerator = new TaskGenerator(ewsUrl, generatedUsers, domainName);
        NotesGenerator noteGenerator = new NotesGenerator(ewsUrl, generatedUsers, domainName);

        Task<Void> task = new Task<Void>() {

            int progressCounter = 1;

            @Override
            public Void call() throws URISyntaxException, FileNotFoundException, XMLStreamException {
                for (int i = 0; i < userCount; i++) {
                    usersGenerator.generate(1);
                    updateProgress(progressCounter, userCount + emailCount + contactCount + noteCount + taskCount);
                    progressCounter++;
                }

                //ulozeni a vypis chyb
                for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : usersGenerator.save()) {
                    System.out.println(e);
                }

                for (int j = 0; j < emailCount; j++) {
                    try {
                        emailGenerator.generateAndSave(1, emailFoldersSlider.getValue(), flag.isSelected(),
                                randomEncoding.isSelected(), attachment.isSelected(), externalSender.isSelected());
                    } catch (Exception e) {
                        // TODO alert?
                        e.printStackTrace();
                    }
                    updateProgress(progressCounter, userCount + emailCount + contactCount + noteCount + taskCount);
                    progressCounter++;
                }


                 for (int k = 0; k < contactCount; k++) {
                     try {
                         contactGenerator.generateAndSave(1);
                     } catch (Exception e) {
                        //TODO alert?
                         e.printStackTrace();
                     }
                     updateProgress(progressCounter, userCount + emailCount + contactCount + noteCount + taskCount);
                     progressCounter++;
                 }


                 for (int l = 0; l < eventCount; l++) {
                     try {
                         eventGenerator.generateAndSave(1, fullDay.isSelected(), multipleDays.isSelected(), repeatable.isSelected(), privates.isSelected(), eventAttachment.isSelected(), invite.isSelected());
                     } catch (Exception e) {
                         //TODO alert?
                         e.printStackTrace();
                     }
                     updateProgress(progressCounter, userCount + emailCount + contactCount + noteCount + taskCount);
                     progressCounter++;
                }

                 for (int m = 0; m < noteCount; m++) {
                     try {
                         noteGenerator.generateAndSave(1);
                     } catch (Exception e) {
                         //TODO alert?
                         e.printStackTrace();
                     }
                     updateProgress(progressCounter, userCount + emailCount + contactCount + noteCount + taskCount);
                     progressCounter++;
                 }

                 for (int n = 0; n < taskCount; n++) {
                     try {
                         taskGenerator.generateAndSave(1);
                     } catch (Exception e) {
                         //TODO alert?
                         e.printStackTrace();
                     }
                     updateProgress(progressCounter, userCount + emailCount + contactCount + noteCount + taskCount);
                     progressCounter++;
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

                    generatedUsersController.setData(generatedUsers, emailGenerator.getMessages(), contactGenerator.getContacts(), eventGenerator.getEvents(), noteGenerator.getNotes(), taskGenerator.getTasks(), ewsUrl, loginData);

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

        System.out.println(domainsRaw);

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
            taskCount = Integer.parseInt(this.eventsCountData.getText());
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