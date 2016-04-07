package br.ufrn.cloudbox.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class FilesTransfer {
	
	public static void sendFile(OutputStream outputStream, String absoluteFilePath) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(absoluteFilePath);
		byte[] buffer = new byte[8192];
		int count;
		while ((count = fileInputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, count);
		}
		outputStream.flush();
		
		fileInputStream.close();
	}
	
	public static File receiveFile(InputStream inputStream, String absoluteOutputFilePath, long fileSize, Date lastModified) throws IOException {
		File outputFile = new File(absoluteOutputFilePath);
		outputFile.getParentFile().mkdirs();
		
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		byte[] buffer = new byte[8192];
		
		int filesize = (int) fileSize; // Send file size in separate msg
		int remaining = filesize;
		int read = 0;
		while((read = inputStream.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			remaining -= read;
			fileOutputStream.write(buffer, 0, read);
		}
		fileOutputStream.close();
		
		outputFile.setLastModified(lastModified.getTime());
		return outputFile;
	}
	
}
