package br.ufrn.cloudbox.client.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import br.ufrn.cloudbox.client.service.ChangeListener;
import br.ufrn.cloudbox.client.service.OperationExecutor;
import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainWindowController {

	@FXML
	private TextField folderField;

	@FXML
	private Text txtStatus;

	@FXML
	private Text txtLoggedUser;

	private OperationExecutor operationExecutor;
	private DirectoryChooser directoryChooser;

	private User user;
	private File selectedDirectory = null;

	private ChangeListener changeListener;

	public MainWindowController() {
		this.directoryChooser = new DirectoryChooser();
		this.directoryChooser.setTitle("Escolher diretório de sicronização");
		this.directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}

	public void initData(User user) {
		this.user = user;
		this.txtLoggedUser.setText("Logado como: " + this.user.getEmail());
		this.operationExecutor = new OperationExecutor(txtStatus);
	}

	@FXML
	public void handleChooseFolderAction(ActionEvent event) {
		try {
			Stage stage = (Stage) folderField.getScene().getWindow();

			File directory = this.directoryChooser.showDialog(stage);
			if (directory != null) {
				changeDirectory(directory);
				syncAndMonitorNewRootFolder();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void changeDirectory(File directory) throws InterruptedException, IOException, URISyntaxException, ConnectionException {
		selectedDirectory = directory;
		updateDirectoryPathExhibition();
		stopPreviousMonitorActive();
	}

	private void syncAndMonitorNewRootFolder() throws IOException, URISyntaxException, ConnectionException {
		String absolutePathRootDirectory = this.selectedDirectory.getAbsolutePath();

		// Start client change listener
		txtStatus.setText("Iniciando monitoramento...");
		changeListener = new ChangeListener(operationExecutor, user, absolutePathRootDirectory);
		changeListener.start();
		txtStatus.setText("Monitorando diretório...");
	}

	@FXML
	public void handleLogoutAction(ActionEvent event) throws IOException, InterruptedException {
		stopPreviousMonitorActive();
		openLoginWindow();
	}

	private void stopPreviousMonitorActive() {
		if (this.changeListener != null) {
			txtStatus.setText("Parando monitoramento anterior...");
			this.changeListener.stopListening();
			this.changeListener = null;
		}
	}

	private void updateDirectoryPathExhibition() {
		if (this.selectedDirectory != null) {
			this.folderField.setText(this.selectedDirectory.getAbsolutePath());
		} else {
			this.folderField.clear();
		}
	}

	private void openLoginWindow() throws IOException {
		Stage stage = (Stage) this.folderField.getScene().getWindow();
		Parent root = FXMLLoader.load(ClassLoader.getSystemResource("login.fxml"));
		Scene scene = new Scene(root);

		stage.setTitle("Login - CloudBox");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.show();
	}

}
