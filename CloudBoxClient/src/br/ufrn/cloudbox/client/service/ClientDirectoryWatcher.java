package br.ufrn.cloudbox.client.service;
 
import java.io.IOException;
 
import br.ufrn.cloudbox.exception.ConnectionException;
import br.ufrn.cloudbox.model.User;
import br.ufrn.cloudbox.service.DirectoryWatcher;
 
public class ClientDirectoryWatcher extends DirectoryWatcher {
 
    private OperationExecutor operationExecutor;
    private User user;
 
    public ClientDirectoryWatcher(OperationExecutor operationExecutor, User user, String absoluteRootDirectory) throws IOException {
        super(absoluteRootDirectory);
        this.operationExecutor = operationExecutor;
        this.user = user;
    }
 
    @Override
    public void processCreateEvent(String relativePath) throws ConnectionException {
    	System.out.println("CREATE: " + relativePath);
//        this.operationExecutor.sendFileToServer(user, relativePath, getAbsoluteRootDirectory());
    }
 
    @Override
    public void processDeleteEvent(String relativePath) throws ConnectionException {
        System.out.println("DELETE: " + relativePath);
    	this.operationExecutor.deleteFileOnServer(user, relativePath);
    }
 
    @Override
    public void processModifyEvent(String relativePath) throws ConnectionException {
    	System.out.println("MODIFY: " + relativePath);
//        this.operationExecutor.sendFileToServer(user, relativePath, getAbsoluteRootDirectory());
    }
 
}