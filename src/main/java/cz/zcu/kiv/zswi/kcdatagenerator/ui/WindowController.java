package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.event.ChangeListener;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.EmailGenerator;
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

    @FXML
    private TextField userCountData;

    @FXML
    private Text actiontarget;

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
    private CheckBox advanced;

    @FXML
    private CheckBox users;

    @FXML
    private CheckBox emails;

    @FXML
    private Label firstNamesLabel;

    @FXML
    private Label lastNamesLabel;

    @FXML
    private GridPane usersTable;

    @FXML
    private GridPane emailsTable;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Slider emailFoldersSlider;

    @FXML
    private Label emailFoldersSliderLabel;

    private static final int DEFAULT_USER_COUNT = 10;
    private static int userCount, emailCount;
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

    @FXML
    private void handleAdvancedAction() {
        if (advanced.isSelected()) {

        } else {

        }
    }

    @FXML
    public void handleUsersAction() {
        if (users.isSelected()) {
            usersTable.setVisible(true);
            GridPane.setColumnIndex(emailsTable, 1);
        } else {
            usersTable.setVisible(false);
            GridPane.setColumnIndex(emailsTable, 0);
        }
    }

    @FXML
    public void handleChooseFirstNameFileAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        firstnamesFile = chooser.showOpenDialog(new Stage());

        firstNamesLabel.setText(firstnamesFile.getName());
    }

    @FXML
    public void handleChooseLastNameFileAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        lastnamesFile = chooser.showOpenDialog(new Stage());

        lastNamesLabel.setText(lastnamesFile.getName());
    }

    private void showNumberError() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Chyba - generování uživatelů");
        alert.setHeaderText("Formát čísla");
        alert.setContentText("Musíte zadat celé nezáporné číslo");
        alert.showAndWait();
    }

    private void generate() throws IOException, URISyntaxException {

        checkInput();

        String domainName = "localhost";
        userCount = Integer.parseInt(userCountData.getText());
        emailCount = Integer.parseInt(emailCountData.getText());

        NameGenerator nameGenerator = new NameGenerator(firstnamesFile.toPath(), lastnamesFile.toPath());
        LoginData loginData = LoginDataSession.getInstance().getLoginData();
        UsersGenerator usersGenerator = new UsersGenerator(loginData.client, loginData.domainId, nameGenerator);

        List<GeneratedUser> generatedUsers = usersGenerator.getUsers();
        generatedUsers.add(new GeneratedUser(null, null, null, loginData.username));

        EmailGenerator eg = new EmailGenerator("http://localhost:81/Ews/Exchange.asmx", generatedUsers, domainName);

        Task<Void> task = new Task<Void>() {

            @Override
            public Void call() {
                for (int i = 0; i < userCount; i++) {
                    usersGenerator.generate(1);
                    updateProgress(i, userCount + emailCount);
                    updateProgress(i, userCount);
                }

                for (int j = 0; j < emailCount; j++) {
                    try {
                        eg.generateAndSave(j, emailFoldersSlider.getValue(), flag.isSelected(), randomEncoding.isSelected(), attachment.isSelected(), externalSender.isSelected());
                    } catch (Exception e) {
                        //TODO alert?
                        e.printStackTrace();
                    }
                    updateProgress(j, userCount + emailCount);
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

        // ulozeni a vypis chyb
        for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : usersGenerator.save()) {
            System.out.println(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setProgress(0);

        emailFoldersSlider.setOnMouseDragged(e -> {
            int percentage = (int)emailFoldersSlider.getValue();
            emailFoldersSliderLabel.setText("Folders: " + percentage + "%");
        });
    }

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
    }

    public void setStage(Stage stage) {
        this.windowStage = stage;
    }
}