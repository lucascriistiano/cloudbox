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
		byte[] buffer = new byte[4096];

		while (fileInputStream.read(buffer) > 0) {
			outputStream.write(buffer);
		}
		fileInputStream.close();
	}
	
	//TODO Check if file exists and override it
	public static File receiveFile(InputStream inputStream, String absoluteOutputFilePath, long fileSize, Date lastModified) throws IOException {
		File outputFile = new File(absoluteOutputFilePath);
		outputFile.getParentFile().mkdirs();
		
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		byte[] buffer = new byte[4096];
		
		int filesize = (int) fileSize; // Send file size in separate msg
		int read = 0;
//		int totalRead = 0;
		int remaining = filesize;
		while((read = inputStream.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
//			totalRead += read;
			remaining -= read;
//			System.out.println("read " + totalRead + " bytes.");
			fileOutputStream.write(buffer, 0, read);
		}
		fileOutputStream.close();
		
		outputFile.setLastModified(lastModified.getTime());
		return outputFile;
	}
	
}
