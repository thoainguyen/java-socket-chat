import java.io.*;
public class RuntimeDemo {
	public static void main(String[]args) {
		try {
			Process process1 = Runtime.getRuntime().exec("cmd.exe /c start java ServerPeer 9929");
			Process process2 = Runtime.getRuntime().exec("cmd.exe /c start java ClientPeer localhost 9929");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
