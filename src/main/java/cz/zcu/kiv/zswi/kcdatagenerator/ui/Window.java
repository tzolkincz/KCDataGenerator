package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Window extends Application {

	// path to FXML file - path relative to classpath
	private static final String WINDOW_XML =  "/fxml/Window.fxml";

	// width of the window
	private static final double WINDOW_WIDTH = 300;

	// height of the window
	private static final double WINDOW_HEIGHT = 250;

	// loader of FXML file
	private FXMLLoader loader;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("KerioConnect generator");
		// obtains scene generated from FXML file
		primaryStage.setScene(createScene());
		primaryStage.show();
	}

	private Scene createScene() {
		// loading FXML file
		loader = new FXMLLoader(getClass().getResource(WINDOW_XML));// loading CSS file

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
			alert.setContentText("file: " + WINDOW_XML + "\n" +
					             "Error: " + e.getMessage() + "\n" +
					             "Exception: " + e.getClass().getName());
			alert.showAndWait();
			System.exit(1);
		}

		// creating scene from loaded panel
		Scene scene = new Scene(rootPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		// removing all CSS from the scene - only default look is used
		scene.getStylesheets().clear();

		return scene;
	}
}
