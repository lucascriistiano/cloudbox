package br.ufrn.cloudbox.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Request implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6177538315255452337L;

	private OperationCode operationCode;
	private User user;

	private String relativePath;
	private long fileSize;
	private Date lastModified;

	private List<FileInfo> fileInfoList;
	
	public OperationCode getOperation() {
		return operationCode;
	}

	public void setOperation(OperationCode operationCode) {
		this.operationCode = operationCode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRelativePath() {
		return relativePath;
	}
	
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public List<FileInfo> getFileInfoList() {
		return fileInfoList;
	}
	
	public void setFileInfoList(List<FileInfo> fileInfoList) {
		this.fileInfoList = fileInfoList;
	}
	
}
