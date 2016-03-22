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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

	@FXML
	private TextField username;
	
	@FXML
	private PasswordField password;
	
	@FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }
	
	@FXML
    private void handleLoginAction(ActionEvent event) {
		// pripojeni api clienta
		ApiClient client = new ApiClient();
		
		try {
			client.login("http://localhost:4040", username.getText(), password.getText());
		} catch(Exception e) {
			
		}

		//vyber domeny
		Domain[] domains = client.getApi(Domains.class).get(new SearchQuery()).getList();

		String domainId = "";
		for (Domain domain : domains) {
			System.out.println(" => " + domain.getName());
			System.out.println(" => " + domain.getId());
			domainId = domain.getId();
		}
		LoginDataSession.getInstance().setLoginData(new LoginData(client, domainId));
		
		((Stage)((Node)event.getTarget()).getScene().getWindow()).close();
		
		try {
	        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Window.fxml"));
	        Parent root1 = (Parent) fxmlLoader.load();
	        
            Stage stage = new Stage();
            stage.setTitle("Generated users");
            stage.setScene(new Scene(root1));  
            stage.show();
        } catch(Exception e) {
           e.printStackTrace();
        }
	
    }

}
