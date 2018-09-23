package chatapp.client;

import java.net.*;
import java.io.*;

public class Client{
	
	private Socket server;
	
	public Client(String host, int port){
		server = connect(host,port);
	}
	
	public Socket connect(String address, int port){
		Socket sock = new Socket(address,port);
		return sock;
	}
	
}