
 
import java.io.*;
import java.net.*;
 
/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author ThoaiNguyen
 */
public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
	private String name;
 
    public WriteThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
 
        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    public void run() {
 
        Console console = System.console();
        String text;
 
        do {
            text = console.readLine();
            writer.println("[" + this.name + "]: " + text);
 
        } while (!text.equals("bye"));
 
        try {
            socket.close();
        } catch (IOException ex) {
 
            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}