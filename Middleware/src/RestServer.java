import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.JSONObject;

public class RestServer implements Comparable<RestServer> {
	private final String address;
	private long latency;
	private double cpuUsage;
	
	public RestServer(String ip) throws IOException {
		this.address = "http://" + ip + ":8080/Server/facedetect/";
		
		URL url = new URL(this.address);
		
		long t0 = System.currentTimeMillis();
		URLConnection connection = url.openConnection();
		long tf = System.currentTimeMillis();
		
		this.latency = tf - t0;
		
		Scanner in = new Scanner(connection.getInputStream());
		
		String jsonStr = in.nextLine();
		
		System.out.println("JSON: " + jsonStr);
		
		JSONObject json = new JSONObject(jsonStr);
		
		this.cpuUsage = json.getDouble("cpu_load");
		
		in.close();
	}

	public String getAddress() {
		return address;
	}

	public long getLatency() {
		return latency;
	}

	public double getCpuUsage() {
		return cpuUsage;
	}
	
	public String toString() {
		return String.format("{ address: %s, latency: %d, cpu_usage: %f }", this.address, this.latency, this.cpuUsage);
	}
	
	public double getPriority() {
		return this.latency + this.cpuUsage;
	}

	@Override
	public int compareTo(RestServer o) {
		double p1 = getPriority();
		double p2 = o.getPriority();
		if (p1 > p2) {
			return 1;
		} else if (p2 > p1) {
			return 2;
		} else {
			return 0;
		}
	}
}
