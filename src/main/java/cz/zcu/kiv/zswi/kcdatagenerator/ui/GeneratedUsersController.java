package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GeneratedUsersController implements Initializable{

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        generatedUsers = FXCollections.observableArrayList();

        firstName.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("lastName"));
        username.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("username"));
        password.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("password"));

        generatedUsersTable.setItems(generatedUsers);
    }

    public void setData(List<GeneratedUser> generatedData) {
        this.generatedUsers.addAll(generatedData);
    }

    @FXML
    private void handleExportAction() {
        String data = prepareCSV();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog((Stage) usersTable.getScene().getWindow());

        if(file != null){
            saveFile(data, file);
        }
    }

    private String prepareCSV() {
        String data = "";

        for (GeneratedUser user : generatedUsers) {
            data += user.getFirstName() + ";" + user.getLastName() + ";" + user.getUsername() + ";" + user.getPassword() + ";\n";
        }

        return data;
    }

    private void saveFile(String content, File file){
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
