import java.net.*;
import java.io.*;


public class Client{
	
	ServerSocket server = null;
	int peerPort;
	Socket sock = null;
	
	public Client(String host, int serPort, int peerPort){
		this.peerPort = peerPort;
		server = new ServerSocket(peerPort);
		sock = new Socket(host,serPort);
	}
	
	public void execute(String command, Socket sock){
		InputStream input = sock.getInputStream();
		OutputStream output = sock.getOutputStream();
		
		switch(command){
			case "SIGNIN":
				signIn(output);
				break;
			case "SIGNUP":
				signUp(output);
				break;
			case "CONNECT":
				connectTo(output);
				break;
			case "GETLIST":
				getList(output);
				break;
			case "CLOSE":
				closeConnect(output);
				break;
			default:
				System.out.println("Error pass input");
		}
		
	}
	public void closeConnect(OutputStream out){
		String text = "CLOSE";
		sendMessage(text,out);
	}
	
	public void signIn(OutputStream out){ // SIGNIN,<UserName>,<UserPass>
		Console console = System.console();
        String text = "SIGNIN";
		System.out.println("UserName: ");
		text += "," + console.readLine();
		System.out.println("UserPass: ");
		text += "," + console.readLine();
		sendMessage(text,out);
	}
	public void signUp(OutputStream out){// SIGNUP,<UserName>,<UserPass>
		Console console = System.console();
        String text = "SIGNUP";
		System.out.println("UserName: ");
		text += "," + console.readLine();
		System.out.println("UserPass: ");
		text += "," + console.readLine();
		sendMessage(text,out);
	}
	public void connectTo(OutputStream out){ // CONNECT,<NguoiMuonNoiChuyen>
		Console console = System.console();
        String text = "CONNECT";
		System.out.println("Connect To: ");
		text += "," + console.readLine();
		sendMessage(text,out);
	}
	public void getList(OutputStream out){ //GETLIST
		String text = "GETLIST";
		sendMessage(text,out);
	}
	
	public void sendMessage(OutputStream out){
		out.write(message.getBytes());
	}
	
	public String receiveMessage(InputStream inp){
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		for(int nxt = 0; (nxt = in.read()) != -1;)
			buf.write(nextByte);
		return String(buf.toByteArray()); 
	}
	
	public void sendFile(String fileDir, OutputStream out){  // Ham dung de gui file
		File myFile = new File (fileDir);
		byte [] byteArray  = new byte [(int)myFile.length()];
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(byteArray,0,byteArray.length);
        out.write(byteArray,0,byteArray.length);
        out.flush();
	}
	
	public void recvFile(String fileName, InputStream inp){ // Ham dung de nhan file giua hai may
		int bytesRead = -1; byte[] buffer = new byte[1024];
		FileOutputStream fileOut = new FileOutputStream(fileName);
		while ((bytesRead = inp.read(buffer)) != -1)
			fileOut.write(buffer, 0, bytesRead);
	}
	
	public static void main(String [] argv){
		
	}
}