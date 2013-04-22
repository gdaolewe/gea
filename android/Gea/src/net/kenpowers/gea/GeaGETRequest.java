package net.kenpowers.gea;
import java.util.HashMap;
import java.util.Iterator;

public class GeaGETRequest implements GeaServerRequest {
	private String baseURL;
	//private String[] parameters;
	private HashMap<String, String> parameters;
	private String queryString;
	
	/**
	 * Constructor for building Gea GET request. Used for pulling ratings from GEA server.
	 * @param baseURL location of GEA server.
	 * @param parameters parameters for GET request.
	 */
	public GeaGETRequest(String baseURL, HashMap<String, String> parameters) {
		this.baseURL = baseURL;
		this.parameters = parameters;
		
		queryString = "";
//		for (String param : parameters)
//			queryString += "/" + param;
		Iterator<HashMap.Entry<String, String>> entries = parameters.entrySet().iterator();
		while(entries.hasNext()){
			HashMap.Entry<String, String> entry = (HashMap.Entry<String, String>) entries.next();
			queryString += entry.getKey() + "=" + entry.getValue();
			if(entries.hasNext())
				queryString += "&";
		}
	}
	
//	public String getParameterAtIndex(int index) {
//		if (index > parameters.length-1)
//			return null;
//		else
//			return parameters[index];
//	}
	
	/**
	 * @return the full URL built for the GET request
	 */
	public String getURL() {
		return baseURL + queryString;
	}
	
	/**
	 * @return the RequestMethod encapsulated in this request
	 */
	public RequestMethod getRequestMethod() {
		return RequestMethod.GET;
	}
	
	
}
