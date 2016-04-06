package br.ufrn.cloudbox.client.connection;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.model.FilesTransfer;
import br.ufrn.cloudbox.model.Request;
import br.ufrn.cloudbox.model.Response;
import br.ufrn.cloudbox.model.ResponseCode;
import br.ufrn.cloudbox.service.FileInfoLoader;

public class Connection {

	private String ip;
	private int port;
	private Socket serverSocket;
	
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	public Connection(String ip, int port) throws ConnectionException {
		try {
			this.port = port;
			this.serverSocket = new Socket(this.ip, this.port);
			this.objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
			this.objectOutputStream = new ObjectOutputStream(serverSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while openning connection. Error: " + e.getMessage());
		}
	}
	
	public void close() throws ConnectionException {
		try {
			this.serverSocket.close();
			this.objectInputStream.close();
			this.objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while closing connection. Error: " + e.getMessage());
		}
	}

	public Response sendRequest(Request request) throws ConnectionException {
		Response response = null;
		try {
			objectOutputStream.writeObject(request);
			response = (Response) objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while sending request. Error: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while receiving response. Error: " + e.getMessage());
		}
		return response;
	}
	
	public Response sendGetFileRequest(Request request, String outputFilePath, Date lastModified) throws ConnectionException {
		Response response = null;
		try {
			objectOutputStream.writeObject(request);
			response = (Response) objectInputStream.readObject();
			
			if (response.getResponseCode() == ResponseCode.OK) {
				File outputFile = FilesTransfer.receiveFile(objectInputStream, outputFilePath, response.getFileSize(), lastModified);
				response.setFile(outputFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while sending get file request. Error: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while receiving file response. Error: " + e.getMessage());
		}
		return response;
	}

	public Response sendSendFileRequest(Request request, String absoluteOutputPathRoot) throws ConnectionException {
		Response response = null;
		try {
			String localRelativePath = request.getRelativePath();
			String absoluteFilePath = FileInfoLoader.buildAbsoluteFilePath(absoluteOutputPathRoot, localRelativePath);
			File file = new File(absoluteFilePath);
			request.setFileSize(file.length());
			request.setLastModified(new Date(file.lastModified()));
			objectOutputStream.writeObject(request);

			response = (Response) objectInputStream.readObject();
			if (response.getResponseCode() == ResponseCode.OK) {
				FilesTransfer.sendFile(objectOutputStream, absoluteFilePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while sending send file request. Error: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ConnectionException("Error while receiving response. Error: " + e.getMessage());
		}
		return response;
	}

}
