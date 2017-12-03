package main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import data.ClypeData;

/**
 * @author Reece Emero
 * This class is a blueprint for a ClypeServer object.
 */

public class ClypeServer {
	private int port;
	private boolean closeConnection = false;
	private static final int DEFAULT_PORT = 7000;
	private ArrayList<ServerSideClientIO> serverSideClientIOList;
	
	/**
	 * Constructor that sets port number and initializes the ArrayList to a new empty ArrayList.
	 * Throws an IllegalArgumentException if the port number is less than 1024
	 * @param port The port number on server connected to
	 */
	public ClypeServer(int port) throws IllegalArgumentException {
		if(port < 1024) {
			throw new IllegalArgumentException("The port number cannot be less than 1024.");
		}
		this.port = port;
		this.serverSideClientIOList = new ArrayList<ServerSideClientIO>();
	}
	
	/**
	 * Default constructor that sets port to default port number 7000
	 */
	public ClypeServer() {
		this(DEFAULT_PORT);
	}
	
	/**
	 * Create a StringBuilder called "strList".
	 * Iterates through the "ServerSideClientIOList", getting each ServerSideClientIO, and appending it to the string list "strList".
	 * Then it trims the ', "' at the end of the string list.
	 * Finally it returns the list of usernames in "strList".
	 * @return strList a list of the usernames currently connected to the Clype server.
	 */
	public String returnUserNames() {
		//List synchronized...
		StringBuilder strList = new StringBuilder();
		for(int i = 0; i < serverSideClientIOList.size(); i++) {
			ServerSideClientIO serverSideClientIOObject = serverSideClientIOList.get(i);
			strList.append(serverSideClientIOObject.username + ", ");
		}
		if(strList.length() > 0) {
			strList.setLength(strList.length()-2);
		}
		return strList.toString();
	}
	
	/**
	 * This is a synchronized method that takes in a single ClypeData object 'dataToBroadcastToClients'.
	 * Iterates through the list 'serverSideClientIOList'.
	 * For every object in the list, calls the object's setDataToSendToClient() method to set the instance variable 'dataToSendToClient' in that object.
	 * Then, it calls the object's sendData() method to force the object to send the data to the corresponding client.
	 * @param dataToBroadcastToClients is a ClypeData object.
	 */
	public synchronized void broadcast(ClypeData dataToBroadcastToClients) {
		//List synchronized...
		for(int i = 0; i < serverSideClientIOList.size(); i++) {
			ServerSideClientIO serverSideClientIOObject = serverSideClientIOList.get(i);
			serverSideClientIOObject.setDataToSendToClient(dataToBroadcastToClients);
			serverSideClientIOObject.sendData();
		}
	}
	
	/**
	 * This is a synchronized method that takes in a single ServerSideClientIO object, and removes this object from the list 'serverSideClientIOList'.
	 * @param serverSideClientToRemove is a ServerSideClientIO object.
	 */
	public synchronized void remove(ServerSideClientIO serverSideClientToRemove) {
		serverSideClientIOList.remove(serverSideClientToRemove);
	}
	
	/**
	 * This method first opens a ServerSocket object.
	 * Next, it accepts clients in a while loop that goes on as long as the connection is open.
	 * For every accepted client, this method creates a new ServerSideClientIO object and adds it to the ArrayList 'serverSideClientIOList'.
	 * Then, this method creates a new Thread object by wrapping the Thread class constructor around the ServerSideClientIO object, and start the Thread object.
	 * Closes the ServerSocket 'sskt' outside the while loop.
	 */
	public void start() {
		try {
			ServerSocket sskt = new ServerSocket(getPort());
			while(!closeConnection) {
				Socket clientSkt = sskt.accept();
				ServerSideClientIO serverSideClientIORunnable = new ServerSideClientIO(this, clientSkt);
				serverSideClientIOList.add(serverSideClientIORunnable);
				Thread serverSideClientIOThread = new Thread(serverSideClientIORunnable);
				serverSideClientIOThread.start();
			}
			//Close any I/O related resources
			if(closeConnection == true) {
				sskt.close();
			}
		} catch (IOException ioe) {
			System.err.println("I/O error occurred in start() method in ClypeServer.");
		}
	}
	
	/**
	 * This accessor returns the port number
	 * @return the current port number
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Overrides the hashCode method and creates hash code based on instance variables
	 */
	public int hashCode() {
		int result = 17;
		result = result*37 + port;
		result = result*37 + (closeConnection ? 0 : 1);
		/*
		result = result*37 + dataToSendToClient.hashCode();
		result = result*37 + dataToReceiveFromClient.hashCode();
		*/
		return result;
	}
		
	/**
	 * Overrides the equals method and checks if two ClypeServer objects are the same
	 */
	public boolean equals(Object other) {
		ClypeServer otherServer = (ClypeServer)other;
		/*if (this.dataToReceiveFromClient == null || otherServer.dataToReceiveFromClient == null) {
			return false;
		}
		if (this.dataToSendToClient == null || otherServer.dataToSendToClient == null) {
			return false;
		}*/
		return this.port == otherServer.port &&
				this.closeConnection == otherServer.closeConnection;
				//this.dataToReceiveFromClient == otherServer.dataToReceiveFromClient &&
				//this.dataToSendToClient == otherServer.dataToSendToClient;
	}
	
	/**
	 * Overrides the toString method and returns a full description of the class with all instance variables
	 */
	public String toString() {
		return
				"This class is a blueprint for a ClypeServer object. \n" +
				"The port number is: " + getPort() + "\n" +
				"Is the connection closed? (False=open): " + this.closeConnection + "\n" +
				"The hash code is: " + hashCode() + //"\n" +
				//"dataToSendToClient status (should be null): " + this.dataToSendToClient + "\n" +
				//"dataToReceiveFromClient status (should be null): " + this.dataToReceiveFromClient + "\n" +
				"\n";
	}
	
	/**
	 * Main method, when you run the jar file these instructions are run.
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			ClypeServer defaultServer = new ClypeServer();
			defaultServer.start();
		} else if(args.length == 1) {
			ClypeServer serverWithPort = new ClypeServer(Integer.valueOf(args[0]));
			serverWithPort.start();
		} else {
			System.out.println("Please use this format: 'java ClypeServer <portnumber>");
		}
	}
}
