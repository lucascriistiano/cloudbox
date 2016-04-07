package br.ufrn.cloudbox.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufrn.cloudbox.model.User;

public class ChangeListener {

	private ServerChangeListener serverChangeListener;
	private ClientDirectoryWatcher clientDirectoryWatcher;
	
	private List<String> removeEventsFileList;

	public ChangeListener(OperationExecutor operationExecutor, User user, String absolutePathRootDirectory) throws IOException {
		this.removeEventsFileList = Collections.synchronizedList(new ArrayList<>());
		
		this.serverChangeListener = new ServerChangeListener(operationExecutor, user, absolutePathRootDirectory, removeEventsFileList);
		this.clientDirectoryWatcher = new ClientDirectoryWatcher(absolutePathRootDirectory, removeEventsFileList);
	}
	
	public void start() {
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
