
package net.kenpowers.gea.test;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

import net.kenpowers.gea.GeaServerHandler;
import net.kenpowers.gea.GeaServerHandler.RequestMethod;
import android.test.AndroidTestCase;

public class RequestTaskTest extends AndroidTestCase{
	
	private final String lethargica = "t308758"; //lethargica
	private final RequestMethod GET = GeaServerHandler.RequestMethod.GET;
	private final RequestMethod POST = GeaServerHandler.RequestMethod.POST;
	private JSONArray response;
	private final BasicNameValuePair from = new BasicNameValuePair("from", "rdio");
	private final BasicNameValuePair id = new BasicNameValuePair("id", lethargica);
	private final BasicNameValuePair verdict = new BasicNameValuePair("verdict", GeaServerHandler.RATE_UP_ARGUMENT);
	
	@Before
	public void initialize(){
		response = null;
	}
	
	@Test
	public void testURL(){
		String expected = "http://gea.kenpowers.net/rate?from=rdio&id=t308758&verdict=like";
		String result = GeaServerHandler.getURLStringForParams(GeaServerHandler.NET_BASE_URL + GeaServerHandler.BASE_RATE_QUERY, new BasicNameValuePair[] { from, id, verdict });
		assertEquals(expected, result);
	}
	
	@Ignore
	@Test
	public void testGET(){
		String baseURL = GeaServerHandler.NET_BASE_URL + GeaServerHandler.BASE_RATE_QUERY;
		BasicNameValuePair limit = new BasicNameValuePair("limit", "10");
		String getURL = GeaServerHandler.getURLStringForParams(baseURL, new BasicNameValuePair[] { limit });
		response = GeaServerHandler.getJSONForRequest(getURL, GET);
		JSONObject jobject = null;
		try{
			jobject = response.getJSONObject(0);
			assertEquals(10, response.length()); //make sure we get back 
			
		}
		catch(JSONException e){
			fail(e.toString());
		}
	}
	
	@Ignore
	@Test
	public void testPOST(){
		String postURL = GeaServerHandler.NET_BASE_URL + GeaServerHandler.BASE_RATE_QUERY + "from=rdio&" + "id=" + lethargica + "&verdict=" + GeaServerHandler.RATE_UP_ARGUMENT;
		response = GeaServerHandler.getJSONForRequest(postURL, POST);
		JSONObject jobject = null;
		try{
			jobject = response.getJSONObject(0);
			
		}
		catch(JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	//public void test
}
