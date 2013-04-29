
package net.kenpowers.gea.test;

import static org.junit.Assert.*;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

import net.kenpowers.gea.GeaServerHandler;
import net.kenpowers.gea.GeaServerHandler.RequestMethod;

public class RequestTaskTest{
	
	private final String lethargica = "t308758"; //lethargica
	private final RequestMethod GET = GeaServerHandler.RequestMethod.GET;
	private final RequestMethod POST = GeaServerHandler.RequestMethod.POST;
	private final String baseURL = GeaServerHandler.NET_BASE_URL + GeaServerHandler.BASE_RATE_QUERY;
	private JSONArray response;
	private JSONObject jobject;
	private final BasicNameValuePair from = new BasicNameValuePair("from", "rdio");
	private final BasicNameValuePair id = new BasicNameValuePair("id", lethargica);
	private final BasicNameValuePair like = new BasicNameValuePair("verdict", GeaServerHandler.RATE_UP_ARGUMENT);
	private final BasicNameValuePair dislike = new BasicNameValuePair("verdict", GeaServerHandler.RATE_DOWN_ARGUMENT);
	
	@Before
	public void initialize(){
		response = null;
		jobject = null;
	}
	
	@Test
	public void testURL(){
		String expected = "http://gea.kenpowers.net/rate?from=rdio&id=t308758&verdict=like";
		String result = GeaServerHandler.getURLStringForParams(baseURL, new BasicNameValuePair[] { from, id, like });
		assertEquals(expected, result);
	}
	
	@Test
	public void testGET() throws JSONException{
		BasicNameValuePair limit = new BasicNameValuePair("limit", "10");
		String getURL = GeaServerHandler.getURLStringForParams(baseURL, new BasicNameValuePair[] { limit });
		response = GeaServerHandler.getJSONForRequest(getURL, GET);
		assertEquals(10, response.length());
		jobject = response.getJSONObject(0);
		assertNotNull(jobject.getString("artist"));
		assertFalse(jobject.getString("artist").equals(""));
		assertNotNull(jobject.getString("album"));
		assertFalse(jobject.getString("album").equals(""));
		assertNotNull(jobject.getString("title"));
		assertFalse(jobject.getString("title").equals(""));
		assertNotNull(jobject.getString("id"));
	}
	
	@Test
	public void testPOST() throws JSONException{
		//String postURL = GeaServerHandler.NET_BASE_URL + GeaServerHandler.BASE_RATE_QUERY + "from=rdio&" + "id=" + lethargica + "&verdict=" + GeaServerHandler.RATE_UP_ARGUMENT;
		String postURL = GeaServerHandler.getURLStringForParams(baseURL, new BasicNameValuePair[] { from, id, like });
		response = GeaServerHandler.getJSONForRequest(postURL, POST);
		assertEquals(1, response.length());
		jobject = response.getJSONObject(0);
		int rating = jobject.getInt("id");
		postURL = GeaServerHandler.getURLStringForParams(postURL, new BasicNameValuePair[] { from, id, dislike });
		response = GeaServerHandler.getJSONForRequest(postURL, POST);
		jobject = response.getJSONObject(0);
		int updatedRating = jobject.getInt("id");
		assertEquals(rating + 1, updatedRating);
		postURL = GeaServerHandler.getURLStringForParams(postURL, new BasicNameValuePair[] { from, id, like });
		response = GeaServerHandler.getJSONForRequest(postURL, POST);
		jobject = response.getJSONObject(0);
		updatedRating = jobject.getInt("id");
		assertEquals(rating, updatedRating);
	}
}
