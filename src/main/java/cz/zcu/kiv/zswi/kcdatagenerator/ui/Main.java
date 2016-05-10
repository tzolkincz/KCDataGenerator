package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main class that starts whole application.
 * @author Daniel Holubář
 *
 */
public class Main extends Application {

    /**
     * Path to fxml file.
     */
    private static final String LOGIN_WINDOW_XML =  "/fxml/Login.fxml";


    /**
     * Width of login window.
     */
    private static final double WINDOW_WIDTH = 300;

    /**
     * Loader for FXML file.
     */
    private static final double WINDOW_HEIGHT = 200;

    /**
     * Width of login window.
     */
    private FXMLLoader loader;

    /**
     * Scene for login window.
     */
    private Scene login;

    /**
     * Main stage of window.
     */
    private Stage primaryStage;

    /**
     * Main class, that starts application.
     * @param args external arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Opens window.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Kerio Connect generator");
        // obtains scene generated from FXML file
        this.primaryStage.setScene(createScene());
        this.primaryStage.show();
    }

    /**
     * Creates scene from FXML file.
     * @return scene
     */
    private Scene createScene() {
        // loading FXML file
        loader = new FXMLLoader(getClass().getResource(LOGIN_WINDOW_XML));

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
           	e.printStackTrace();
            System.exit(1);
        }

        // creating scene from loaded panel
        login = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        // removing all CSS from the scene - only default look is used
        login.getStylesheets().clear();

        return login;
    }

}
