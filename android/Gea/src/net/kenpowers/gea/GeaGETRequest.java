package net.kenpowers.gea;
import java.util.HashMap;

public class GeaGETRequest implements GeaServerRequest {
	private String baseURL;
	private String[] parameters;
	private String queryString;
	
	public GeaGETRequest(String baseURL, String[] parameters) {
		this.baseURL = baseURL;
		this.parameters = parameters;
		
		queryString = "";
		for (String param : parameters)
			queryString += "/" + param;
	}
	
	public String getParameterAtIndex(int index) {
		if (index > parameters.length-1)
			return null;
		else
			return parameters[index];
	}
	
	public String getURL() {
		return baseURL + queryString;
	}
	
	public RequestMethod getRequestMethod() {
		return RequestMethod.GET;
	}
	
	
}
