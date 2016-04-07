package br.ufrn.cloudbox.client.service;
 
import java.io.IOException;
import java.util.List;

import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.service.DirectoryWatcher;
 
public class ClientDirectoryWatcher extends DirectoryWatcher {
 
	private List<String> removeEventsFileList;
 
    public ClientDirectoryWatcher(String absoluteRootDirectory, List<String> removeEventsFileList) throws IOException {
        super(absoluteRootDirectory);
		this.removeEventsFileList = removeEventsFileList;
    }
 
    @Override
    public void processCreateEvent(String relativePath) throws ConnectionException {
    	System.out.println("CREATE: " + relativePath);
    }
 
    @Override
    public void processDeleteEvent(String relativePath) throws ConnectionException {
        System.out.println("DELETE: " + relativePath);
        removeEventsFileList.add(relativePath);
    }
 
    @Override
    public void processModifyEvent(String relativePath) throws ConnectionException {
    	System.out.println("MODIFY: " + relativePath);
    }
 
}