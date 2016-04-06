package br.ufrn.cloudbox.client.connection;

import br.ufrn.cloudbox.exception.ConnectionException;

public class ConnectionFactory {

	private static final String IP = "127.0.0.1";
//	private static final String IP = "192.168.1.110";
	private static final int PORT = 3000;
	
	public static Connection openConnection() throws ConnectionException {
		return new Connection(IP, PORT);
	}

}