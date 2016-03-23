package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.NameGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.UsersGenerator;
import java.net.URISyntaxException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WindowController implements Initializable {

	@FXML
	private TextField userCountData;

	@FXML
	private Text actiontarget;

	private static final int DEFAULT_USER_COUNT = 10;
	private int userCount = DEFAULT_USER_COUNT;

	@FXML
    private void handleUserGenerationAction(ActionEvent event) {
		checkUserCount();
		generate();
    }

	@FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }

	private void checkUserCount() {

		this.userCountData.focusedProperty().addListener((arg0, oldValue, newValue) -> {
			if(!newValue) {
				if(this.userCountData.getText().isEmpty()) {
					this.userCount = DEFAULT_USER_COUNT;
				} else {
					userCount = Integer.parseInt(this.userCountData.getText());

					if(Math.signum((double)userCount) == -1.0) {
						showNumberError();
						this.userCountData.setText("");
					}
				}
			}
		});
	}

	private void showNumberError() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Chyba - generování uživatelů");
		alert.setHeaderText("Formát čísla");
		alert.setContentText("Musíte zadat celé nezáporné číslo");
		alert.showAndWait();
	}

	private void generate() {

		//generovani uzivatelu
		//NameGenerator prijma jako argumenty cesty ke slovnikum (fist, last names).
		//  Kdyz je null, pouziji se implicitni slovniky
		UsersGenerator usersGenerator = null;

		this.userCount = Integer.parseInt(this.userCountData.getText());

		try {
			NameGenerator nameGenerator = new NameGenerator(null, null);

			LoginData loginData = LoginDataSession.getInstance().getLoginData();

			usersGenerator = new UsersGenerator(loginData.client,loginData.domainId, nameGenerator);
			usersGenerator.generate(this.userCount);

			try {
		        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UsersTable.fxml"));
		        Parent root1 = (Parent) fxmlLoader.load();
		        GeneratedUsersController generatedUsersController = fxmlLoader.<GeneratedUsersController>getController();

		        generatedUsersController.setData(usersGenerator.getUsers());

                Stage stage = new Stage();
                stage.setTitle("Generated users");
                stage.setScene(new Scene(root1));
                stage.show();
	        } catch(Exception e) {
	           e.printStackTrace();
	        }

		} catch (IOException|URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//ulozeni a vypis chyb
		for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : usersGenerator.save()) {
			System.out.println(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
}