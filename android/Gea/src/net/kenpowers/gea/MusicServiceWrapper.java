package net.kenpowers.gea;
import android.media.MediaPlayer;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.rdio.android.api.Rdio;
import com.rdio.android.api.RdioApiCallback;
import com.rdio.android.api.RdioListener;
import com.rdio.android.api.services.RdioAuthorisationException;
import com.rdio.android.api.RdioSubscriptionType;

import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;



public class MusicServiceWrapper implements RdioListener, RdioApiCallback {
	private final String rdioKey = "9d3ayynanambvwxq5wpnw82y";
	private final String rdioSecret = "CdNPjjcrPW";
	
	private Rdio rdio;
	
	public MusicServiceWrapper (Context context) {
		rdio = new Rdio(rdioKey, rdioSecret, null, null, context, this);
	}
	
	public MediaPlayer getPlayerForTrack(String trackKey, String sourceKey) {
		return null;
	}
	
	public void search(String query, String types) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("types", types));
		rdio.apiCall("search", params, this);
	}
	
	/*
	 * RdioListener methods
	 */
	
	public void onRdioAuthorised(String accessToken, String accessTokenSecret) {
		
	}
	
	public void onRdioReady() {
		
	}
	
	public void onRdioUserAppApprovalNeeded(Intent authorisationIntent) {
		
	}
	
	public void onRdioUserPlayingElsewhere() {
		
	}
	
	/*
	 * RdioApiCallback methods
	 */
	
	public void onApiSuccess(JSONObject result) {
		try {
			Log.d(MainActivity.LOG_TAG, result.toString(2));
		} catch (Exception e) {
			
		}
	}
	
	public void onApiFailure(String methodName, Exception e) {
		
	}
}
