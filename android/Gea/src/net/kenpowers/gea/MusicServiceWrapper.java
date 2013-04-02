package net.kenpowers.gea;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.rdio.android.api.Rdio;
import com.rdio.android.api.RdioApiCallback;
import com.rdio.android.api.RdioListener;
import com.rdio.android.api.services.RdioAuthorisationException;
import com.rdio.android.api.RdioSubscriptionType;

import java.util.List;
import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;



public class MusicServiceWrapper implements RdioApiCallback, SearchCompletePublisher, 
											TrackChangedPublisher {
	private static final MusicServiceWrapper INSTANCE = new MusicServiceWrapper();
	
	
	
	private final String rdioKey = "9d3ayynanambvwxq5wpnw82y";
	private final String rdioSecret = "CdNPjjcrPW";
	private MediaPlayer player;
	
	
	private Rdio rdio;
	private Track currentTrack;
	
	private MusicServiceWrapper () {
		searchCompleteListeners = new ArrayList<SearchCompleteListener>();
		trackChangedListeners = new ArrayList<TrackChangedListener>();
		musicServiceReadyListeners = new ArrayList<MusicServiceReadyListener>();
	}
	
	public static MusicServiceWrapper getInstance (Context context) {
		if (INSTANCE.rdio == null)
			INSTANCE.rdio = new Rdio(INSTANCE.rdioKey, INSTANCE.rdioSecret, null, null, context, 
				new RdioListener() {
					public void onRdioAuthorised(String accessToken, String accessTokenSecret) {}
					public void onRdioReady() {
						INSTANCE.notifyMusicServiceReadyListeners();
					}
					public void onRdioUserAppApprovalNeeded(Intent authorisationIntent) {}
					public void onRdioUserPlayingElsewhere() {}
			});
		return INSTANCE;
	}
	
	private List<SearchCompleteListener> searchCompleteListeners;
	
	public void registerSearchCompleteListener(SearchCompleteListener listener) {
		searchCompleteListeners.add(listener);
	}
	public void notifySearchCompleteListeners(MusicServiceObject[] results) {
		if (searchCompleteListeners.size() > 0)
			for (SearchCompleteListener l : searchCompleteListeners)
				l.onSearchComplete(results);
	}
	
	private List<TrackChangedListener> trackChangedListeners;
	
	public void registerTrackChangedListener(TrackChangedListener listener) {
		trackChangedListeners.add(listener);
	}
	public void notifyTrackChangedListeners(Track track) {
		if (trackChangedListeners.size() > 0)
			for (TrackChangedListener listener: trackChangedListeners)
				listener.onTrackChanged(track);
	}
	
	private List<MusicServiceReadyListener> musicServiceReadyListeners;
	
	public void registerMusicServiceReadyListener(MusicServiceReadyListener listener) {
		musicServiceReadyListeners.add(listener);
	}
	public void notifyMusicServiceReadyListeners() {
		if (musicServiceReadyListeners.size() > 0)
			for (MusicServiceReadyListener listener: musicServiceReadyListeners)
				listener.onMusicServiceReady();
	}
	
	public void getPlayerForTrack(Track track) {
		currentTrack = track;
		new RdioMediaPlayerTask(this).execute(currentTrack.getKey(), null);
	}
	
	public int getPlayerPosition() {
		if (player == null)
			return 0;
		else {
			return player.getCurrentPosition();
		}
	}
	
	public int getPlayerDuration() {
		if (player == null)
			return 0;
		else if (rdio.canUserPlayFullStreams()==false) {
		/*	TODO MediaPlayer.getDuration() returns 0 when playing streaming media 
		 *	Replace hardcoded duration when playing 30-second clips
		 */
			return 30000;
		} else {
			return player.getDuration();
		}
	}
	
	public void togglePlayerPaused() {
		if (player != null) {
			if (player.isPlaying())
				player.pause();
			else
				player.start();
		}
	}
	
	public boolean playerIsPlaying() {
		if (player==null)
			return false;
		else
			return player.isPlaying();
	}
	
	public void seekPlayerTo(int seconds) {
		player.seekTo(seconds*1000);
	}
	
	private final static int MAX_VOLUME = 100;
	
	public void setPlayerVolume(int volumePercent) {
		float volume = (float) (1 - (Math.log(MAX_VOLUME - volumePercent) / Math.log(MAX_VOLUME)));
		player.setVolume(volume, volume);
	}
	
	public void search(String query, String types) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("types", types));
		rdio.apiCall("search", params, this);
	}
	
	/*
	 * RdioApiCallback methods
	 */
	
	public void onApiSuccess(JSONObject result) {
		try {
			JSONArray resultsJSON = result.getJSONObject("result").getJSONArray("results");
			
			MusicServiceObject[] results = new MusicServiceObject[resultsJSON.length()];
			for (int i=0; i<resultsJSON.length(); i++) {
				JSONObject obj = resultsJSON.getJSONObject(i);
				
				String key = obj.getString("key");
				String type = obj.getString("type");
				
				if (type.equals("t")) {
					results[i] = new Track(key, "track", obj.getString("name"), obj.getString("artist"),
							obj.getString("album"), obj.getString("icon"), obj.getInt("duration"));
					Log.d(MainActivity.LOG_TAG, results[i].toString());
					
				} else if (key.equals("r")) {
					
				} else if (key.equals("a")) {
					
				} else {
					Log.e(MainActivity.LOG_TAG, "Invalid result type");
				}
			}
			
			notifySearchCompleteListeners(results);
			
		} catch (JSONException e) {
			Log.d(MainActivity.LOG_TAG,"JSON exception");		
		}	
	}
	
	public void onApiFailure(String methodName, Exception e) {
		Log.e(MainActivity.LOG_TAG, "Rdio API call failed");
	}
	
	private void mediaPlayerReady(MediaPlayer player) {
		try {
			player.prepare();
			if (this.player != null) {
				this.player.reset();
				this.player.release();
				this.player = null;
			}
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer player) {
					currentTrack = null;
					notifyTrackChangedListeners(currentTrack);
				} });
			this.player = player;
			notifyTrackChangedListeners(currentTrack);
		} catch(Exception e) {
			Log.e(MainActivity.LOG_TAG, e.toString());
		}
	}
	
	public void cleanup() {
		rdio.cleanup();
		if (player != null) {
			player.stop();
			player.reset();
			player.release();
			player = null;
		}
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
