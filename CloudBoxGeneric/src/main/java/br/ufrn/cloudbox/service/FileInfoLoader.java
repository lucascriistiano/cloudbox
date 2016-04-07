package br.ufrn.cloudbox.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.cloudbox.model.FileInfo;

public class FileInfoLoader {
	
	public static List<FileInfo> loadDirectoryFileInfo(String directory) throws IOException, URISyntaxException {
		File directoryFile = new File(directory);
		Path directoryPath = directoryFile.toPath();
		
		List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
		Files.walk(directoryPath).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && Files.isReadable(filePath)) {
				File file = new File(filePath.toUri());
				Date lastModified = new Date(file.lastModified());
				
				//Get file path as relative of the root directory
				String relativePath = directoryFile.toURI().relativize(file.toURI()).getPath();
				
				FileInfo fileInfo = new FileInfo(relativePath, lastModified);
				fileInfoList.add(fileInfo);
			}
		});
		return fileInfoList;
	}
	
	public static String buildAbsoluteFilePath(String absoluteOutputPathRoot, String localRelativePath) {
		return absoluteOutputPathRoot + File.separatorChar + localRelativePath;
	}
}
