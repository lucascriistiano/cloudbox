package br.ufrn.cloudbox.client.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.model.User;

public class ServerChangeListener extends Thread {

	public static final int UPDATE_TIME = 5; //In seconds

	private OperationExecutor operationExecutor;

	private User user;
	private String absolutePathRootDirectory;
	private boolean listening;

	private List<String> removeEventsFileList;

	public ServerChangeListener(OperationExecutor operationExecutor, User user, String absolutePathRootDirectory, List<String> removeEventsFileList) {
		this.operationExecutor = operationExecutor;
		this.user = user;
		this.absolutePathRootDirectory = absolutePathRootDirectory;
		this.removeEventsFileList = removeEventsFileList;
		this.listening = true;
	}

	@Override
	public void run() {
		try {
			while (this.listening) {
				//Remove files with occurred delete events on server
				Iterator<String> iterator = removeEventsFileList.iterator();
				while(iterator.hasNext()) {
					String relativePath = iterator.next();
					this.operationExecutor.deleteFileOnServer(user, relativePath);
					iterator.remove();
				}
				
				this.operationExecutor.syncFilesWithServer(this.user, this.absolutePathRootDirectory);
				sleep(UPDATE_TIME * 1000);
			}
		} catch (ConnectionException e) {
			System.out.println("Error while getting updated files from server. Error: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Server change listener interrupted.");
		} catch (URISyntaxException e) {
			System.out.println("Error while getting updated files from server. Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error while getting updated files from server. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void stopListening() {
		this.listening = false;
	}

}
