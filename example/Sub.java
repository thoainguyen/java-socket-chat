import java.net. * ;
import java.io. * ;
public class Sub {
	public static void main(String[]argv) {
		try {

			Socket client = new Socket("localhost", 1234);

			/*OutputStream outToServer = client.getOutputStream();

			DataOutputStream out = new DataOutputStream(outToServer);

			out.writeUTF("Hello from "
				 + client.getLocalSocketAddress());
			*/
			InputStream inFromServer = client.getInputStream();
			DataInputStream in =
				new DataInputStream(inFromServer);

			Thread.sleep(10000);
			
			System.out.println("Server says " + in.readUTF());
			
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){}
	}
}
