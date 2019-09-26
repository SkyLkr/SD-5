import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;

import javax.imageio.ImageIO;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;

@Path("/")
public class FaceDetectionService {
	@GET
	@Produces("application/json")
	public String testConnection() {
		
		double load = -1;
		try {
			load = getCpuLoad();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject();
		json.put("cpu_load", load);
		
		return json.toString();
	}
	
	@POST
	@Path("/sendImg")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String sendImage(
		@FormParam("file") InputStream uploadedInputStream,
		@FormParam("file") FormDataContentDisposition fileDetail) throws IOException {
		
		String Location = saveImgToDisk(uploadedInputStream, fileDetail);
		BufferedImage imagem = ImageIO.read(new File(Location));
        
        int newimage[][][] = new int[3][imagem.getHeight()][imagem.getWidth()];
        double c = 10;
        
        for(int i = 0; i<imagem.getHeight()-1;i++) {
        	for (int j = 0;j<imagem.getWidth()-1;j++) {
        		Color color = new Color(imagem.getRGB(j, i));
                int red = (int) (color.getRed()*0.299);
        		int blue = (int) (color.getBlue()*0.587);
        		int green = (int) (color.getGreen()*0.114);
                
                int newRGB = (int) (c*Math.log(red+1)) + (int)(c*Math.log(blue+1))+ (int)(c*Math.log(green+1));
                Color gray = new Color (newRGB,newRGB,newRGB);
                imagem.setRGB(j,i,gray.getRGB());

            }
                
        }

		return "Success!";
	}
	
	private double getCpuLoad() throws Exception {

	    MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
	    ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
	    AttributeList list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });

	    if (list.isEmpty())     return Double.NaN;

	    Attribute att = (Attribute)list.get(0);
	    Double value  = (Double)att.getValue();

	    // usually takes a couple of seconds before we get real values
	    if (value == -1.0)      return Double.NaN;
	    // returns a percentage value with 1 decimal point precision
	    return ((int)(value * 1000) / 10.0);
	}
	
	private String saveImgToDisk(InputStream uploadedInputStream, FormDataContentDisposition fileDetail) {
		String uploadedFileLocation = System.getProperty("user.dir") + File.separator + fileDetail.getFileName();
		
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploadedFileLocation;
	}
}
