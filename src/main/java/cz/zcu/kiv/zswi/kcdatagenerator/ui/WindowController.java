package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.IOException;
import java.net.URISyntaxException;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ApiClient;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.NameGenerator;
import cz.zcu.kiv.zswi.kcdatagenerator.gen.UsersGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WindowController {

	@FXML 
	private TextField userCountData;
	
	@FXML 
	private Text actiontarget;
	
	private int userCount;
	private static final int DEFAULT_USER_COUNT = 10;
	
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
		// pripojeni api clienta
		ApiClient client = new ApiClient();
		client.login("http://localhost:4040", "Sculptura", "911015");

		//vyber domeny
		Domain[] domains = client.getApi(Domains.class).get(new SearchQuery()).getList();

		String domainId = "";
		
		for (Domain domain : domains) {
			System.out.println(" => " + domain.getName());
			System.out.println(" => " + domain.getId());
			domainId = domain.getId();
		}

		//generovani uzivatelu
		//NameGenerator prijma jako argumenty cesty ke slovnikum (fist, last names).
		//  Kdyz je null, pouziji se implicitni slovniky
		UsersGenerator g = null;
		
		try {
			NameGenerator n = new NameGenerator(null, null);
			n.load();
			g = new UsersGenerator(client, domainId, n);
			g.generate(5);
			
			
			
			try {
		        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UsersTable.fxml"));
		        Parent root1 = (Parent) fxmlLoader.load();
		        GeneratedUsersController generatedUsersController = fxmlLoader.<GeneratedUsersController>getController();
                
		        generatedUsersController.setData(g.getGeneratedUsers());
		        
                Stage stage = new Stage();
                stage.setTitle("Generated users");
                stage.setScene(new Scene(root1));  
                stage.show();
	        } catch(Exception e) {
	           e.printStackTrace();
	        }
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//ulozeni a vypis chyb
		for (com.kerio.lib.json.api.connect.admin.struct.common.Error e : g.save()) {
			System.out.println(e);
		}
	}
}