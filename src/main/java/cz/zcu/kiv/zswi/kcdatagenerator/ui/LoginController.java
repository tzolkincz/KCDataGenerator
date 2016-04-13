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
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField url;

    @FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }

    @FXML
    public void handleEnterPressed(ActionEvent event) {
       this.handleLoginAction(event);
    }

    @FXML
    private void handleLoginAction(ActionEvent event) {
        // pripojeni api clienta
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

        //vyber domeny
        Domain[] domains = client.getApi(Domains.class).get(new SearchQuery()).getList();

        String domainId = "";
        for (Domain domain : domains) {
            System.out.println(" => " + domain.getName());
            System.out.println(" => " + domain.getId());
            domainId = domain.getId();
        }
        LoginDataSession.getInstance().setLoginData(new LoginData(client, domainId, username.getText()));

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
