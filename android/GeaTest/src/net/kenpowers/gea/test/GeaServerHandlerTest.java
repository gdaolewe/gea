package net.kenpowers.gea.test;

import java.net.HttpURLConnection;

import junit.framework.TestCase;

import org.apache.http.message.BasicNameValuePair;

import net.kenpowers.gea.GeaServerHandler;
import net.kenpowers.gea.GeaServerHandler.RequestMethod;

public class GeaServerHandlerTest extends TestCase {
	
	public void test_connect() {
		HttpURLConnection connection = GeaServerHandler.connect("http://google.com", RequestMethod.GET);
		try {
			assertEquals(200, connection.getResponseCode());
		} catch(Exception e) {}
	}
	
	public void test_getURLStringForParams() {
		String result;
		String baseURL = "http://google.com/search?";
		BasicNameValuePair[] params = new BasicNameValuePair[10];
		params[0] = new BasicNameValuePair("","");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL));
		params[0] = new BasicNameValuePair("","a");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL));
		params[0] = new BasicNameValuePair("a","");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL));
		params[0] = new BasicNameValuePair("a","b");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL + "a=b"));
		params[1] = new BasicNameValuePair("", "");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL + "a=b"));
		params[1] = new BasicNameValuePair("c", "");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL + "a=b"));
		params[1] = new BasicNameValuePair("", "d");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL + "a=b"));
		params[1] = new BasicNameValuePair("c", "d");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL + "a=b&c=d"));
		params[2] = new BasicNameValuePair("e", "f");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL + "a=b&c=d&e=f"));
		params[0] = new BasicNameValuePair("", "");
		result = GeaServerHandler.getURLStringForParams(baseURL, params);
		assertTrue(result.equals(baseURL + "c=d&e=f"));
	}
}
