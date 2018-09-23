package chatapp.server;

import java.net. * ;
import java.io. * ;

public class ServerThread implements Runnable {
	private ChatServer server;
	private Socket client;

	public ServerThread(ChatServer serv, Socket sock) {
		this.server = serv;
		this.client = sock;
	}

	public void run() {

		try {
			InputStream input = client.getInputStream();
			OutputStream output = client.getOutputStream();

			handleClientRequest(input, output);

		} catch (IOException e) {}

	}

	public void handleClientRequest(InputStream input, OutputStream output) {
		String currentUser = "";
		for(;;){
			String message = receive(input);
			if(message == "" || message[0] == "0"){ // Close connection
				server.off(currentUser);
				input.close();
				output.close();
				this.client.close();
				break;
			}
			else if(message[0] == "1"){	// SignIn	vd: "1,ThoaiNguyen,abc123"
				
			}
			else if(message[0] == "2"){	// SignUp	vd: "1,ThoaiNguyen,abc123"
				
			}
			else if(message[0] == "3"){ // GetList	vd: "3,all", "3,on" , "3,off"
				
			}
			else if(message[0] == "4"){ // Connect  vd: "4,MinhTham"
				
			}
			else{ // Error
				
			}
		}
	}

	public String receive(InputStream in) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int nextByte;
		while ((nextByte = in.read()) != -1){
			buf.write(nextByte);
		}
		return String(buf.toByteArray());
	}
	public void send(String message, OutputStream out) {
		out.write(message.getBytes());
	}
}
