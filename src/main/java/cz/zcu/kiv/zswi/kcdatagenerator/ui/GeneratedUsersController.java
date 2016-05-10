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

/**
 * Class for window with generated users actions and items.
 * @author Daniel Holubář
 *
 */
public class GeneratedUsersController implements Initializable {

	/**
	 * Window with generated users table.
	 */
    @FXML
    private BorderPane usersTable;

    /**
     * Table with generated users data.
     */
    @FXML
    private TableView<GeneratedUser> generatedUsersTable;

    /**
     * Column with user's firstname.
     */
    @FXML
    private TableColumn<GeneratedUser, String> firstName;

    /**
     * Column with user's lastname.
     */
    @FXML
    private TableColumn<GeneratedUser, String> lastName;

    /**
     * Column with user's username.
     */
    @FXML
    private TableColumn<GeneratedUser, String> username;

    /**
     * Column with user's password.
     */
    @FXML
    private TableColumn<GeneratedUser, String> password;

    /**
     * List of generated users.
     */
    private ObservableList<GeneratedUser> generatedUsers;
    
    /**
     * List of generated emails.
     */
    private List<EmailMessage> emailMessages;
    
    /**
     * List of generated contacts.
     */
    private List<Contact> contacts;
    
    /**
     * List of generated events.
     */
    private List<Appointment> events;
    
    /**
     * List of generated notes.
     */
    private List<EmailMessage> notes;
    
    /**
     * List of generated tasks.
     */
    private List<Task> tasks;

    /**
     * URL of exchange service.
     */
    private String ewsUrl;
    
    /**
     * Container for login data of logged in user.
     */
    private LoginData loginData;

    /**
     * Triggers when new instance of class is made.
     */
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

    /**
     * Fills lists with data.
     * @param generatedData generated users
     * @param emailMessages generated emails
     * @param contacts generated contacts
     * @param events generated events
     * @param notes generated notes
     * @param tasks generated tasks
     * @param ewsUrl url of exchange service
     * @param loginData login data of user
     */
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
    
    /**
     * Exports emails into XML file.
     */
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

    /**
     * Exports contacts into XML file.
     */
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

    /**
     * Exports events into XML file.
     */
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

    /**
     * Exports notes into XML file.
     */
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

    /**
     * Exports tasks into XML file.
     */
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
