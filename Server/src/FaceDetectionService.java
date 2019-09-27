import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("facedetect")
public class FaceDetectionService {
	
	private static final String UPLOAD_FOLDER = System.getProperty("user.home") + File.separator + "REST_IMG";
	
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
	public Response sendImage(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {
		
		if (uploadedInputStream == null || fileDetail == null) {
			return Response.status(400).entity("Invalid form data").build();
		}
		
		String location = saveImgToDisk(uploadedInputStream, fileDetail);
		
		try {
			File imgFile = new File(location);
			BufferedImage imagem = ImageIO.read(imgFile);
			
			//imgFile.delete();
	        
	        double c = 10;
	        
	        int height = imagem.getHeight();
	        int width = imagem.getWidth();
	        
	        for(int i = 0; i < height; i++) {
	        	for (int j = 0;j < width; j++) {
	        		Color color = new Color(imagem.getRGB(j, i));
	                int red = (int) (color.getRed()*0.299);
	        		int blue = (int) (color.getBlue()*0.587);
	        		int green = (int) (color.getGreen()*0.114);
	                
	                int newRGB = (int) (c*Math.log(red+1)) + (int)(c*Math.log(blue+1))+ (int)(c*Math.log(green+1));
	                Color gray = new Color (newRGB,newRGB,newRGB);
	                
	                imagem.setRGB(j,i,gray.getRGB());
	            }
	        }
	        
	        ImageIO.write(imagem, "jpg", imgFile);
	        
	        return Response.status(200).entity(imgFile.getName()).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(500).entity("Could not save file").build();
		}
	}
	
	@GET
	@Path("/getImg/{file}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getProcessedImg( @PathParam("file") String fileName ) {
		File file = new File(UPLOAD_FOLDER + File.separator + fileName);
		
		ResponseBuilder response = Response.ok((Object) file);
		
		return response.build();
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
		String uploadedFileLocation = UPLOAD_FOLDER + File.separator + fileDetail.getFileName();
		
		File saveDir = new File(UPLOAD_FOLDER);
		if (!saveDir.exists()) {
			saveDir.mkdirs();
		}
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			
			return uploadedFileLocation;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
