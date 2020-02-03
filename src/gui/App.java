package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

	private void useageInformation() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Mule known limitations!");
		alert.setHeaderText("Limitations:");
		String context = String.format("* %s%n* %s%n", "Must open and close for consecutive runs", "Title and Author can't contain spaces (yet!)");
		alert.setContentText(context);
		alert.showAndWait();
	}
}