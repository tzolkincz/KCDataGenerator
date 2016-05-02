package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import com.kerio.lib.json.api.connect.admin.iface.Domains;
import com.kerio.lib.json.api.connect.admin.struct.Domain;
import com.kerio.lib.json.api.connect.admin.struct.common.SearchQuery;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.ApiClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Window extends Application {

    // path to FXML file - path relative to classpath
    private static final String LOGIN_WINDOW_XML =  "/fxml/Login.fxml";

    // width of the window
    private static final double WINDOW_WIDTH = 300;

    // height of the window
    private static final double WINDOW_HEIGHT = 200;

    // loader of FXML file
    private FXMLLoader loader;

    private Scene generation, login;

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kerio Connect generator");
        // obtains scene generated from FXML file
        this.primaryStage.setScene(createScene());
        this.primaryStage.show();
    }

    private Scene createScene() {
        // loading FXML file
        loader = new FXMLLoader(getClass().getResource(LOGIN_WINDOW_XML));// loading CSS file

        Pane rootPane = null;

        try {
            // obtaining root pane from loaded FXML
            rootPane = (Pane)loader.load();
            // when FXML is not found or cannot be parsed, an exception occurs
            // this will display dialog with information about exception
            // when debugging, it is better to replace this with
            // e.printStackTrace to have a better information about exception
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Window cannot be created");
            alert.setHeaderText("Cannot load XML file with definition of main window");
            alert.setContentText("file: " + LOGIN_WINDOW_XML + "\n" +
                                 "Error: " + e.getMessage() + "\n" +
                                 "Exception: " + e.getClass().getName());
            alert.showAndWait();
            System.exit(1);
        }

        // creating scene from loaded panel
        generation = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        // removing all CSS from the scene - only default look is used
        generation.getStylesheets().clear();

        return generation;
    }

}
