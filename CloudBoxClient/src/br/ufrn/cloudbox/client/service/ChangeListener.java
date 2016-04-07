package br.ufrn.cloudbox.client.service;

import java.io.IOException;

import br.ufrn.cloudbox.model.User;

public class ChangeListener extends Thread {

	private ServerChangeListener serverChangeListener;
	private ClientDirectoryWatcher clientDirectoryWatcher;

	public ChangeListener(OperationExecutor operationExecutor, User user, String absolutePathRootDirectory) throws IOException {
		this.serverChangeListener = new ServerChangeListener(operationExecutor, user, absolutePathRootDirectory);
		this.clientDirectoryWatcher = new ClientDirectoryWatcher(operationExecutor, user, absolutePathRootDirectory);
	}
	
	@Override
	public void run() {
		this.serverChangeListener.start();
		this.clientDirectoryWatcher.start();
	}
	
	public void stopListening() {
		this.serverChangeListener.stopListening();
		this.clientDirectoryWatcher.stopWatching();
		
		try {
			this.serverChangeListener.join(2000);
			this.serverChangeListener.interrupt();

			this.clientDirectoryWatcher.join(2000);
			this.clientDirectoryWatcher.interrupt();
		} catch (InterruptedException e) {
			System.out.println("Error while joining threads. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
