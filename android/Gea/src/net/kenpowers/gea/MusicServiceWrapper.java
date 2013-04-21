package net.kenpowers.gea;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.rdio.android.api.Rdio;
import com.rdio.android.api.RdioApiCallback;
import com.rdio.android.api.RdioListener;
import com.rdio.android.api.services.RdioAuthorisationException;
import com.rdio.android.api.RdioSubscriptionType;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;



public class MusicServiceWrapper implements RdioApiCallback, SearchCompletePublisher, 
											TrackChangedPublisher {
	
	private static final MusicServiceWrapper INSTANCE = new MusicServiceWrapper();
	
	private final String LOG_TAG = "Gea Music Service";
	
	private final String rdioKey = "9d3ayynanambvwxq5wpnw82y";
	private final String rdioSecret = "CdNPjjcrPW";
	private Rdio rdio;
	
	private MediaPlayer player;
	private int volume;
	private Track currentTrack;

	private MusicServiceWrapper() {
		if (rdio == null) {
			rdio = new Rdio(rdioKey, rdioSecret, null, null, MainActivity.getAppContext(), 
				new RdioListener() {
					public void onRdioAuthorised(String accessToken, String accessTokenSecret) {}
					public void onRdioReady() {
						notifyMusicServiceReadyListeners();
					}
					public void onRdioUserAppApprovalNeeded(Intent authorisationIntent) {}
					public void onRdioUserPlayingElsewhere() {}
			});
		}
		searchCompleteListeners = new ArrayList<SearchCompleteListener>();
		trackChangedListeners = new ArrayList<TrackChangedListener>();
		musicServiceReadyListeners = new ArrayList<MusicServiceReadyListener>();
	}
	
	public static MusicServiceWrapper getInstance() {
		return INSTANCE;
	}
	
	public void cleanup() {
		Log.i(LOG_TAG, "Cleaning up..");
		rdio.cleanup();
		if (player != null) {
			player.stop();
			player.reset();
			player.release();
			player = null;
		}
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
		if (player == null || currentTrack == null)
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
			if (player.isPlaying()) {
				player.pause();				
			}
			else {
				player.start();
			}
		}
	}
	
	public boolean playerIsPlaying() {
		if (player==null)
			return false;
		else
			return player.isPlaying();
	}
	
	public void seekPlayerTo(int seconds) {
		if (player != null)
			player.seekTo(seconds*1000);
	}
	
	private final static int MAX_VOLUME = 100;
	
	public void setPlayerVolume(int volumePercent) {
		volume = volumePercent;
		if (player != null) {
			float volume = (float) (1 - (Math.log(MAX_VOLUME - volumePercent) / Math.log(MAX_VOLUME)));
			player.setVolume(volume, volume);
		}
	}
	
	public void search(String query, String types) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("types", types));
		rdio.apiCall("search", params, this);
	}
	
	public void getMusicServiceObjectsForKeys(String[] keys) {
		String keysString = "";
		for (int i=0; i<keys.length; i++) {
			keysString += keys[i];
			if (i < keys.length-1)
				keysString += ",";
		}
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("keys", keysString));
		rdio.apiCall("get", params, this);
	}
	
	/*
	 * RdioApiCallback methods
	 */
	public void onApiSuccess(JSONObject result) {
		try {
			result = result.getJSONObject("result");
			if (result.has("results")) {
				JSONArray resultsJSON = result.getJSONArray("results");
				MusicServiceObject[] results = new MusicServiceObject[resultsJSON.length()];
				Log.i(LOG_TAG, "Search results:");
				for (int i=0; i<resultsJSON.length(); i++) {
					JSONObject obj = resultsJSON.getJSONObject(i);
					
					results[i] = getMusicServiceObjectForJSON(obj);
					
					/*String key = obj.getString("key");
					String type = obj.getString("type");
					
					if (type.equals("t")) {
						results[i] = new Track(key, "track", obj.getString("name"), obj.getString("artist"),
								obj.getString("album"), obj.getString("icon"), obj.getInt("duration"));
						Log.i(LOG_TAG, results[i].toString());
						
					} else if (type.equals("r")) {
						results[i] = new Artist(key, "artist", obj.getString("name"), obj.getString("icon"));
					} else if (type.equals("a")) {
						JSONArray trackKeysJSON = obj.getJSONArray("trackKeys");
						String[] trackKeys = new String[trackKeysJSON.length()];
						for (int trackIndex=0; trackIndex<trackKeysJSON.length(); trackIndex++)
							trackKeys[trackIndex] = trackKeysJSON.getString(trackIndex);
						results[i] = new Album(key, "album", obj.getString("name"), obj.getString("artist"), obj.getString("artistKey"),
											   obj.getString("icon"), trackKeys);
					} else {
						Log.e(LOG_TAG, "Invalid result type " + key);
					}*/
				}
				notifySearchCompleteListeners(results);
			} else {
				MusicServiceObject[] results = new MusicServiceObject[result.length()];
				Iterator<?> jsonKeys = result.keys();
				int i = 0;
				while (jsonKeys.hasNext()) {
					String jsonKey = (String)jsonKeys.next();
					JSONObject obj = result.getJSONObject(jsonKey);
					results[i] = getMusicServiceObjectForJSON(obj);
					i++;
				}
				notifySearchCompleteListeners(results);
			}
			
		} catch (JSONException e) {
			Log.d(LOG_TAG,"JSON exception");		
		}	
	}
	
	private MusicServiceObject getMusicServiceObjectForJSON(JSONObject obj) {
		MusicServiceObject msObj = null;
		
		try {
			String key = obj.getString("key");
			String type = obj.getString("type");
		
		if (type.equals("t")) {
			msObj = new Track(key, "track", obj.getString("name"), obj.getString("artist"),
					obj.getString("album"), obj.getString("icon"), obj.getInt("duration"));
			
		} else if (type.equals("r")) {
			msObj = new Artist(key, "artist", obj.getString("name"), obj.getString("icon"));
		} else if (type.equals("a")) {
			JSONArray trackKeysJSON = obj.getJSONArray("trackKeys");
			String[] trackKeys = new String[trackKeysJSON.length()];
			for (int trackIndex=0; trackIndex<trackKeysJSON.length(); trackIndex++)
				trackKeys[trackIndex] = trackKeysJSON.getString(trackIndex);
			msObj = new Album(key, "album", obj.getString("name"), obj.getString("artist"), obj.getString("artistKey"),
								   obj.getString("icon"), trackKeys);
		} else {
			Log.e(LOG_TAG, "Invalid result type " + key);
		}
		} catch (JSONException e) {
			Log.d(LOG_TAG,"JSON exception");
		}
		if (msObj != null)
			Log.i(LOG_TAG, msObj.toString());
		return msObj;
	}
	
	public void onApiFailure(String methodName, Exception e) {
		Log.e(LOG_TAG, "Rdio API call failed");
	}
	
	private void mediaPlayerReady(MediaPlayer player) {
		if (this.player != null) {
			this.player.reset();
			this.player.release();
			this.player = null;
		}
		
		try {
			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
					mp.setWakeMode(MainActivity.getAppContext(), PowerManager.PARTIAL_WAKE_LOCK);
					mp.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer player) {
							currentTrack = null;
							notifyTrackChangedListeners(currentTrack);
						}
					});
					notifyTrackChangedListeners(currentTrack);
				}
			});
			player.prepareAsync();
			this.player = player;
			setPlayerVolume(volume);
		} catch(Exception e) {
			Log.e(LOG_TAG, e.toString());
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
