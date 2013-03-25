package net.kenpowers.gea;

import java.util.HashMap;

public class GeaPOSTRequest implements GeaServerRequest {
	private String baseURL;
	private HashMap<String, String> parameters;
	private String queryString;
	
	public GeaPOSTRequest(String baseURL, HashMap<String, String> parameters) {
		this.baseURL = baseURL;
		this.parameters = parameters;
		
			queryString += "?";
			for (HashMap.Entry<String, String> entry : this.parameters.entrySet())
				queryString += entry.getKey() + "=" + entry.getValue() + "&";
		
	}
	
	public String getParameterForKey(String key) {
		//stub
		return null;
	}
	
	public String getURL() {
		return baseURL + queryString;
	}
	
	public String getRequestMethod() {
		return "POST";
	}
}
