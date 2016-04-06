package br.ufrn.cloudbox.client.service;

import java.io.IOException;
import java.net.URISyntaxException;

import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.model.User;

public class ServerChangeListener extends Thread {

	public static final int UPDATE_TIME = 5; //In seconds

	private OperationExecutor operationExecutor;

	private User user;
	private String absolutePathRootDirectory;
	private boolean listening;


	public ServerChangeListener(OperationExecutor operationExecutor, User user, String absolutePathRootDirectory) {
		this.operationExecutor = operationExecutor;
		this.user = user;
		this.absolutePathRootDirectory = absolutePathRootDirectory;
		this.listening = true;
	}

	@Override
	public void run() {
		try {
			while (this.listening) {
				this.operationExecutor.syncFilesWithServer(this.user, this.absolutePathRootDirectory);
				sleep(UPDATE_TIME * 1000);
			}
		} catch (ConnectionException e) {
			System.out.println("Error while getting updated files from server. Error: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopListening() {
		this.listening = false;
	}

}
