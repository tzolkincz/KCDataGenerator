package cz.zcu.kiv.zswi.kcdatagenerator.ui;

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

public class GeneratedUsersController implements Initializable{

	
	@FXML
	private TableView<GeneratedUser> generatedUsersTable;
	
	@FXML
	private TableColumn<GeneratedUser, String> firstName;
	
	@FXML
	private TableColumn<GeneratedUser, String> lastName;
	
	@FXML
	private TableColumn<GeneratedUser, String> password;
	
	private ObservableList<GeneratedUser> generatedUsers;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		generatedUsers = FXCollections.observableArrayList();

		firstName.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("firstName"));
		lastName.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("lastName"));
		password.setCellValueFactory(new PropertyValueFactory<GeneratedUser, String>("password"));
		
		generatedUsersTable.setItems(generatedUsers);
	}
	
	public void setData(List<GeneratedUser> generatedData) {
		this.generatedUsers.addAll(generatedData);
	}
	
	
}
