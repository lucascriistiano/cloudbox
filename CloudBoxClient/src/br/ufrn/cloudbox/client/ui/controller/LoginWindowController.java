package br.ufrn.cloudbox.client.ui.controller;

import java.io.IOException;

import br.ufrn.cloudbox.client.service.OperationExecutor;
import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.exception.UserNotFoundException;
import br.ufrn.cloudbox.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginWindowController {

	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;

	@FXML
	private Text errorLogin;

	private OperationExecutor operationExecutor;

	public LoginWindowController() {
		//TODO Change and remove null
		this.operationExecutor = new OperationExecutor(null);
	}

	@FXML
	public void handleLoginAction(ActionEvent event) {
		String email = emailField.getText();
		String password = passwordField.getText();

		try {
			User user = this.operationExecutor.login(email, password);
			openWindowForUser("main.fxml", "Principal - CloudBox", user);
		} catch (ConnectionException e) {
			errorLogin.setText("Erro ao comunicar com o servidor.");
			e.printStackTrace();
		} catch (UserNotFoundException e) {
			errorLogin.setText("Email/senha inválidos!");
		} catch (IOException e) {
			errorLogin.setText("Erro ao realizar operação.");
			e.printStackTrace();
		}
	}

	@FXML
	public void handleRegisterAction(ActionEvent event) {
		try {
			openWindow("register.fxml", "Registro - CloudBox");
		} catch (IOException e) {
			errorLogin.setText("Erro ao iniciar operação.");
			e.printStackTrace();
		}
	}

	private void openWindow(String file, String title) throws IOException {
		Parent root = FXMLLoader.load(ClassLoader.getSystemResource(file));
		Scene scene = new Scene(root);

		Stage stage = (Stage) emailField.getScene().getWindow();
		stage.setTitle(title);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.show();
	}

	private void openWindowForUser(String file, String title, User user) throws IOException {
		FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(file));

		Stage stage = (Stage) emailField.getScene().getWindow();
		stage.setScene(new Scene((Pane) loader.load()));

		MainWindowController controller = loader.<MainWindowController> getController();
		controller.initData(user);

		stage.setTitle(title);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.show();
	}
}
