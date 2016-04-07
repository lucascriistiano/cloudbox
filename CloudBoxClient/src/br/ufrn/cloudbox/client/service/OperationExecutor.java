package br.ufrn.cloudbox.client.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import br.ufrn.cloudbox.client.connection.Connection;
import br.ufrn.cloudbox.client.connection.ConnectionFactory;
import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.exception.DuplicatedUserException;
import br.ufrn.cloudbox.exception.UserNotFoundException;
import br.ufrn.cloudbox.model.ErrorCode;
import br.ufrn.cloudbox.model.FileInfo;
import br.ufrn.cloudbox.model.Request;
import br.ufrn.cloudbox.model.Response;
import br.ufrn.cloudbox.model.ResponseCode;
import br.ufrn.cloudbox.model.User;
import br.ufrn.cloudbox.service.FileInfoLoader;
import br.ufrn.cloudbox.service.RequestFactory;
import javafx.application.Platform;
import javafx.scene.text.Text;

public class OperationExecutor {

	private Text txtStatus;

	public OperationExecutor(Text txtStatus) {
		this.txtStatus = txtStatus;
	}

	public User login(String email, String password) throws ConnectionException, UserNotFoundException {
		Connection connection = ConnectionFactory.openConnection();
		Request request = RequestFactory.createLoginRequest(new User(email, password));
		Response response = connection.sendRequest(request);
		connection.close();

		if (response.getResponseCode() == ResponseCode.ERROR) {
			if (response.getErrorCode() == ErrorCode.USER_NOT_FOUND) {
				throw new UserNotFoundException();
			}
		}

		return response.getUser();
	}

	public void register(User user) throws ConnectionException, DuplicatedUserException {
		Connection connection = ConnectionFactory.openConnection();
		Request request = RequestFactory.createRegisterRequest(user);
		Response response = connection.sendRequest(request);
		connection.close();

		if (response.getResponseCode() == ResponseCode.ERROR) {
			if (response.getErrorCode() == ErrorCode.DUPLICATED_USER) {
				throw new DuplicatedUserException();
			}
		}
	}

	public void syncFilesWithServer(User user, String absolutePathRootDirectory)
			throws IOException, URISyntaxException, ConnectionException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Realizando sincronização com o servidor.");
			}
		});
		List<FileInfo> currentDirectoryFileInfoList = FileInfoLoader.loadDirectoryFileInfo(absolutePathRootDirectory);
		List<FileInfo> fileInfoListToModify = requestSyncFilesInfoList(user, currentDirectoryFileInfoList);

		processModificationFromServer(user, absolutePathRootDirectory, fileInfoListToModify);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Diretório sincronizado com o servidor.");
			}
		});
	}

	private synchronized List<FileInfo> requestSyncFilesInfoList(User user, List<FileInfo> currentDirectoryFileInfoList)
			throws ConnectionException {
		Connection connection = ConnectionFactory.openConnection();
		Request request = RequestFactory.createSyncFilesRequest(user, currentDirectoryFileInfoList);
		Response response = connection.sendRequest(request);
		connection.close();

		return response.getFileInfoList();
	}

	/**
	 * Request files from server that has not locally and send files that server
	 * doesn't have
	 */
	private void processModificationFromServer(User user, String absolutePathRootDirectory,
			List<FileInfo> fileInfoListToModify) throws ConnectionException, IOException {

		for (FileInfo fileInfo : fileInfoListToModify) {
			String relativePath = fileInfo.getRelativePath();
			String absoluteFilePath = FileInfoLoader.buildAbsoluteFilePath(absolutePathRootDirectory, relativePath);
			Date lastModified = fileInfo.getLastModified();

			switch (fileInfo.getFileOperation()) {
			case DELETE_ON_CLIENT:
				deleteFileOrFolderOnDisk(relativePath, absoluteFilePath);
				break;

			case GET_FROM_SERVER:
				saveFileFromServer(user, relativePath, absoluteFilePath, lastModified);
				break;

			case SEND_TO_SERVER:
				sendFileToServer(user, relativePath, absolutePathRootDirectory);
				break;

			default:
				break;
			}
		}
	}

	public synchronized void deleteFileOrFolderOnDisk(String relativePath, String absoluteFilePath) throws IOException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Removendo o arquivo '" + relativePath + "'...");
			}
		});

		File fileToDelete = new File(absoluteFilePath);
		if (fileToDelete.isDirectory()) {
			FileUtils.deleteDirectory(fileToDelete);
		} else {
			fileToDelete.delete();
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Arquivo '" + relativePath + "' removido!");
			}
		});
	}

	public synchronized File saveFileFromServer(User user, String relativePath, String outputFilePath,
			Date lastModified) throws ConnectionException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Transferindo o arquivo '" + relativePath + "'...");
			}
		});

		Connection connection = ConnectionFactory.openConnection();
		Request request = RequestFactory.createGetFileRequest(user, relativePath);
		Response response = connection.sendGetFileRequest(request, outputFilePath, lastModified);
		connection.close();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Arquivo '" + relativePath + "' recebido!");
			}
		});

		return response.getFile();
	}

	public synchronized void sendFileToServer(User user, String relativePath, String absolutePathRootDirectory)
			throws ConnectionException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Enviando o arquivo '" + relativePath + "' para o servidor...");
			}
		});
		
		Connection connection = ConnectionFactory.openConnection();
		Request request = RequestFactory.createSendFileRequest(user, relativePath);
		Response response = connection.sendSendFileRequest(request, absolutePathRootDirectory);
		connection.close();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtStatus.setText("Arquivo '" + relativePath + "' enviado!");
			}
		});

	}

	public synchronized void deleteFileOnServer(User user, String relativePath) throws ConnectionException {
		Connection connection = ConnectionFactory.openConnection();
		Request request = RequestFactory.createDeleteFileRequest(user, relativePath);
		Response response = connection.sendRequest(request);
		connection.close();
	}

}
