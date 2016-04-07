package br.ufrn.cloudbox.client.ui.controller;

import java.io.IOException;

import br.ufrn.cloudbox.client.service.OperationExecutor;
import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.exception.DuplicatedUserException;
import br.ufrn.cloudbox.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterWindowController {

	@FXML private TextField firstNameField;
	@FXML private TextField lastNameField;
	@FXML private TextField emailField;
	@FXML private PasswordField passwordField;

	@FXML private Text errorRegister;
	
	private OperationExecutor operationExecutor;
	
	public RegisterWindowController() {
		//TODO Change and remove null
		this.operationExecutor = new OperationExecutor(null, null);
	}
	
	@FXML
	public void handleRegisterAction(ActionEvent event) throws IOException {
		try {
			String firstName = firstNameField.getText();
			String lastName = lastNameField.getText();
			String email = emailField.getText();
			String password = passwordField.getText();
			
			User user = new User(firstName, lastName, email, password);
			this.operationExecutor.register(user);
			openWindow("login.fxml", "Login - CloudBox");
		} catch (ConnectionException e) {
			errorRegister.setText("Erro ao realizar operação.");
			e.printStackTrace();
		} catch (DuplicatedUserException e) {
			errorRegister.setText("Email já cadastrado.");
		}
	}
	
	@FXML
	public void handleBackAction(ActionEvent event) throws IOException {
		openWindow("login.fxml", "Login - CloudBox");
	}

	private void openWindow(String file, String title) throws IOException {
		Stage stage = (Stage) firstNameField.getScene().getWindow();
		Parent root = FXMLLoader.load(ClassLoader.getSystemResource(file));
		Scene scene = new Scene(root);
		
		stage.setTitle(title);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.show();
	}
}
