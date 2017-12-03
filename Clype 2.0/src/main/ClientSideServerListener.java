package main;

public class ClientSideServerListener extends Thread implements Runnable {
	private ClypeClient client;
	
	ClientSideServerListener(ClypeClient Client){
		this.client = Client;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!client.closeConnection) {
			client.receiveData();
			client.printData();
		}
	}

}