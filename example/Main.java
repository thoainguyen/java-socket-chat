import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.Thread ;

public class Main {
	public static void main(String args[])throws Exception {
		ServerSocket ssock = new ServerSocket(1234);
		while (true) {
			Socket sock = ssock.accept();
			new SocketThread(sock).start();
			System.out.println("-----------new connect-------");
		}
	}
}

class SocketThread extends Thread {
	Socket csocket;

	public SocketThread(Socket csocket) {
		this.csocket = csocket;
	}

	public void run() {
		try {
			DataOutputStream out =
				new DataOutputStream(csocket.getOutputStream());
			
			out.writeUTF("Ahihi");

			/*DataInputStream in =
				new DataInputStream(csocket.getInputStream());
			System.out.println(in.readUTF());
			in.close();*/
		
			out.close();
			csocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
