package br.ufrn.cloudbox.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7215583134965014833L;
	
	String relativeFilePath;
	Date lastModified;
	
	FileOperation fileOperation;
	
	public FileInfo(String relativeFilePath, Date lastModified) {
		this.relativeFilePath = relativeFilePath;
		this.lastModified = lastModified;
	}
	
	public FileInfo(String relativeFilePath, Date lastModified, FileOperation fileOperation) {
		this(relativeFilePath, lastModified);
		this.fileOperation = fileOperation;
	}
	
	public String getRelativePath() {
		return relativeFilePath;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public FileOperation getFileOperation() {
		return fileOperation;
	}
	
	public void setFileOperation(FileOperation fileOperation) {
		this.fileOperation = fileOperation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relativeFilePath == null) ? 0 : relativeFilePath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (relativeFilePath == null) {
			if (other.relativeFilePath != null)
				return false;
		} else if (!relativeFilePath.equals(other.relativeFilePath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.S");
		return relativeFilePath + " - " + simpleDateFormat.format(lastModified);
	}
	
}
