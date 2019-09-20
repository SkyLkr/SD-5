import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class FaceDetectionService {
	@GET
	@Produces("application/json")
	public String testConnection() {
		return "Blip blop!";
	}
}
