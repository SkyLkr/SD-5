import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		try {
			
			URL url = new URL("http://localhost:8080/Server/facerec");
			
			URLConnection connection = url.openConnection();
			long before = System.currentTimeMillis();
			connection.connect();
			long after = System.currentTimeMillis();
			
			System.out.println("Latency: " + (after-before) + "ms");
			
			Scanner in = new Scanner(connection.getInputStream());
			
			String json = in.nextLine();
			
			System.out.println(json);
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
