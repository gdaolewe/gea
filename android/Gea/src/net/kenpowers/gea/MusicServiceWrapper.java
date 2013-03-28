package net.kenpowers.gea;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import org.json.*;



public class MusicServiceWrapper implements RdioListener, RdioApiCallback {
	private final String rdioKey = "9d3ayynanambvwxq5wpnw82y";
	private final String rdioSecret = "CdNPjjcrPW";
	private MediaPlayer player;
	
	private Rdio rdio;
	
	public MusicServiceWrapper (Context context) {
		rdio = new Rdio(rdioKey, rdioSecret, null, null, context, this);
	}
	
	public void getPlayerForTrack(String trackKey, String sourceKey) {
		new RdioMediaPlayerTask(this).execute(trackKey, sourceKey);
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
			JSONObject resultsObject = (JSONObject) (result.getJSONObject("result")
					.getJSONArray("results").get(0));
			String trackKey = resultsObject.get("key").toString();
			Log.d(MainActivity.LOG_TAG, "track name: " + resultsObject.get("name").toString() + 
					"\nartist: " + resultsObject.get("artist") + "\nalbum: " + resultsObject.get("album")
					+ "\ntrackKey: " + trackKey);
			new RdioMediaPlayerTask(this).execute(trackKey, null);
			
		} catch (Exception e) {
			Log.d(MainActivity.LOG_TAG,"Pretty printing JSON failed, printing without pretty print");
			Log.d(MainActivity.LOG_TAG, result.toString());
			
			
		}
		
	}
	
	public void mediaPlayerReady(MediaPlayer player) {
		
		this.player = player;
		try {
			player.prepare();
			if (this.player != null) {
				this.player.stop();
			}
			player.start();
		} catch(Exception e) {
			Log.e(MainActivity.LOG_TAG, e.toString());
		}
	}
	
	public void onApiFailure(String methodName, Exception e) {
		Log.e(MainActivity.LOG_TAG, "Rdio API call failed");
	}
	
	private class RdioMediaPlayerTask extends AsyncTask<String, String, MediaPlayer> {
		private MusicServiceWrapper callback;
		public RdioMediaPlayerTask (MusicServiceWrapper callback) {
			this.callback = callback;
		}
		
		protected MediaPlayer doInBackground(String... params) {
			return rdio.getPlayerForTrack(params[0], params[1], true);
			
		}
		
		@Override
	    protected void onPostExecute(MediaPlayer result) {
	        super.onPostExecute(result);
	        callback.mediaPlayerReady(result);
	    }
		
		
	}
}
