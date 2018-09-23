package chatapp.server;
import java.util. * ;
import java.net. * ;
import java.io. * ;

public class ChatServer {
	private Set < User > listUsers;
	private ServerSocket server;

	public ChatServer(int port) {
		listUsers = new HashSet <  > ();
		server = new ServerSocket(port);
	}

	public addUser(String userName, String userPass) {
		User user = new User(userName, userPass);
		listUsers.add(user);
	}
	

	public void start() {
		for (; ;) {
			try {
				Socket clnSock = servSock.accept();
				Thread thread = new Thread(new ServerThread(this, clnSock));
				thread.start();
			} catch (IOException e) {}
		}
	}
	
}
