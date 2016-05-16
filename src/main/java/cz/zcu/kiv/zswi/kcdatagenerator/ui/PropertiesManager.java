package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.jabsorb.serializer.impl.SetSerializer;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Manager for saving and loading data from file
 * @author Daniel Holubar
 *
 */
public class PropertiesManager {

	/**
	 * Properties object for saving and getting data from and into file.
	 */
	private Properties prop;
	
	/**
	 * Controller with form.
	 */
	private WindowController windowController;
	
	/**
	 * Constructor
	 * @param windowController controller winth form for getting data filled.
	 */
	public PropertiesManager(WindowController windowController) {
		this.windowController = windowController;
		this.prop = new Properties();
	}

	/**
	 * Saves form filled data into properties file.
	 */
	public void saveProperties() {

		OutputStream output = null;

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("properties files (*.properties)",
				"*.properties");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showSaveDialog((Stage) windowController.windowRootPane.getScene().getWindow());
		if (file == null) {
			return;
		}

		try {

			output = new FileOutputStream(file);
			
			// set the properties value
			if(windowController.attachment.isSelected()) {
				prop.setProperty("attachment", "true");
			} else {
				prop.setProperty("attachment", "false");
			}
			
			prop.setProperty("contactsCountData", windowController.contactsCountData.getText());

			if (windowController.nationalChars.isSelected()) {
				prop.setProperty("nationalChars", "true");
			} else {
				prop.setProperty("nationalChars", "false");
			}

			if(windowController.domainBox.getValue() == null) {
				prop.setProperty("domainBox", "null");
			} else {
				prop.setProperty("domainBox", windowController.domainBox.getValue());
			}
			
			prop.setProperty("emailCountData", windowController.emailCountData.getText());
			prop.setProperty("emailFoldersSlider", windowController.emailFoldersSlider.getValue() + "");

			if (windowController.eventAttachment.isSelected()) {
				prop.setProperty("eventAttachment", "true");
			} else {
				prop.setProperty("eventAttachment", "false");
			}

			prop.setProperty("eventsCountData", windowController.eventsCountData.getText());

			if (windowController.externalSender.isSelected()) {
				prop.setProperty("externalSender", "true");
			} else {
				prop.setProperty("externalSender", "false");
			}

			if (windowController.flag.isSelected()) {
				prop.setProperty("flag", "true");
			} else {
				prop.setProperty("flag", "false");
			}

			if (windowController.fullDay.isSelected()) {
				prop.setProperty("fullDay", "true");
			} else {
				prop.setProperty("fullDay", "false");
			}

			if (windowController.invite.isSelected()) {
				prop.setProperty("invite", "true");
			} else {
				prop.setProperty("invite", "false");
			}

			if (windowController.multipleDays.isSelected()) {
				prop.setProperty("multipleDays", "true");
			} else {
				prop.setProperty("multipleDays", "false");
			}

			prop.setProperty("notesCountData", windowController.notesCountData.getText());

			if (windowController.privates.isSelected()) {
				prop.setProperty("privates", "true");
			} else {
				prop.setProperty("privates", "false");
			}

			if (windowController.randomEncoding.isSelected()) {
				prop.setProperty("randomEncoding", "true");
			} else {
				prop.setProperty("randomEncoding", "false");
			}

			if (windowController.repeatable.isSelected()) {
				prop.setProperty("repeatable", "true");
			} else {
				prop.setProperty("repeatable", "false");
			}

			prop.setProperty("tasksCountData", windowController.tasksCountData.getText());
			
			prop.setProperty("userCountData", windowController.userCountData.getText());

			// save properties to project root folder
			prop.store(output, null);

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("File info");
			alert.setHeaderText("Properties succesfully saved!");
			alert.showAndWait();
		} catch (Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("File error");
			alert.setHeaderText("Cannot save file!");
			ex.printStackTrace();
			alert.setContentText(ex.getMessage());
			alert.showAndWait();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Loads data from properties file and fill them into form.
	 */
	public void loadProperties() {

		InputStream input = null;
		
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("properties files (*.properties)",
				"*.properties");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showOpenDialog((Stage) windowController.windowRootPane.getScene().getWindow());
		if (file == null) {
			return;
		}

		try {

			input = new FileInputStream(file);

			// load a properties file
			prop.load(input);

			if(prop.getProperty("attachment").equals("true")) {
				windowController.attachment.setSelected(true);
			} else {
				windowController.attachment.setSelected(false);
			}
			
			windowController.contactsCountData.setText(prop.getProperty("contactsCountData"));

			if(prop.getProperty("nationalChars").equals("true")) {
				windowController.nationalChars.setSelected(true);
			} else {
				windowController.nationalChars.setSelected(false);
			}
	
			windowController.domainBox.setValue(prop.getProperty("domainBox"));
			windowController.emailCountData.setText(prop.getProperty("emailCountData"));
			windowController.emailFoldersSlider.setValue(Double.parseDouble(prop.getProperty("emailFoldersSlider")));

			if (prop.getProperty("eventAttachment").equals("true")) {
				windowController.eventAttachment.setSelected(true);
			} else {
				windowController.eventAttachment.setSelected(false);
			}

			windowController.eventsCountData.setText(prop.getProperty("eventsCountData"));

			if (windowController.externalSender.isSelected()) {
				windowController.externalSender.setSelected(true);
			} else {
				windowController.externalSender.setSelected(false);
			}

			if (prop.getProperty("flag").equals("true")) {
				windowController.flag.setSelected(true);
			} else {
				windowController.flag.setSelected(false);
			}

			if (prop.getProperty("fullDay").equals("true")) {
				windowController.fullDay.setSelected(true);
			} else {
				windowController.fullDay.setSelected(false);
			}

			if (prop.getProperty("invite").equals("true")) {
				windowController.invite.setSelected(true);
			} else {
				windowController.invite.setSelected(false);
			}

			if (prop.getProperty("multipleDays").equals("true")) {
				windowController.multipleDays.setSelected(true);
			} else {
				windowController.multipleDays.setSelected(false);
			}

			windowController.notesCountData.setText(prop.getProperty("notesCountData"));

			if (prop.getProperty("privates").equals("true")) {
				windowController.privates.setSelected(true);
			} else {
				windowController.privates.setSelected(false);
			}

			if (prop.getProperty("randomEncoding").equals("true")) {
				windowController.randomEncoding.setSelected(true);
			} else {
				windowController.randomEncoding.setSelected(false);
			}

			if (prop.getProperty("repeatable").equals("true")) {
				windowController.repeatable.setSelected(true);
			} else {
				windowController.repeatable.setSelected(false);
			}

			windowController.tasksCountData.setText(prop.getProperty("tasksCountData"));

			windowController.userCountData.setText(prop.getProperty("userCountData"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
