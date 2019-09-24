import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConnectionHandler implements Runnable {
	
	private Socket client;
	private List<RestServer> serverList;
	
	public ConnectionHandler(Socket client) {
		this.client = client;
		this.serverList = new ArrayList<RestServer>();
	}

	@Override
	public void run() {
		try {
			serverList.add(new RestServer("localhost"));
			serverList.add(new RestServer("localhost"));
			
			Scanner s = new Scanner(client.getInputStream());
			
			while(s.hasNextLine()) {
				String message = s.nextLine();
				
				System.out.println("Recebido: " + message);
			}
			
			s.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
