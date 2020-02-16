package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getClassLoader().getResource("resources/app.fxml"));
		Parent content = loader.load();
		Scene scene = new Scene(content);
		primaryStage.setScene(scene);
		useageInformation();
		primaryStage.show();
	}

	/**
	 * Dialog showing current state of the application (the limits).
	 * Hopefully unnecessary soon.
	 */
	private void useageInformation() {
		new Popup(
				"Mule known limitations!", "Limitations:", String.format("* %s%n* %s",
						"Must open and close for consecutive runs", "App might freeze while making the PDF"),
				AlertType.INFORMATION).showAndWait();
	}
}