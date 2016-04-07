package br.ufrn.cloudbox.client.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import br.ufrn.cloudbox.client.service.ChangeListener;
import br.ufrn.cloudbox.client.service.OperationExecutor;
import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.model.User;
import br.ufrn.cloudbox.service.FileInfoLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainWindowController {

	@FXML
	private TextField folderField;

	@FXML
	private Text txtStatus;

	@FXML
	private Text txtLoggedUser;

	@FXML
	private ListView<String> fileIgnoreListView;

	private OperationExecutor operationExecutor;
	private DirectoryChooser directoryChooser;
	private FileChooser fileChooser;

	private User user;
	private File selectedDirectory = null;

	private ChangeListener changeListener;

	public static final ObservableList<String> ignoredFiles = FXCollections.observableArrayList();

	public MainWindowController() {
		this.directoryChooser = new DirectoryChooser();
		this.directoryChooser.setTitle("Escolher diretório de sicronização");
		this.directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		this.fileChooser = new FileChooser();
		this.fileChooser.setTitle("Escolher arquivo a ser ignorado");
		this.fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}

	public void initData(User user) {
		this.user = user;
		this.txtLoggedUser.setText("Logado como: " + this.user.getEmail());
		this.operationExecutor = new OperationExecutor(txtStatus, ignoredFiles);

		this.fileIgnoreListView.setItems(ignoredFiles);
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

	@FXML
	public void handleAddFileIgnoreAction(ActionEvent event) {
		if (selectedDirectory != null) {
			Stage stage = (Stage) folderField.getScene().getWindow();

			File selectedFileToIgnore = fileChooser.showOpenDialog(stage);
			if (selectedFileToIgnore != null) {
				String relativeFileToIgnorePath = FileInfoLoader.getRelativeFilePath(selectedDirectory,selectedFileToIgnore);

				if(!ignoredFiles.contains(relativeFileToIgnorePath)) {
					ignoredFiles.add(relativeFileToIgnorePath);
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Arquivo já ignorado");
					alert.setHeaderText(null);
					alert.setContentText("Arquivo já havia sido adicionado na lista de arquivos ignorados anteriormente.");
					alert.showAndWait();
				}
				
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Impossível prosseguir");
			alert.setHeaderText(null);
			alert.setContentText("Selecione um diretório antes de prosseguir.");
			alert.showAndWait();
		}
	}

	@FXML
	public void handleRemoveFileIgnoreAction(ActionEvent event) {
		if (selectedDirectory != null) {
			String selectedRelativeFileToIgnorePath = fileIgnoreListView.getSelectionModel().getSelectedItem();
			if (selectedRelativeFileToIgnorePath != null) {
				ignoredFiles.remove(selectedRelativeFileToIgnorePath);
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Arquivo não selecionado");
				alert.setHeaderText(null);
				alert.setContentText("Selecione um arquivo a ser removido.");
				alert.showAndWait();	
			}
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Diretório não selecionado");
			alert.setHeaderText(null);
			alert.setContentText("Selecione um diretório antes de prosseguir.");
			alert.showAndWait();
		}
	}

	private void changeDirectory(File directory)
			throws InterruptedException, IOException, URISyntaxException, ConnectionException {
		selectedDirectory = directory;
		updateDirectoryPathExhibition();
		stopPreviousMonitorActive();

		fileChooser.setInitialDirectory(selectedDirectory);
		ignoredFiles.clear();
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
	public void handleLogoutAction(ActionEvent event) throws IOException, InterruptedException, ConnectionException {
		stopPreviousMonitorActive();
		operationExecutor.logout(user);
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
