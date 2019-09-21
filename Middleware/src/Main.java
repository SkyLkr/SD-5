import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://localhost:8080/Server/facerec");
			
			Scanner in = new Scanner(url.openStream());
			
			String json = in.nextLine();
			
			System.out.println(json);
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
