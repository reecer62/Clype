package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import main.ClypeServer;

import data.ClypeData;
import data.MessageClypeData;

/**
 * This class implements the Runnable interface and handles sending and receiving data.
 */
public class ServerSideClientIO implements Runnable {
	private ClypeData dataToReceiveFromClient;
	private ClypeData dataToSendToClient;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	private boolean closeConnection;
	private ClypeServer server;
	private Socket clientSocket;
	String username;
	
	/**
	 * This constructor should set the ClypeServer instance variable 'this.server' and Socket instance variable 'this.clientSocket' in this class respectively to the value of the ClypeServer object 'server' and Socket object 'clientSocket' fed as parameters to it.
	 * The 'closeConnection' variable is initialized to false.
	 * All other variables are initialized to null.
	 * @param server is a ClypeServer object.
	 * @param clientSocket is a Socket object.
	 */
	public ServerSideClientIO(ClypeServer server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
		this.closeConnection = false;
		this.dataToReceiveFromClient = null;
		this.dataToSendToClient = null;
		this.outToClient = null;
		this.inFromClient = null;
	}
	
	/**
	 * This method gets the object output and input streams of the client from 'clientSocket', and contains a while loop that runs as long as the connection is open.
	 * In the loop, the method calls the 'receiveData()' method to get data from the associated client and calls a new method 'broadcast()' in the 'this.server' object to broadcast the data from the associated client to all clients.
	 * Briefly, the broadcast method in the server will in turn call the sendData() method of each ServerSideClientIO object used in threading to send broadcasted data to each client.
	 * This will enable all clients to receive the data sent from any particular client.
	 * If the dataToSendToClient is not a message or sendfile routine then check if the data is one of the signifiers "r" or "p".
	 * If the signifier is "p" then set this.username to the data's username.
	 * If the signifier is "r" then set the data being sent to the client to a new MessageClypeData that will add this.username to the list of users in the Clype chat and then send back the packet of data.
	 */
	@Override
	public void run() {
		try {
			this.outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
			this.inFromClient = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException ioe) {
			System.err.println("I/O error occurred in run() method of ServerSideClientIO.");
		}
		while(!closeConnection) {
			receiveData();
			dataToSendToClient = dataToReceiveFromClient;
			if(dataToSendToClient.getType() != 0) {
				if(dataToSendToClient.getType() != 1) {
					this.server.broadcast(dataToSendToClient);
				}
			} else {
				//gives this client a client with a username
				if (dataToSendToClient.getData().equals("p")) {
					this.username = dataToSendToClient.getUserName();
				} else if (dataToSendToClient.getData().equals("r")) {
					dataToSendToClient = new MessageClypeData(this.username, server.returnUserNames(), "TIME",  0);
					sendData();
				}
			}
		}
	}
	
	/**
	 * This method sends data to the corresponding client.
	 * Writes out the ClypeData object ‘dataToSendToClient’ to	the	ObjectOutputStream ‘outToClient’.
	 */
	public void sendData() {
			try {
				outToClient.writeObject(dataToSendToClient);
			} catch (IOException ioe) {
				System.err.println("I/O Error occurred in sendData() method in ServerSideClientIO.");
			}
	}
	
	/**
	 * This is a simple mutator that sets the variable 'this.dataToSendToClient' in this class to the parameter sent to the method.
	 * @param dataToSendToClient is a ClypeData object, the data to send to the client.
	 */
	public void setDataToSendToClient(ClypeData dataToSendToClient) {
		this.dataToSendToClient = dataToSendToClient;
	}
	
	/**
	 * Reads in a ClypeData object from the ObjectInputStream ‘inFromClient’ into ‘dataToReceiveFromClient’
	 * Checks if the data is from the initial packet of data sent from the client by checking the signifiers.  If it's a signifier then don't print that signifier.
	 * This method then receives data from the corresponding client.
	 * When this method tests if the connection should be closed, it signals the server to remove this client using the remove() method in ClypeServer.
	 */
	public void receiveData() {
		try {
			dataToReceiveFromClient = (ClypeData)inFromClient.readObject();
			if(dataToReceiveFromClient.getData("TIME").equals("w") || dataToReceiveFromClient.getData("TIME").equals("y")) {
				//Do nothing
			} else {
				System.out.println("Data from client: \n" +  dataToReceiveFromClient.getData("TIME"));
			}
			
			if (dataToReceiveFromClient.getType() == 1) {
				closeConnection = true;
				dataToSendToClient = dataToReceiveFromClient;
				sendData();
				server.remove(this);
			}
		} catch (IOException ioe) {
			System.err.println("I/O Error occurred in receiveData() method of ServerSideClientIO.");
		} catch (ClassNotFoundException cnfe) {
			System.err.println("Class not locatable.");
		}
	}
	
	/**
	 * Override the equals method.
	 */
	@Override
	public boolean equals(Object other) {
		ServerSideClientIO otherServerSideClientIO = (ServerSideClientIO)other;
		return this.closeConnection == otherServerSideClientIO.closeConnection &&
				this.dataToReceiveFromClient.equals(otherServerSideClientIO.dataToReceiveFromClient) &&
				this.dataToSendToClient.equals(otherServerSideClientIO.dataToSendToClient) &&
				this.outToClient.equals(otherServerSideClientIO.outToClient) &&
				this.inFromClient.equals(otherServerSideClientIO.inFromClient) &&
				this.server.equals(otherServerSideClientIO.server) &&
				this.clientSocket.equals(otherServerSideClientIO.clientSocket);
	}
	
}
