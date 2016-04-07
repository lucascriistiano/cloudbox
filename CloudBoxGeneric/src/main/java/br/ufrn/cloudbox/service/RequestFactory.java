package br.ufrn.cloudbox.service;

import java.util.List;

import br.ufrn.cloudbox.model.FileInfo;
import br.ufrn.cloudbox.model.OperationCode;
import br.ufrn.cloudbox.model.Request;
import br.ufrn.cloudbox.model.User;

public class RequestFactory {

	public static Request createLoginRequest(User user) {
		Request request = new Request();
		request.setOperation(OperationCode.LOGIN);
		request.setUser(user);

		return request;
	}
	
	public static Request createLogoutRequest(User user) {
		Request request = new Request();
		request.setOperation(OperationCode.LOGOUT);
		request.setUser(user);

		return request;
	}

	public static Request createRegisterRequest(User user) {
		Request request = new Request();
		request.setOperation(OperationCode.REGISTER);
		request.setUser(user);

		return request;
	}

	public static Request createSyncFilesRequest(User user, List<FileInfo> fileInfoList) {
		Request request = new Request();
		request.setOperation(OperationCode.SYNC_FILES);
		request.setFileInfoList(fileInfoList);
		request.setUser(user);

		return request;
	}

	public static Request createGetFileRequest(User user, String localRelativePath) {
		Request request = new Request();
		request.setOperation(OperationCode.GET_FILE);
		request.setRelativePath(localRelativePath);
		request.setUser(user);

		return request;
	}

	public static Request createSendFileRequest(User user, String localRelativePath) {
		Request request = new Request();
		request.setOperation(OperationCode.SEND_FILE);
		request.setRelativePath(localRelativePath);
		request.setUser(user);
		
		return request;
	}

	public static Request createDeleteFileRequest(User user, String localRelativePath) {
		Request request = new Request();
		request.setOperation(OperationCode.DELETE_FILE);
		request.setRelativePath(localRelativePath);
		request.setUser(user);
		
		return request;
	}

}
