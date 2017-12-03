package main;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

//import Main.ClientSideServerListener;

import data.ClypeData;
import data.FileClypeData;
import data.MessageClypeData;

/**
 * 
 * @author anthonymangiacapra
 *
 *	This class implements the Client protocols for Clype.
 *
 */

public class ClypeClient {
	private String userName;
	private String hostName;
	private int port;
	boolean closeConnection; // false is open, true is closed
	private ClypeData dataToSendToServer;
	private ClypeData dataToReceiveFromServer;
	private ObjectInputStream inFromServer;
	private ObjectOutputStream outToServer;
	java.util.Scanner inFromStd;
	
	/**
	 * The constructor that sets the values of userName, hostName, and port
	 * This constructor sets the closeConnection to open 
	 * It also sets dataToSendToServer and dataToReceiveFromServer to null
	 * @param userName the name for the user of the function
	 * @param hostname the name for the host server
	 * @port the port the client is accessing the host through
	 */
	public ClypeClient(String userName, String hostName, int port){
		try {
			this.userName = userName;
			this.hostName = hostName;
			this.port = port;
			this.closeConnection = false; // set to open
			this.dataToSendToServer = null;
			this.dataToReceiveFromServer = null;
			this.inFromServer = null;
			this.outToServer = null;
		} catch(IllegalArgumentException Iae){
			System.err.println("Arguments not within required bounds");
		} if(port < 1024 || userName == null || hostName == null) {
			throw new IllegalArgumentException();
		}
	}
	/**
	 * The constructor that sets the values of userName and hostName
	 * This constructor sets the port to 7000 by default
	 * @param userName the name for the user of the function
	 * @param hostName the name for the host server
	 */
	public ClypeClient(String userName, String hostName){
			this(userName, hostName, 7000);
	}
	/**
	 * The constructor that set the value of userName
	 * This constructor sets the hostName to "localhost" by default
	 * @param username the name for the user of the function
	 * 
	 */
	public ClypeClient(String userName){
		this(userName, "localhost");
	}
	/**
	 * The default constructor
	 * Sets the userName to "Anon" by default
	 */
	public ClypeClient(){
		this("Anon");
	}
	
	/**
	 * Returns nothing
	 */
	public void start() {
		try {
			inFromStd = new Scanner(System.in);
			Socket echoSocket = new Socket(hostName, port);
			outToServer = new ObjectOutputStream(echoSocket.getOutputStream());
			inFromServer = new ObjectInputStream(echoSocket.getInputStream());
			ClientSideServerListener serverListener = new ClientSideServerListener(this);
			serverListener.start();
			while(!closeConnection) {
				readClientData();
				sendData();
//				receiveData();
//				printData();
			}
			
			
			echoSocket.close();
			outToServer.close();
			inFromServer.close();
		} catch(IOException IOE) { 
			System.err.println("IO Exception occured");
		}
	}	
	/**
	 * Takes an input and either closes the connection, lists the users, or assigns the dataToSendToServer to a FileClypeData or MessageClypeData
	 */
	public void readClientData() {
		dataToSendToServer = null;
		System.out.println("Enter input type: \nDONE \nSENDFILE \nLISTUSERS");
		String input = inFromStd.nextLine();
		if (input.equals("DONE")) {
			closeConnection = true;
		} else if (input.equals("SENDFILE")) {
			System.out.println("enter the file name");
			String fileName = inFromStd.nextLine();
			dataToSendToServer = new FileClypeData(getUserName(), fileName, 2);
			boolean caught = true;
			try {
				((FileClypeData) dataToSendToServer).readFileContents();
				caught = false;
			} finally {
				if(caught != false)
				dataToSendToServer = null;
			}
			//attempt to read the file and set to null if the file throws an exception
		} else if (input.equals("LISTUSERS")) {
			//this currently does nothing
		} else {
			dataToSendToServer = new MessageClypeData(getUserName(), input, 3);
		}
	}
	/**
	 * Returns nothing
	 */
	public void sendData() {
		try {
		outToServer.writeObject(dataToSendToServer);
		} catch(IOException IOE) {
			System.err.println("IO Exception was thrown");
		}
		
	}
	/**
	 * Returns nothing
	 */
	public void receiveData() {
		try {
		dataToReceiveFromServer = (ClypeData) inFromServer.readObject();
		} catch(IOException IOE) {
			System.err.println("IO Exception was Thrown");
		} catch(ClassNotFoundException CNF) {
			System.err.println("class not found");
		}
	}
	/**
	 * Returns nothing
	 */
	public void printData() {
		System.out.println(dataToReceiveFromServer);
	}
	/**
	 * Returns userName
	 * @return
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * Returns hostName
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}
	/**
	 * Returns port
	 * @return
	 */
	public int getPort() {
		return port;
	}
	@Override
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hash = 7;
		hash = hash * 19 + port;
		hash = hash * 37 + (closeConnection? 1 : 0);
		hash = hash * 53 + dataToReceiveFromServer.hashCode();
		hash = hash * 71 + dataToSendToServer.hashCode();
		return hash;
	}
	@Override
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		ClypeClient otherClypeClient = (ClypeClient)other;
		return this.port == otherClypeClient.port && this.closeConnection == otherClypeClient.closeConnection && this.dataToReceiveFromServer == otherClypeClient.dataToReceiveFromServer && this.dataToSendToServer == otherClypeClient.dataToSendToServer;
	}
	@Override
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "userName: " + userName + " hostName: " + hostName + " port: " + port + " closeConnection: " + closeConnection + " dataToReceiveFromServer: " + dataToReceiveFromServer + " dataToSendToServer" + dataToSendToServer;
	}
	
	public static void main(String args[]) {
		if(args.length == 0) {
			ClypeClient client = new ClypeClient();
			client.start();
		} else {
			String commandLineArgs[] = args[0].split("[@:]");
			if(commandLineArgs.length == 1) {
				ClypeClient client = new ClypeClient(commandLineArgs[0]);
				client.start();
			} else if(commandLineArgs.length == 2) {
				ClypeClient client = new ClypeClient(commandLineArgs[0], commandLineArgs[1]);
				client.start();
			} else if(commandLineArgs.length == 3) {
				ClypeClient client = new ClypeClient(commandLineArgs[0], commandLineArgs[1], Integer.valueOf(commandLineArgs[2]));
				client.start();
			}
		}
	}
	
}
