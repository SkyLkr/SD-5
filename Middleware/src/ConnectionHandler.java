import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
	
	private final int PACK_SIZE = 8*1024;
	
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
			InputStream in = new BufferedInputStream(client.getInputStream());
			OutputStream out = new BufferedOutputStream(client.getOutputStream());
			
			File saveDir = new File(System.getProperty("user.dir") + File.separator + "images");
			
			if (!saveDir.exists()) saveDir.mkdirs();
			
			File f = File.createTempFile("INPUT", ".jpg", saveDir);
			
			FileOutputStream fileOut = new FileOutputStream(f);
			
			byte[] bytes = new byte[PACK_SIZE];
			
			
			int read;
			
//			int numLoops = in.read();
//			System.out.println("Number of packages "+ numLoops);
//			
//			System.out.print("Recebendo arquivo.");
//			for (int i = 0; i < numLoops; i++) {
//				read = in.read(bytes);
//				fileOut.write(bytes, 0, read);
//				System.out.print(".");
//			}
//			fileOut.flush();
			
			while ((read = in.read(bytes)) > 0) {
				fileOut.write(bytes, 0, read);
				System.out.print(".");
			}
			System.out.print("\n");
			
			fileOut.close();
			
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
				
				if (statusCode == 200) {
					
					System.out.println("[" + statusCode + "]" + responseString);
					
					URL urlObj = new URL(rest.getAddress() + "getImg/" + responseString);
					
					InputStream is = new BufferedInputStream(urlObj.openStream());
					
					File resDir = new File(System.getProperty("user.dir") + File.separator + "result_imgs");
					resDir.mkdirs();
					
					File tf = File.createTempFile("TEMP", ".jpg", resDir);
					
					BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(tf));
					
					read = 0;
					while ((read = is.read(bytes)) > 0) {
						bout.write(bytes, 0, read);
					}
					bout.close();
					is.close();
										
					BufferedInputStream bin = new BufferedInputStream(new FileInputStream(tf));
//					
//					numLoops = 1 + (int) (tf.length() / PACK_SIZE);
//					System.out.println(tf.length() + "/" + PACK_SIZE + " = " + (1+ (int)tf.length()/PACK_SIZE));
//					
//					out.write(numLoops);
//					
//					for (int i = 0; i < numLoops; i++) {
//						read = bin.read(bytes);
//						out.write(bytes, 0, read);
//					}
//					out.flush();
					
					while ((read = bin.read(bytes)) > 0) {
						out.write(bytes, 0, read);
					}
					
//					bin.read(bytes);
//					out.write(bytes, 0, (int)tf.length());
					
					bin.close();
					out.close();
					in.close();
					
					System.out.println("Finished.");
				} else {
					System.out.println("[" + statusCode + "]" + responseString);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
