package br.ufrn.cloudbox.client.ui;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(ClassLoader.getSystemResource("login.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(ClassLoader.getSystemResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Login - CloudBox");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.centerOnScreen();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
