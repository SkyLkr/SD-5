import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	private static ServerSocket server;

	public static void main(String[] args) {
//		try {
//			
//			URL url = new URL("http://localhost:8080/Server/facerec");
//			
//			URLConnection connection = url.openConnection();
//			long before = System.currentTimeMillis();
//			connection.connect();
//			long after = System.currentTimeMillis();
//			
//			System.out.println("Latency: " + (after-before) + "ms");
//			
//			Scanner in = new Scanner(connection.getInputStream());
//			
//			String json = in.nextLine();
//			
//			System.out.println(json);
//			
//			in.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		try {
			server = new ServerSocket(333);
			System.out.println("Server aberto na porta 333");
			
			while(true) {
				Socket client = server.accept();
				
				System.out.println("Cliente " + client.getInetAddress() + " se conectou.");
				
				ConnectionHandler handler = new ConnectionHandler(client);
				Thread t = new Thread(handler);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
