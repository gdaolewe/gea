package net.kenpowers.gea;

import java.util.HashMap;
import java.util.Iterator;

public class GeaPOSTRequest implements GeaServerRequest {
	private String baseURL;
	private HashMap<String, String> parameters;
	private String queryString;
	
	public GeaPOSTRequest(String baseURL, HashMap<String, String> parameters) {
		this.baseURL = baseURL;
		this.parameters = parameters;
		
			queryString = "/rate?";
			Iterator<HashMap.Entry<String, String>> entries = parameters.entrySet().iterator();
			while (entries.hasNext()) {
				HashMap.Entry<String, String> entry = (HashMap.Entry<String, String>) entries.next();
				queryString += entry.getKey() + "=" + entry.getValue();	
				if (entries.hasNext())
					queryString += "&";
			}
		
	}
	
	public String getParameterForKey(String key) {
		return parameters.get(key);
	}
	
	public String getURL() {
		return baseURL + queryString;
	}
	
	public String getRequestMethod() {
		return "POST";
	}
}
