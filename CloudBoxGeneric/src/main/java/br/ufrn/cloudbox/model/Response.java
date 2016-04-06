package br.ufrn.cloudbox.model;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2719225610861290453L;
	
	private ResponseCode responseCode;
	private ErrorCode errorCode;

	private User user;
	private List<FileInfo> fileInfoList;
	
	private File file;
	private long fileSize;
	
	public ResponseCode getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public List<FileInfo> getFileInfoList() {
		return fileInfoList;
	}

	public void setFileInfoList(List<FileInfo> fileInfoList) {
		this.fileInfoList = fileInfoList;
	}

	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
