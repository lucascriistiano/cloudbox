package br.ufrn.cloudbox.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.ufrn.cloudbox.exception.DuplicatedUserException;
import br.ufrn.cloudbox.exception.FileListingException;
import br.ufrn.cloudbox.exception.UserNotFoundException;
import br.ufrn.cloudbox.model.ErrorCode;
import br.ufrn.cloudbox.model.FileInfo;
import br.ufrn.cloudbox.model.FilesTransfer;
import br.ufrn.cloudbox.model.Request;
import br.ufrn.cloudbox.model.Response;
import br.ufrn.cloudbox.model.User;
import br.ufrn.cloudbox.server.dao.HibernateOperationDao;
import br.ufrn.cloudbox.server.dao.IOperationDao;
import br.ufrn.cloudbox.server.model.Operation;
import br.ufrn.cloudbox.server.service.OperationExecutor;
import br.ufrn.cloudbox.service.ResponseFactory;

public class RequestHandler extends Thread {

	private Socket clientSocket;
	private OperationExecutor operationExecutor;

	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;
	private IOperationDao operationDao;

	private static Map<Long, User> userLockList = new ConcurrentHashMap<Long, User>();

	public RequestHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.operationExecutor = OperationExecutor.getInstance();

		this.operationDao = HibernateOperationDao.getInstance();
	}

	@Override
	public void run() {
		try {
			this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

			Request request = (Request) objectInputStream.readObject();
			switch (request.getOperation()) {
			case REGISTER:
				handleRegisterRequest(request);
				break;

			case LOGIN:
				handleLoginRequest(request);
				break;

			case LOGOUT:
				handleLogoutRequest(request);
				break;

			case SYNC_FILES:
				handleSyncFilesRequest(request);
				break;

			case GET_FILE:
				handleGetFileRequest(request);
				break;

			case SEND_FILE:
				handleSendFileRequest(request);
				break;

			case DELETE_FILE:
				handleDeleteFileRequest(request);
				break;

			default:
				handleUnknownOperationRequest();
				break;
			}

			objectOutputStream.close();
			objectInputStream.close();
			this.clientSocket.close();
		} catch (ClassNotFoundException e) {
			System.out.println("Error while sending response. Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error while handling request. Error: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Requisição finalizada");
	}

	private void handleRegisterRequest(Request request) throws IOException {
		System.out.println("Register operation requested");
		User requestUser = request.getUser();

		Response response;
		try {
			System.out.println(requestUser);
			operationExecutor.register(requestUser);
			response = ResponseFactory.createResponseOK();
		} catch (DuplicatedUserException e) {
			System.out.println("Aborting operation - Duplicated user");
			response = ResponseFactory.createResponseERROR(ErrorCode.DUPLICATED_USER);
		}
		objectOutputStream.writeObject(response);
	}

	private void handleLoginRequest(Request request) throws IOException {
		System.out.println("Login operation requested");
		User requestUser = request.getUser();

		Response response;
		try {
			User user = operationExecutor.login(requestUser);
			if (!userLockList.containsKey(user.getId())) {
				userLockList.put(user.getId(), user);
			}

			response = ResponseFactory.createResponseOK();
			response.setUser(user);
		} catch (UserNotFoundException e) {
			System.out.println("Aborting operation - Invalid email/password");
			response = ResponseFactory.createResponseERROR(ErrorCode.USER_NOT_FOUND);
		}
		objectOutputStream.writeObject(response);
	}

	// TODO Use logout on client
	private void handleLogoutRequest(Request request) throws IOException {
		System.out.println("Logout operation requested");

		User requestUser = request.getUser();
		synchronized (userLockList.get(requestUser.getId())) {
			userLockList.remove(requestUser.getId());
			Response response = ResponseFactory.createResponseOK();
			response.setUser(requestUser);
			objectOutputStream.writeObject(response);
		}
	}

	// TODO Check
	private void handleSyncFilesRequest(Request request) throws IOException {
		System.out.println("Sync files operation requested");

		User requestUser = request.getUser();
		synchronized (userLockList.get(requestUser.getId())) {
			Response response;
			try {
				List<FileInfo> clientFileInfoList = request.getFileInfoList();
				List<FileInfo> responseFileInfo = operationExecutor.syncFilesInfoList(requestUser, clientFileInfoList);
				
				response = ResponseFactory.createResponseOK();
				response.setFileInfoList(responseFileInfo);
			} catch (FileListingException e) {
				System.out.println("Aborting operation - Error while listing user files sync");
				response = ResponseFactory.createResponseERROR(ErrorCode.ERROR_WHILE_LISTING_FILES);
			}
			objectOutputStream.writeObject(response);
		}
	}

	private void handleGetFileRequest(Request request) throws IOException {
		System.out.println("Get file operation requested");

		User requestUser = request.getUser();
		synchronized (userLockList.get(requestUser.getId())) {
			String relativePath = request.getRelativePath();
			String absoluteFilePath = OperationExecutor.getAbsoluteFilePath(requestUser, relativePath);

			File requestedFile = new File(absoluteFilePath);
			Response response;
			if (requestedFile.exists()) {
				response = ResponseFactory.createResponseOK();
				response.setFileSize(requestedFile.length());
				objectOutputStream.writeObject(response);

				FilesTransfer.sendFile(this.objectOutputStream, absoluteFilePath);
				System.out.println("File '" + relativePath + "' sent!");
			} else {
				System.out.println("Aborting operation - Requested file doesn't exist");
				response = ResponseFactory.createResponseERROR(ErrorCode.FILE_NOT_FOUND);
				objectOutputStream.writeObject(response);
			}
		}
	}

	private void handleSendFileRequest(Request request) throws IOException {
		System.out.println("Send file operation requested");

		User requestUser = request.getUser();
		synchronized (userLockList.get(requestUser.getId())) {
			Response response = ResponseFactory.createResponseOK();
			objectOutputStream.writeObject(response);

			String relativePath = request.getRelativePath();
			Date lastModified = request.getLastModified();

			String absoluteOutputFilePath = OperationExecutor.getAbsoluteFilePath(requestUser, relativePath);
			System.out.println("Will receive: '" + absoluteOutputFilePath + "'");
			FilesTransfer.receiveFile(objectInputStream, absoluteOutputFilePath, request.getFileSize(), lastModified);

			this.operationDao.registerOperation(new Operation(relativePath, lastModified, Operation.UPDATE, requestUser));
		}
	}

	private void handleDeleteFileRequest(Request request) throws IOException {
		System.out.println("Delete file operation requested");

		User requestUser = request.getUser();
		synchronized (userLockList.get(requestUser.getId())) {
			String relativePath = request.getRelativePath();
			operationExecutor.deleteFileOrFolderOnDisk(requestUser, relativePath);

			Response response = ResponseFactory.createResponseOK();
			objectOutputStream.writeObject(response);

			this.operationDao.registerOperation(new Operation(relativePath, new Date(), Operation.DELETE, requestUser));
		}
	}

	private void handleUnknownOperationRequest() throws IOException {
		System.out.println("Unknown requested operation");
		Response response = ResponseFactory.createResponseUNKNOWN();
		objectOutputStream.writeObject(response);
	}

}
