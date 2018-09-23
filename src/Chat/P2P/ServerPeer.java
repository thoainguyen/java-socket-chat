
import java.io. * ;
import java.net. * ;

/**
 * This is the chat server program.
 * Press Ctrl + C to terminate the program.
 *
 * @author ThoaiNguyen
 */
public class ServerPeer {
	private int port;

	public ServerPeer(int port) {
		this.port = port;
	}

	public void execute() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			Socket socket = serverSocket.accept();
			new ReadThread(socket).start();
			new WriteThread(socket, "SERVER").start();

		}
		catch (IOException ex) {
			System.out.println("Error in the server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void main(String[]args) {
		if (args.length < 1)
			return;

		int port = Integer.parseInt(args[0]);

		ServerPeer server = new ServerPeer(port);
		server.execute();
	}
}