package net.kenpowers.gea;

import java.io.InputStream;

import java.util.HashMap;

import org.json.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.app.SearchManager;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.widget.SearchView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import android.util.Log;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class MainActivity extends SherlockFragmentActivity implements RequestTaskCompleteListener, 
														TrackChangedListener {
	
	private static Context context;
	static final String LOG_TAG = "Gea";
	private String baseURL;
	
	MusicServiceWrapper music;
	private Track currentTrack;
	
	private GoogleMap gmap;
	
	private int volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        MainActivity.context = getApplicationContext();
        
        Log.i(MainActivity.LOG_TAG, "MainActivity started");
        
        //If in debug mode, connect to Gea server running on localhost, else connect to production server
        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
        if (isDebuggable)
        	baseURL = GeaServerConstants.LOCALHOST_BASE_URL;
        else
        	baseURL = GeaServerConstants.NET_BASE_URL;
        
        volume = 100;
        music = MusicServiceWrapper.getInstance();
        music.registerTrackChangedListener(MainActivity.this);
        setUpSeekBarListeners();
        
        setUpMapIfNeeded();
    }
    
    @Override
    protected void onResume() {
    	((SeekBar)findViewById(R.id.volumeSeekBar)).setProgress(volume);
    	music = MusicServiceWrapper.getInstance();
    	music.setPlayerVolume(volume);
    	super.onResume();
    }
    
    private void setUpSeekBarListeners() {
    	//set up change listener for song progress seek bar
    	((SeekBar)findViewById(R.id.progressSeekBar)).setOnSeekBarChangeListener(
        		new SeekBar.OnSeekBarChangeListener() {
        			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        				if (fromUser) {	//only skip if bar moved by user
        					int seconds = getSecondsFromProgress(progress, music.getPlayerDuration()/1000);
        					String position = getFormattedTimeFromSeconds(seconds);
        					((TextView) findViewById(R.id.currentPositionText)).setText(position);
        				}
        			}
        			public void onStartTrackingTouch(SeekBar seekBar) {
        				trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
        			}
        			public void onStopTrackingTouch(SeekBar seekBar) {
        				music.seekPlayerTo(getSecondsFromProgress(seekBar.getProgress(),
        							music.getPlayerDuration()/1000));
        				trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
        			}
        });
        //set up change listener for volume control seek bar
        ((SeekBar)findViewById(R.id.volumeSeekBar)).setOnSeekBarChangeListener(
        		new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {}
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						if (fromUser) {
							volume = progress;
							music.setPlayerVolume(progress);
						}
					}
		});
        
    }
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (gmap == null) {
            gmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            // Check if we were successful in obtaining the map.
            if (gmap != null) {
                // The Map is verified. It is now safe to manipulate the map.
            	Criteria criteria = new Criteria();
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);
                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
                gmap.moveCamera(center);
                gmap.moveCamera(zoom);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        
     // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(MainActivity.SEARCH_SERVICE);
        com.actionbarsherlock.widget.SearchView searchView = (com.actionbarsherlock.widget.SearchView) menu.findItem(R.id.searchField)
        																								   .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return true;
    }
    
    @Override
	public void onDestroy() {
    	super.onDestroy();
    	//music.cleanup();
	}
    
    public static Context getAppContext() {
    	return MainActivity.context;
    }
    
    public void onUpButtonClicked(View view) {
    	ImageButton upButton = (ImageButton)view;
    	if (upButton.getId() != R.id.approval_up_button)
    		return;
    	//show clicked icon
    	upButton.setImageResource(R.drawable.thumbup_over);
    	//return to unclicked icon after delay
    	new Handler().postDelayed(new Runnable() {
    		public void run() {
    			ImageButton upButton = ((ImageButton)findViewById(R.id.approval_up_button));
    			upButton.setImageResource(R.drawable.thumbup);
    		}
    	}, 100);
    	if (currentTrack != null)
    		sendApprovalRequest(true);
    }
    public void onDownButtonClicked(View view) {
    	ImageButton downButton = (ImageButton)view;
    	if (downButton.getId() != R.id.approval_down_button)
    		return;
    	//show clicked icon
    	downButton.setImageResource(R.drawable.thumbdown_over);
    	//return to unclicked icon after delay
    	new Handler().postDelayed(new Runnable() {
    		public void run() {
    			ImageButton downButton = ((ImageButton)findViewById(R.id.approval_down_button));
    			downButton.setImageResource(R.drawable.thumbdown);
    		}
    	}, 100);
    	if (currentTrack != null)
    		sendApprovalRequest(false);
    }
    
    public void sendApprovalRequest(boolean trackLiked) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("from", "rdio");
        params.put("id", currentTrack.getKey());
        params.put("verdict", trackLiked? "like" : "dislike");
        
        new RequestTask(this).execute(
        		new GeaPOSTRequest(baseURL + GeaServerConstants.BASE_RATE_QUERY, params));
    }
    
    public void onTaskComplete(GeaServerRequest request, String result) {
    	if (result==null || request==null) {
    		Log.e(LOG_TAG, "Error fetching JSON");
    		return;
    	}
    		
    }
    
    public void togglePaused(View view) {
    	if (view.getId() == R.id.play_pause_button)
    		music.togglePlayerPaused();
    	ImageButton button = (ImageButton)findViewById(R.id.play_pause_button);
    	if (music.playerIsPlaying()) {
    		trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
    		
    		button.setImageResource(R.drawable.pause);
    	}
    	else {
    		trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
    		button.setImageResource(R.drawable.play);
    	}
    }
    
    private int getProgressPercent(int position, int duration) {
    	double progress = ((double)position/duration)*100;
    	return (int)progress;
    }
    
    private int getSecondsFromProgress(int progress, int duration) {
    	double seconds = (double)progress*duration / 100;
    	return (int)seconds;
    }
    
    private String getFormattedTimeFromSeconds(int seconds) {
    	int secs = seconds % 60;
    	int minutes = seconds / 60;
    	return "" + (minutes<10 ? "0":"") + minutes + ":" + (secs<10? "0":"") + secs;
    }
    
    private Handler trackPositionUpdateHandler = new Handler();
    
    public void onTrackChanged(Track track) {
    	if (track==null) {
    		((ImageButton)findViewById(R.id.play_pause_button)).setImageResource(R.drawable.play);
        	trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
    	} else {
    		Log.d(LOG_TAG, "Now playing " + track.toString());
    		currentTrack = track;
    		downloadAlbumArt(currentTrack.getAlbumArtURL());
	    	String trackInfo = String.format(currentTrack.toString());
	    	((TextView)findViewById(R.id.songInfoText)).setText(trackInfo);
	    	//((TextView)findViewById(R.id.play_pause_button)).setText("Pause");
	    	((ImageButton)findViewById(R.id.play_pause_button)).setImageResource(R.drawable.pause);
	    	
	    	trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
    	}
    }
    
    
    
    private Runnable updateTrackPositionTask = new Runnable() {
	    public void run() {
	    	int durationSeconds = music.getPlayerDuration()/1000;
	    	String durationString = getFormattedTimeFromSeconds(durationSeconds);
	    	((TextView)findViewById(R.id.durationText)).setText(durationString);
	    	
			int currentPositionSeconds = music.getPlayerPosition()/1000;
	    	String currentPositionString = getFormattedTimeFromSeconds(currentPositionSeconds);
			((TextView)findViewById(R.id.currentPositionText)).setText(currentPositionString);
			trackPositionUpdateHandler.postDelayed(this, 100);
			
			int progress = getProgressPercent(currentPositionSeconds, durationSeconds);
			((SeekBar)findViewById(R.id.progressSeekBar)).setProgress(progress);
	    }
    };
    
    private void downloadAlbumArt(String url) {
    	ImageView albumArtView = (ImageView)findViewById(R.id.playerAlbumArt);
    	new DownloadImageTask(albumArtView).execute(url);
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView image;

	    public DownloadImageTask(ImageView image) {
	        this.image = image;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String url = urls[0];
	        Bitmap result = null;
	        try {
	            InputStream in = new java.net.URL(url).openStream();
	            result = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return result;
	    }

	    protected void onPostExecute(Bitmap result) {
	        image.setImageBitmap(result);
	    }
	}
    
    public boolean onOptionsItemSelected (MenuItem item) {
		return true;
		
	}
    
}

