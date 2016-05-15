package cz.zcu.kiv.zswi.kcdatagenerator.imp;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import cz.zcu.kiv.zswi.kcdatagenerator.gen.GeneratedUser;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

/**
 * Class handles upload of .eml files on server.
 * 
 * @author Daniel Holubář
 *
 */
public class EmlImporter {

	/**
	 * Main window pane.
	 */
	@FXML
	public AnchorPane windowRootPane;
	
	/**
	 * List of email files.
	 */
	private ArrayList<File> emailsFiles;

	/**
	 * Serializer for import of .eml files
	 */
	private Serializer serializer;

	/**
	 * Constructor
	 * 
	 * @param files
	 *            .eml files with emails
	 */
	public EmlImporter(ArrayList<File> files) {
		this.emailsFiles = files;
		serializer = new Serializer();
	}

	/**
	 * Imports emails from files.
	 * 
	 * @param generatedUsers
	 *            list of generated users
	 * @param ewsUrl
	 *            url for import via ews
	 * @param domainName
	 *            name of domain
	 */
	public void importEml(List<GeneratedUser> generatedUsers, String ewsUrl, String domainName) {

		Stage stage = new Stage();
		
		Label label = new Label("Import progress: ");
		label.setFont(new Font("Arial", 15));
		
		ProgressBar progressBar = new ProgressBar(0);
		progressBar.setMaxWidth(Double.MAX_VALUE);
		
		try {
			 GridPane root = new GridPane();
			 root.setPadding(new Insets(10));
			 
			 GridPane.setHalignment(label, HPos.LEFT);
			 root.add(label, 0, 0);
			 
			 GridPane.setHalignment(progressBar, HPos.RIGHT);
			 root.add(progressBar, 1, 0);
			 
			 stage.setTitle("Import progress");
			 stage.setScene(new Scene(root));
			 stage.show();
		} catch (Exception e) {
			e.printStackTrace();

		}


		Task<Void> task = new Task<Void>() {

			@Override
			public Void call() throws URISyntaxException, FileNotFoundException, XMLStreamException, Exception {
				int allCount = emailsFiles.size();
				int currentCount = 0;
				
				try {
					for (File file : emailsFiles) {
						currentCount++;
						String emlFileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
						serializer.sendEmlToServer(emlFileContent, generatedUsers, ewsUrl, domainName,
								WellKnownFolderName.Inbox);
						updateProgress(currentCount, allCount);
					}
				} catch (Exception e) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Error - email import");
					alert.setHeaderText("Import error");
					alert.setContentText(e.getMessage());
					alert.showAndWait();
					e.printStackTrace();
				}
				
				return null;
			}

			@Override
			protected void succeeded() {
				stage.hide();
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Success - email import");
				alert.setHeaderText("Import successful!");
				alert.showAndWait();
			}
		};
		
		progressBar.progressProperty().bind(task.progressProperty());
		new Thread(task).start();
	}

}
