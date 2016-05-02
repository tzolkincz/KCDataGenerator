package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ExchangeServiceFactory;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import microsoft.exchange.webservices.data.core.EwsServiceXmlWriter;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Task;

public class GeneratedUsersController implements Initializable {

    @FXML
    private BorderPane usersTable;

    @FXML
    private TableView<GeneratedUser> generatedUsersTable;

    @FXML
    private TableColumn<GeneratedUser, String> firstName;

    @FXML
    private TableColumn<GeneratedUser, String> lastName;

    @FXML
    private TableColumn<GeneratedUser, String> username;

    @FXML
    private TableColumn<GeneratedUser, String> password;

    private ObservableList<GeneratedUser> generatedUsers;
    private List<EmailMessage> emailMessages;
    private List<Contact> contacts;
    private List<Appointment> events;
    private List<EmailMessage> notes;
    private List<Task> tasks;

    private String ewsUrl;
    private LoginData loginData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        generatedUsers = FXCollections.observableArrayList();

        firstName.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("lastName"));
        username.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("username"));
        password.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("password"));

        firstName.setCellFactory(TextFieldTableCell.forTableColumn());
        lastName.setCellFactory(TextFieldTableCell.forTableColumn());
        username.setCellFactory(TextFieldTableCell.forTableColumn());
        password.setCellFactory(TextFieldTableCell.forTableColumn());

        generatedUsersTable.setEditable(true);

        generatedUsersTable.setItems(generatedUsers);
    }

    public void setData(List<GeneratedUser> generatedData, List<EmailMessage> emailMessages, List<Contact> contacts, List<Appointment> events, List<EmailMessage> notes, List<Task> tasks, String ewsUrl,
            LoginData loginData) {
        generatedUsers.addAll(generatedData);
        this.ewsUrl = ewsUrl;
        this.loginData = loginData;
        this.emailMessages = emailMessages;
        this.contacts = contacts;
        this.events = events;
        this.notes = notes;
        this.tasks = tasks;
    }
    //TODO jedna metoda
    @FXML
    private void handleExportEmailAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) usersTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        FileOutputStream stream = null;

        try {
            List<EmailMessage> messages = emailMessages;
            ExchangeService service = ExchangeServiceFactory.create(ewsUrl, loginData.username, loginData.password);
            stream = new FileOutputStream(file);
            EwsServiceXmlWriter writer = new EwsServiceXmlWriter(service, stream);

            for (EmailMessage message : messages) {
                try {
                    message.writeToXml(writer);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File info");
            alert.setHeaderText("Emails succesfully saved!");
            alert.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File error");
            alert.setHeaderText("Cannot save file!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @FXML
    private void handleExportContactsAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) usersTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        FileOutputStream stream = null;

        try {
            ExchangeService service = ExchangeServiceFactory.create(ewsUrl, loginData.username, loginData.password);
            stream = new FileOutputStream(file);
            EwsServiceXmlWriter writer = new EwsServiceXmlWriter(service, stream);

            for (Contact contact : contacts) {
                try {
                    contact.writeToXml(writer);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File info");
            alert.setHeaderText("Contacts succesfully saved!");
            alert.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File error");
            alert.setHeaderText("Cannot save file!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @FXML
    private void handleExportEventsAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) usersTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        FileOutputStream stream = null;

        try {
            ExchangeService service = ExchangeServiceFactory.create(ewsUrl, loginData.username, loginData.password);
            stream = new FileOutputStream(file);
            EwsServiceXmlWriter writer = new EwsServiceXmlWriter(service, stream);

            for (Appointment event : events) {
                try {
                    event.writeToXml(writer);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File info");
            alert.setHeaderText("Events succesfully saved!");
            alert.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File error");
            alert.setHeaderText("Cannot save file!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @FXML
    private void handleExportNotesAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) usersTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        FileOutputStream stream = null;

        try {
            ExchangeService service = ExchangeServiceFactory.create(ewsUrl, loginData.username, loginData.password);
            stream = new FileOutputStream(file);
            EwsServiceXmlWriter writer = new EwsServiceXmlWriter(service, stream);

            for (EmailMessage note : notes) {
                try {
                    note.writeToXml(writer);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File info");
            alert.setHeaderText("Notes succesfully saved!");
            alert.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File error");
            alert.setHeaderText("Cannot save file!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @FXML
    private void handleExportTasksAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) usersTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        FileOutputStream stream = null;

        try {
            ExchangeService service = ExchangeServiceFactory.create(ewsUrl, loginData.username, loginData.password);
            stream = new FileOutputStream(file);
            EwsServiceXmlWriter writer = new EwsServiceXmlWriter(service, stream);

            for (Task task : tasks) {
                try {
                    task.writeToXml(writer);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File info");
            alert.setHeaderText("Tasks succesfully saved!");
            alert.showAndWait();
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File error");
            alert.setHeaderText("Cannot save file!");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
