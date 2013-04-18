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
	private MediaPlayer player;

	private WifiManager.WifiLock wifiLock;
	
	private Rdio rdio;
	private Track currentTrack;
		
	//private final IBinder musicBinder = new MusicBinder();
	
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
	
	/*@Override
	public IBinder onBind(Intent intent) {
		return musicBinder;
	}
	
	public class MusicBinder extends Binder {
		MusicServiceWrapper getService() {
			return MusicServiceWrapper.this;
		}
	}
	
	private Notification notification;
	private NotificationManager notiMgr;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);
		wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
		notiMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		Log.d(LOG_TAG, "musicservicewrapper service created");	
	}
	
	@Override
	public void onDestroy() {
		cleanup();
		stopForeground(true);
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		cleanup();
		return super.onUnbind(intent);
	}*/
	
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
			if (player.isPlaying()) {
				player.pause();
				/*if (wifiLock != null) {
					try {
						wifiLock.release();
					} catch (Throwable th) {
						//wifiLock may already have been released
					}
				}*/
				
				
			}
			else {
				player.start();
				/*if (wifiLock == null) {
					wifiLock = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
				}
				wifiLock.acquire();*/
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
					Log.d(LOG_TAG, results[i].toString());
					
				} else if (key.equals("r")) {
					
				} else if (key.equals("a")) {
					
				} else {
					Log.e(LOG_TAG, "Invalid result type");
				}
			}
			
			notifySearchCompleteListeners(results);
			
		} catch (JSONException e) {
			Log.d(LOG_TAG,"JSON exception");		
		}	
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
		
		/*Resources res = getApplicationContext().getResources();
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
				 new Intent(getApplicationContext(), MainActivity.class),
				 PendingIntent.FLAG_UPDATE_CURRENT);
		
		notification = new Notification.Builder(getApplicationContext())
        .setContentTitle("Gea")
        .setContentText(currentTrack.toString())
        .setSmallIcon(R.drawable.ic_launcher)
        .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
        .setWhen(System.currentTimeMillis())
        .setAutoCancel(true)
        .setTicker(currentTrack.toString())
        .setContentIntent(pi)
        .build();
		
		notiMgr.notify(0, notification);
		startForeground(0, notification);
		wifiLock.acquire();*/
		
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
		} catch(Exception e) {
			Log.e(MainActivity.LOG_TAG, e.toString());
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
