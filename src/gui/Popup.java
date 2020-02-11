package gui;

import javafx.scene.control.Alert;

public class Popup extends Alert {

	public Popup(String title, String headerText, String contextText) {
		this(title, headerText, contextText, AlertType.ERROR);
	}

	public Popup(String title, String headerText, String contextText, AlertType type) {
		super(type);
		setTitle(title);
		setHeaderText(headerText);
		setContentText(contextText);
	}

}
