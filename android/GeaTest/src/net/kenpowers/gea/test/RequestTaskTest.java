package net.kenpowers.gea.test;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

import net.kenpowers.gea.GeaGETRequest;
import net.kenpowers.gea.GeaPOSTRequest;
import net.kenpowers.gea.GeaServerHandler;
import net.kenpowers.gea.GeaServerRequest;
import net.kenpowers.gea.RequestTask;
import android.test.AndroidTestCase;

public class RequestTaskTest extends AndroidTestCase{
	
	private final String trackID = "t308758"; //lethargica
	private final String server = "http://gea.kenpowers.net";
	private GeaServerRequest get;
	private GeaServerRequest post;
	
	@BeforeClass
	public void setup(){
		get = new GeaGETRequest(server + GeaServerHandler.BASE_RATE_QUERY, null);
		post = new GeaPOSTRequest(server + GeaServerHandler.BASE_RATE_QUERY, null);
	}
	
	@Test
	public void testPOST(){
		RequestTask postRequest = new RequestTask(null);
		
	}
	
	@Test
	public void testGET(){
		RequestTask getRequest = new RequestTask(null);
	}
	
}
