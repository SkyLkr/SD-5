import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

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
			RestServer rest = new RestServer("localhost");
			
			// Getting file from client
			InputStream in = client.getInputStream();
			
			File saveDir = new File(System.getProperty("user.dir") + File.separator + "images");
			
			if (!saveDir.exists()) saveDir.mkdirs();
			
			File f = File.createTempFile("INPUT", ".jpg", saveDir);
			
			FileOutputStream out = new FileOutputStream(f);
			
			byte[] bytes = new byte[1024];
			
			System.out.print("Recebendo arquivo.");
			
			int count;
			while((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
				System.out.print(".");
			}
			System.out.print("\n");
			
			out.close();
			in.close();
			
			System.out.println("Image saved as " + f.getAbsolutePath());
			
			// Sending file to REST server
			FileInputStream fis = null;
			
			try {
				fis = new FileInputStream(f);
				DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
				
				HttpPost httpPost = new HttpPost(rest.getAddress() + "sendImg");
				MultipartEntity entity = new MultipartEntity();
				
				entity.addPart("file", new InputStreamBody(fis, f.getName()));
				httpPost.setEntity(entity);
				
				HttpResponse response = httpClient.execute(httpPost);
				
				int statusCode = response.getStatusLine().getStatusCode();
				HttpEntity responseEntity = response.getEntity();
				String responseString = EntityUtils.toString(responseEntity, "UTF-8");
				
				System.out.println("[" + statusCode + "]" + responseString);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
