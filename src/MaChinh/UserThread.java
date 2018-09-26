
 
import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 */
public class UserThread extends Thread {
    private Socket socket;
    private Server server;
 
    public UserThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }
 
    public void run() {
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		StringTokenizer st;
        try {
            
			while(true){
				st = new StringTokenizer(receiveMessage(in));
				switch(st.nextToken(",")){
					case "SIGNIN":
						signIn(st,out);
						break;
					case "SIGNUP":
						signUp(st,out);
						break;
					case "GETLIST":
						getList(st,out);
						break;
					case "CLOSE":
						closeConnect(out);
						break;
					case "CONNECT":
						connectTo(st,out);
						break;
					default:
				}
				
			}
 
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
	
	public void signIn(StringTokenizer st, OutputStream out){
		
	}
	public void signUp(StringTokenizer st, OutputStream out){
		
	}
	public void getList(StringTokenizer st, OutputStream out){
		
	}
	public void closeConnect(OutputStream out){
		
	}
	public void connectTo(StringTokenizer st, OutputStream out){
		
	}
    public void sendMessage(String message, OutputStream out){
		out.write(message.getBytes());
	}
	
	public String receiveMessage(InputStream inp){
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		for(int nxt = 0; (nxt = in.read()) != -1;)
			buf.write(nextByte);
		return String(buf.toByteArray()); 
	}
}