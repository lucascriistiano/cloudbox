package br.ufrn.cloudbox.server.dao;

import java.util.Map;

import br.ufrn.cloudbox.model.User;
import br.ufrn.cloudbox.server.model.Operation;

public interface IOperationDao {

	public Long registerOperation(Operation operation);
	public Operation getLastOperation(User user, String relativeFilePath);
	public Map<String, Operation> getFilesOperations(User user);
	
}
