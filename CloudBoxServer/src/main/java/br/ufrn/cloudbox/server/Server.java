package br.ufrn.cloudbox.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufrn.cloudbox.server.service.OperationExecutor;

public class Server extends Thread {

	private static final Logger logger = LogManager.getLogger(Server.class);
	
	private int port;
	private ServerSocket serverSocket;

	public Server(int port) {
		try {
			logger.info("Configuring server");
			configureServer();
			this.port = port;
			serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			logger.error("Error while starting server. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void configureServer() {
		// Check and create base directory if it doesn't exist
		File filesDir = new File(OperationExecutor.SERVER_BASEPATH);
		if (!filesDir.exists()) {
			logger.info("Creating user files folder ...");
			filesDir.mkdirs();
		}
	}

	@Override
	public void run() {
		logger.info("Server running on port " + this.port);
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				new RequestHandler(clientSocket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Server(3000).run();
	}

}
