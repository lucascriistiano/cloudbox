package br.ufrn.cloudbox.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import br.ufrn.cloudbox.server.service.OperationExecutor;

public class Server extends Thread {

	private int port;
	private ServerSocket serverSocket;

	public Server(int port) {
		try {
			System.out.println("Configuring server ...");
			configureServer();
			this.port = port;
			serverSocket = new ServerSocket(this.port);
			System.out.println("Server running on port " + this.port);
		} catch (IOException e) {
			System.out.println("Error while starting server. Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void configureServer() {
		// Check and create base directory if it doesn't exist
		File filesDir = new File(OperationExecutor.BASEPATH);
		if (!filesDir.exists()) {
			System.out.println("Creating user files folder ...");
			filesDir.mkdirs();
		}
	}

	@Override
	public void run() {
		System.out.println("Waiting for requests on port " + this.port);
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
