package gui;

import javafx.scene.control.Alert;

public class ErrorAlert extends Alert {

	public ErrorAlert(String title, String headerText, String contextText) {
		super(AlertType.ERROR);
		setTitle(title);
		setHeaderText(headerText);
		setContentText(contextText);
	}

}
