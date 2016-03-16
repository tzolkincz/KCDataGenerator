package cz.zcu.kiv.zswi.kcdatagenerator.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WindowController {

	@FXML
    private void handleExitAction(ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }
}