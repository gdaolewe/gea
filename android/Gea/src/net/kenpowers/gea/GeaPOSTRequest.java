package net.kenpowers.gea;

import java.util.HashMap;
import java.util.Iterator;

public class GeaPOSTRequest implements GeaServerRequest {
	private String baseURL;
	private HashMap<String, String> parameters;
	private String queryString;
	
	/**
	 * Constructor for building POST request. Used for sending approval/disapproval to GEA server.
	 * @param baseURL URL for GEA server
	 * @param parameters POST request parameters. Order is: from, id, verdict.
	 */
	public GeaPOSTRequest(String baseURL, HashMap<String, String> parameters) {
		this.baseURL = baseURL;
		this.parameters = parameters;
		
			queryString = "";
			Iterator<HashMap.Entry<String, String>> entries = parameters.entrySet().iterator();
			while (entries.hasNext()) {
				HashMap.Entry<String, String> entry = (HashMap.Entry<String, String>) entries.next();
				queryString += entry.getKey() + "=" + entry.getValue();	
				if (entries.hasNext())
					queryString += "&";
			}
	}
	
	/**
	 * Retrieve the value mapped to the given key
	 * @param key key to lookup value
	 * @return the value mapped to the key
	 */
	public String getParameterForKey(String key) {
		return parameters.get(key);
	}
	
	/**
	 * @return the full URL for the POST request
	 */
	public String getURL() {
		return baseURL + queryString;
	}
	
	/**
	 * @return the RequestMethod encapsulated in the request.
	 */
	public RequestMethod getRequestMethod() {
		return RequestMethod.POST;
	}
}
