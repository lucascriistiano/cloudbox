package br.ufrn.cloudbox.server.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufrn.cloudbox.exception.DuplicatedUserException;
import br.ufrn.cloudbox.exception.FileListingException;
import br.ufrn.cloudbox.exception.UserNotFoundException;
import br.ufrn.cloudbox.model.FileInfo;
import br.ufrn.cloudbox.model.FileOperation;
import br.ufrn.cloudbox.model.User;
import br.ufrn.cloudbox.server.dao.HibernateOperationDao;
import br.ufrn.cloudbox.server.dao.HibernateUserDao;
import br.ufrn.cloudbox.server.dao.IOperationDao;
import br.ufrn.cloudbox.server.dao.IUserDao;
import br.ufrn.cloudbox.server.model.Operation;

public class OperationExecutor {

	private static final Logger logger = LogManager.getLogger(OperationExecutor.class);

	private static final String FOLDER_NAME = "CloudBoxServerFiles";
	public static final String SERVER_BASEPATH = System.getProperty("user.home") + File.separatorChar + FOLDER_NAME;
	private static OperationExecutor instance;

	private IUserDao userDao;
	private IOperationDao operationDao;

	public static OperationExecutor getInstance() {
		if (instance == null) {
			instance = new OperationExecutor();
		}
		return instance;
	}

	private OperationExecutor() {
		this.userDao = HibernateUserDao.getInstance();
		this.operationDao = HibernateOperationDao.getInstance();
	}

	public User login(User user) throws UserNotFoundException {
		String email = user.getEmail();
		String password = user.getPassword();

		User foundUser = this.userDao.findByEmailAndPassword(email, password);
		if (foundUser == null) {
			throw new UserNotFoundException();
		}
		return foundUser;
	}

	public void register(User user) throws DuplicatedUserException {
		String email = user.getEmail();
		User userWithSameEmail = this.userDao.findByEmail(email);

		if (userWithSameEmail != null) {
			throw new DuplicatedUserException();
		}

		Long userId = this.userDao.register(user);

		// Creates the directory to the user
		new File(SERVER_BASEPATH + File.separatorChar + userId).mkdir();
	}

	public List<FileInfo> syncFilesInfoList(User user, List<FileInfo> clientFileInfoList) throws FileListingException {
		Map<String, Operation> serverFilesOperations = this.operationDao.getFilesOperations(user);

		List<FileInfo> responseFileInfoList = new ArrayList<FileInfo>();

		List<FileInfo> changesForClientList = checkChangesForClient(clientFileInfoList, serverFilesOperations);
		responseFileInfoList.addAll(changesForClientList);

		List<FileInfo> changesForServerList = checkChangesForServer(clientFileInfoList, serverFilesOperations);
		responseFileInfoList.addAll(changesForServerList);

		return responseFileInfoList;
	}

	/**
	 * Check files that client doesn't have, have a outdated version or a
	 * deleted file.
	 * 
	 * @param clientFileInfoList
	 * @return
	 */
	private List<FileInfo> checkChangesForClient(List<FileInfo> clientFileInfoList,
			Map<String, Operation> serverFilesOperations) {
		List<FileInfo> responseFileInfoList = new ArrayList<FileInfo>();
		for (Entry<String, Operation> entry : serverFilesOperations.entrySet()) {
			Operation serverOperation = entry.getValue();
			String relativeFilePath = serverOperation.getRelativeFilePath();
			Date serverFileLastModified = serverOperation.getDatetime();
			String operationType = serverOperation.getType();

			FileInfo serverFileInfo = new FileInfo(relativeFilePath, serverOperation.getDatetime());

			if (!clientFileInfoList.contains(serverFileInfo)) {
				if (operationType.equals(Operation.CREATE) || operationType.equals(Operation.UPDATE)) {
					FileInfo newFileInfo = new FileInfo(relativeFilePath, serverFileLastModified,
							FileOperation.GET_FROM_SERVER);
					responseFileInfoList.add(newFileInfo);

					System.out.println(operationType);
					System.out.println(newFileInfo);
					System.out.println("Adicionou no 1");
				}
			} else {
				int clientFileInfoIndex = clientFileInfoList.indexOf(serverFileInfo);
				FileInfo clientFileInfo = clientFileInfoList.get(clientFileInfoIndex);

				Date clientLastModified = clientFileInfo.getLastModified();
				if (operationType.equals(Operation.DELETE)) {
					FileInfo newFileInfo = new FileInfo(relativeFilePath, clientLastModified,
							FileOperation.DELETE_ON_CLIENT);
					responseFileInfoList.add(newFileInfo);
				} else if (operationType.equals(Operation.CREATE) || operationType.equals(Operation.UPDATE)) {
					if (clientLastModified.before(serverFileLastModified)) {
						FileInfo newFileInfo = new FileInfo(relativeFilePath, serverFileLastModified,
								FileOperation.GET_FROM_SERVER);
						responseFileInfoList.add(newFileInfo);
						System.out.println("Adicionou no 2");
					}
				}
			}
		}
		return responseFileInfoList;
	}

	/**
	 * Check files that server doesn't have or have an out of date version
	 * 
	 * @param clientFileInfoList
	 * @param serverFilesOperations
	 * @return
	 */
	private List<FileInfo> checkChangesForServer(List<FileInfo> clientFileInfoList,
			Map<String, Operation> serverFilesOperations) {
		List<FileInfo> responseFileInfoList = new ArrayList<FileInfo>();
		for (FileInfo clientFileInfo : clientFileInfoList) {
			String relativeFilePath = clientFileInfo.getRelativePath();
			Date clientLastModified = clientFileInfo.getLastModified();

			if (!serverFilesOperations.containsKey(relativeFilePath)) {
				// Server has no operation to this file (it's a new file)
				FileInfo newFileInfo = new FileInfo(relativeFilePath, clientLastModified, FileOperation.SEND_TO_SERVER);
				responseFileInfoList.add(newFileInfo);
			} else {
				Operation lastOperation = serverFilesOperations.get(relativeFilePath);
				Date lastOperationDatetime = lastOperation.getDatetime();
				String serverLastOperationType = lastOperation.getType();

				if (!clientLastModified.equals(lastOperationDatetime)
						&& clientLastModified.after(lastOperationDatetime)) {
					if (serverLastOperationType.equals(Operation.CREATE)
							|| serverLastOperationType.equals(Operation.UPDATE)) {
						// Server has a outdated version of the file
						FileInfo newFileInfo = new FileInfo(relativeFilePath, clientLastModified,
								FileOperation.SEND_TO_SERVER);
						responseFileInfoList.add(newFileInfo);
					}
				}
			}
		}
		return responseFileInfoList;
	}

	public void deleteFileOrFolderOnDisk(User user, String relativePath) throws IOException {
		String absoluteFilePath = getAbsoluteFilePath(user, relativePath);

		File fileToDelete = new File(absoluteFilePath);
		if (fileToDelete.isDirectory()) {
			FileUtils.deleteDirectory(fileToDelete);
			logger.info("Directory '" + absoluteFilePath + "' deleted.");
		} else {
			fileToDelete.delete();
			logger.info("File '" + absoluteFilePath + "' deleted.");
		}
	}

	public static String getAbsoluteFilePath(User user, String relativePath) {
		return SERVER_BASEPATH + File.separatorChar + user.getId() + File.separatorChar + relativePath;
	}

}
