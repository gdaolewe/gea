package net.kenpowers.gea;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.rdio.android.api.Rdio;
import com.rdio.android.api.RdioApiCallback;
import com.rdio.android.api.RdioListener;

public class MusicServiceWrapper {
	
	private static final MusicServiceWrapper INSTANCE = new MusicServiceWrapper();
	
	private final String LOG_TAG = "Gea Music Service";
	
	private final String rdioKey = "9d3ayynanambvwxq5wpnw82y";
	private final String rdioSecret = "CdNPjjcrPW";
	private Rdio rdio;
	private boolean rdioReady;
	
	private MediaPlayer player;
	private int volume;
	private Track currentTrack;
	private Track[] currentPlaylist; 
	private int currentPlaylistIndex;
	
	
	
	private MusicServiceWrapper() {
		rdioReady = false;
		if (rdio == null) {
			rdio = new Rdio(rdioKey, rdioSecret, null, null, MainActivity.getAppContext(), 
				new RdioListener() {
					public void onRdioAuthorised(String accessToken, String accessTokenSecret) {}
					public void onRdioReady() {
						rdioReady = true;
						notifyMusicServiceReadyListeners();
					}
					public void onRdioUserAppApprovalNeeded(Intent authorisationIntent) {}
					public void onRdioUserPlayingElsewhere() {}
			});
		}
		trackChangedListeners = new ArrayList<TrackChangedListener>();
		musicServiceReadyListeners = new ArrayList<MusicServiceReadyListener>();
	}
	
	public static MusicServiceWrapper getInstance() {
		return INSTANCE;
	}
	public boolean isReady() {
		return rdioReady;
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
	
	public static interface SearchCompleteListener {
		public void onSearchComplete(MusicServiceObject[] results);
	}
	
	public static interface TrackChangedListener {
		public void onTrackChanged(Track track);
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
	
	public Track getCurrentTrack() {
		return currentTrack;
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
	
	public void setPlaylist(Track[] tracks, int index) {
		currentPlaylist = tracks;
		currentPlaylistIndex = index;
	}
	
	public void playPreviousTrack() {
		if (currentPlaylist == null)
			return;
		if (currentPlaylistIndex <= 0)
			return;
		currentPlaylistIndex--;
		getPlayerForTrack(currentPlaylist[currentPlaylistIndex]);
	}
	public void playNextTrack() {
		if (currentPlaylist == null)
			return;
		if (currentPlaylistIndex >= currentPlaylist.length-1)
			return;
		currentPlaylistIndex++;
		getPlayerForTrack(currentPlaylist[currentPlaylistIndex]);
	}
	
	public void search(String query, String types, final SearchCompleteListener callback) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("types", types));
		rdio.apiCall("search", params, new RdioApiCallback() {
			@Override
			public void onApiFailure(String methodName, Exception e) {
				Log.e(LOG_TAG, "Rdio API call" + methodName + " failed");
			}
			@Override
			public void onApiSuccess(JSONObject result) {
				MusicServiceObject[] results = null;
				try {
					JSONArray json = result.getJSONObject("result").getJSONArray("results");
					results = new MusicServiceObject[json.length()];
					Log.i(LOG_TAG, "Search results:");
					for (int i=0; i<json.length(); i++) {
						JSONObject obj = json.getJSONObject(i);
						results[i] = getMusicServiceObjectForJSON(obj);
					}
					//notifySearchCompleteListeners(results);
					callback.onSearchComplete(results);
				} catch(JSONException e) {
					Log.e(LOG_TAG,"Error parsing JSON: search");
				}
			}
		});
	}
	
	public void getMusicServiceObjectsForKeys(String[] keys, final SearchCompleteListener callback) {
		String keysString = "";
		for (int i=0; i<keys.length; i++) {
			keysString += keys[i];
			if (i < keys.length-1)
				keysString += ",";
		}
		Log.d(LOG_TAG, keysString);
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("keys", keysString));
		rdio.apiCall("get", params, new RdioApiCallback() {
			@Override
			public void onApiFailure(String methodName, Exception e) {
				Log.e(LOG_TAG, "Rdio API call" + methodName + " failed");
			}
			@Override
			public void onApiSuccess(JSONObject result) {
				MusicServiceObject[] results = null;
				try {
					JSONObject json = result.getJSONObject("result");
					results = new MusicServiceObject[json.length()];
					Iterator<?> jsonKeys = json.keys();
					int i = 0;
					while (jsonKeys.hasNext()) {
						String jsonKey = (String)jsonKeys.next();
						JSONObject obj = json.getJSONObject(jsonKey);
						results[i] = getMusicServiceObjectForJSON(obj);
						i++;
					}
					//notifySearchCompleteListeners(results);
					callback.onSearchComplete(results);
				} catch(JSONException e) {
					Log.e(LOG_TAG,"Error parsing JSON: get");
				}
			}
		});
	}
	
	public void getAlbumsForArtist(Artist artist, final SearchCompleteListener callback) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("artist", artist.getKey()));
		Log.d(LOG_TAG, artist.getKey());
		rdio.apiCall("getAlbumsForArtist", params, new RdioApiCallback() {
			@Override
			public void onApiFailure(String methodName, Exception e) {
				Log.e(LOG_TAG, "Rdio API call" + methodName + " failed");
			}
			@Override
			public void onApiSuccess(JSONObject result) {
				MusicServiceObject[] results = null;
				try {
				JSONArray json = result.getJSONArray("result");
				results = new MusicServiceObject[json.length()];
				Log.i(LOG_TAG, "Search results:");
				for (int i=0; i<json.length(); i++) {
					JSONObject obj = json.getJSONObject(i);
					results[i] = getMusicServiceObjectForJSON(obj);
				}
				//notifySearchCompleteListeners(results);
				callback.onSearchComplete(results);
				} catch (JSONException e) {
					Log.e(LOG_TAG,"Error parsing JSON: getAlbumsForArtist");
				}
			}
			
		});
	}
	
		private MusicServiceObject getMusicServiceObjectForJSON(JSONObject obj) {
			MusicServiceObject msObj = null;
			try {
				String key  = obj.getString("key");
				String type = obj.getString("type");
			
				if (type.equals("t")) {
					msObj = new Track(key, "track", obj.getString("name"), 
													obj.getString("artist"),
													obj.getString("artistKey"), 
													obj.getString("album"), 
													obj.getString("albumKey"),  
													obj.getString("icon"), 
													obj.getInt("duration"), 	  
													obj.getInt("trackNum"));
					
				} else if (type.equals("r")) {
					msObj = new Artist(key, "artist", obj.getString("name"), obj.getString("icon"));
				} else if (type.equals("a")) {
					JSONArray trackKeysJSON = obj.getJSONArray("trackKeys");
					String[] trackKeys = new String[trackKeysJSON.length()];
					for (int trackIndex=0; trackIndex<trackKeysJSON.length(); trackIndex++)
						trackKeys[trackIndex] = trackKeysJSON.getString(trackIndex);
					msObj = new Album(key, "album", obj.getString("name"), 
									  				obj.getString("artist"), 
									  				obj.getString("artistKey"),
									  				obj.getString("icon"),   
									  				trackKeys);
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
							if (currentPlaylist == null)
								notifyTrackChangedListeners(null);
							else if (currentPlaylistIndex >= currentPlaylist.length-1)
								notifyTrackChangedListeners(null);
							else
								playNextTrack();
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