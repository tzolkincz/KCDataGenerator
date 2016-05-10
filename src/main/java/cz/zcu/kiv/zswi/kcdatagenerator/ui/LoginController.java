package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ApiClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Class for login window actions and items.
 * @author Daniel Holubář
 */
public class LoginController {
	
	/**
	 * Field for username credential.
	 */
    @FXML
    private TextField username;

    /**
	 * Field for password credential.
	 */
    @FXML
    private PasswordField password;

    /**
	 * Field for url of server.
	 */
    @FXML
    private TextField url;

    /**
     * Action will exit application.
     * @param event event of action
     */
    @FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }

    /**
     * Action will trigger when Enter key is pressed.
     * @param event event of action
     */
    @FXML
    public void handleEnterPressed(ActionEvent event) {
       this.handleLoginAction(event);
    }

    /**
     * Action will log user in according to credentials filled.
     * @param event event of action
     */
    @FXML
    private void handleLoginAction(ActionEvent event) {
        
        ApiClient client = new ApiClient();

        try {
            client.login(url.getText(), username.getText(), password.getText());
            
        } catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login error");
            alert.setHeaderText("Cannot log in!");
            alert.setContentText("Check login credentials and URL.");
            alert.showAndWait();
        }

        
        Domain[] domains = client.getApi(Domains.class).get(new SearchQuery()).getList();

        String domainId = "";
        for (Domain domain : domains) {
            System.out.println(" => " + domain.getName());
            System.out.println(" => " + domain.getId());
            domainId = domain.getId();
        }
        LoginDataSession.getInstance().setLoginData(new LoginData(client, domainId, username.getText(), password.getText()));
        
        ((Stage)((Node)event.getTarget()).getScene().getWindow()).close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Window.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Kerio Generator");
            stage.setScene(new Scene(root1));
            stage.show();

            WindowController controller = fxmlLoader.getController();
            controller.setStage(stage);

        } catch(Exception e) {
           e.printStackTrace();
        }

    }

}
