package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.event.ChangeListener;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ContactGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.EmailGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.EventGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.NameGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.UsersGenerator;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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

public class WindowController implements Initializable {

    @FXML
    private BorderPane windowRootPane;

    /***********USERS**********/

    @FXML
    private GridPane usersTable;

    @FXML
    private TextField userCountData;

    @FXML
    private Label firstNamesLabel;

    @FXML
    private Label lastNamesLabel;

    /***********EMAILS*********/

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

    /***********TASKS*********/

    @FXML
    private GridPane tasksTable;

    @FXML
    private TextField tasksCountData;

    /**********EVENTS*********/

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

    /*********CONTACTS********/

    @FXML
    private GridPane contactsTable;

    @FXML
    private TextField contactsCountData;

    /***********NOTES*********/

    @FXML
    private GridPane notesTable;

    @FXML
    private TextField notesCountData;

    /***********MAIN**********/

    @FXML
    private ProgressBar progressBar;

    private static final int DEFAULT_USER_COUNT = 10;
    private static int userCount, emailCount, contactCount, eventCount;
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

//    @FXML
//    private void handleAdvancedAction() {
//        if (advanced.isSelected()) {
//
//        } else {
//
//        }
//    }
//
//    @FXML
//    public void handleUsersAction() {
//        if (users.isSelected()) {
//            usersTable.setVisible(true);
//            GridPane.setColumnIndex(emailsTable, 1);
//        } else {
//            usersTable.setVisible(false);
//            GridPane.setColumnIndex(emailsTable, 0);
//        }
//    }

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
        String ewsUrl = "http://localhost:81/Ews/Exchange.asmx";
        LoginData loginData = LoginDataSession.getInstance().getLoginData();

        userCount = Integer.parseInt(userCountData.getText());
        emailCount = Integer.parseInt(emailCountData.getText());



        NameGenerator nameGenerator = new NameGenerator((firstnamesFile == null) ? null :  firstnamesFile.toPath(), (lastnamesFile == null) ? null :  lastnamesFile.toPath());
        UsersGenerator usersGenerator = new UsersGenerator(loginData.client, loginData.domainId, nameGenerator);

        List<GeneratedUser> generatedUsers = usersGenerator.getUsers();
        generatedUsers.add(new GeneratedUser(null, null, null, loginData.username));

        EmailGenerator emailGenerator = new EmailGenerator(ewsUrl, generatedUsers, domainName);
        ContactGenerator contactGenerator = new ContactGenerator(ewsUrl, generatedUsers, domainName);
        EventGenerator eventGenerator = new EventGenerator(ewsUrl, generatedUsers, domainName);

        Task<Void> task = new Task<Void>() {

            int progressCounter = 1;

            @Override
            public Void call() {
                for (int i = 0; i < userCount; i++) {
                    usersGenerator.generate(1);
                    updateProgress(progressCounter, userCount + emailCount + contactCount);
                    progressCounter++;
                }

                for (int j = 0; j < emailCount; j++) {
                    try {
                        emailGenerator.generateAndSave(j, emailFoldersSlider.getValue(), flag.isSelected(), randomEncoding.isSelected(), attachment.isSelected(), externalSender.isSelected());
                    } catch (Exception e) {
                        //TODO alert?
                       e.printStackTrace();
                    }
                    updateProgress(progressCounter, userCount + emailCount + contactCount);
                    progressCounter++;
                }

                for (int k = 0; k < contactCount; k++) {
                    try {
                        contactGenerator.generateAndSave(1);
                    } catch (Exception e) {
                        //TODO alert?
                       e.printStackTrace();
                    }
                    updateProgress(progressCounter, userCount + emailCount + contactCount);
                    progressCounter++;
                }



                for (int l = 0; l < eventCount; l++) {
                    try {
                        eventGenerator.generateAndSave(eventCount, fullDay.isSelected(), multipleDays.isSelected(), repeatable.isSelected(), privates.isSelected(), eventAttachment.isSelected(), invite.isSelected());
                    } catch (Exception e) {
                        //TODO alert?
                        e.printStackTrace();
                    }
                    updateProgress(progressCounter, userCount + emailCount + contactCount);
                    progressCounter++;
                }

                return null;
            }



            @Override
            protected void succeeded() {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UsersTable.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    GeneratedUsersController generatedUsersController = fxmlLoader.<GeneratedUsersController> getController();

                    generatedUsersController.setData(generatedUsers);

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
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setProgress(0);

        notesCountData.setText("not Implemented");
        tasksCountData.setText("not Implemented");

        emailFoldersSlider.valueProperty().addListener(new javafx.beans.value.ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                emailFoldersSliderLabel.setText("Folders: " + newValue.intValue() + "%");
            }
        });
    }
    //TODO rozsahy
    public void checkInput() {
        if (this.userCountData.getText().isEmpty()) {
            this.userCountData.setText(Integer.toString(DEFAULT_USER_COUNT));
        } else {
            userCount = Integer.parseInt(this.userCountData.getText());
            if (Math.signum((double) userCount) == -1.0) {
                showNumberError();
                this.userCountData.setText("");
            }
        }

        if (this.emailCountData.getText().isEmpty()) {
            this.emailCountData.setText(Integer.toString(DEFAULT_USER_COUNT));
        } else {
            emailCount = Integer.parseInt(this.emailCountData.getText());
            if (Math.signum((double) emailCount) == -1.0) {
                showNumberError();
                this.emailCountData.setText("");
            }
        }

        if (this.contactsCountData.getText().isEmpty()) {
            this.contactsCountData.setText(Integer.toString(DEFAULT_USER_COUNT));
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
            this.contactsCountData.setText(Integer.toString(DEFAULT_USER_COUNT));
        } else {
            eventCount = Integer.parseInt(this.eventsCountData.getText());
            if (eventCount > 1000000) {
                showNumberError(0, 1000000);
                this.contactsCountData.setText("");
            }
        }
    }

    public void setStage(Stage stage) {
        this.windowStage = stage;
    }
}