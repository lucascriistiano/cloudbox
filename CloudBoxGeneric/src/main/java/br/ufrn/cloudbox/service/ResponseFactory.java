package br.ufrn.cloudbox.service;

import br.ufrn.cloudbox.model.ErrorCode;
import br.ufrn.cloudbox.model.Response;
import br.ufrn.cloudbox.model.ResponseCode;

public class ResponseFactory {

	public static Response createResponseOK() {
		Response response = new Response();
		response.setResponseCode(ResponseCode.OK);
		return response;
	}

	public static Response createResponseERROR(ErrorCode errorCode) {
		Response response = new Response();
		response.setResponseCode(ResponseCode.ERROR);
		response.setErrorCode(errorCode);
		return response;
	}

	public static Response createResponseUNKNOWN() {
		Response response = new Response();
		response.setResponseCode(ResponseCode.UNKNOWN_OPERATION);
		return response;
	}
	
}
