package net.kenpowers.gea;

import java.io.InputStream;

import java.util.HashMap;

import org.json.simple.*;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity implements RequestTaskCompleteListener, 
														TrackChangedListener {
	
	private static Context context;
	static final String LOG_TAG = "Gea";
	//final String baseURL = "http://gea.kenpowers.net";
	final String baseURL = "http://10.0.2.2:3000";
	final String baseRateQuery = "/rate?";
	
	MusicServiceWrapper music;
	private Track currentTrack;
	
	private GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        
        
        Log.d(MainActivity.LOG_TAG, "MainActivity started");
                
        ((SeekBar)findViewById(R.id.progressSeekBar)).setOnSeekBarChangeListener(
        		new SeekBar.OnSeekBarChangeListener() {
        			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
        			public void onStartTrackingTouch(SeekBar seekBar) {
        				trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
        			}
        			public void onStopTrackingTouch(SeekBar seekBar) {
        				music.seekPlayerTo(getSecondsFromProgress(seekBar.getProgress(),
        							music.getPlayerDuration()/1000));
        				trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
        			}
        });
        
        ((SeekBar)findViewById(R.id.volumeSeekBar)).setOnSeekBarChangeListener(
        		new SeekBar.OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						if (fromUser)
							music.setPlayerVolume(progress);
						
					}
				});
        
        //uncomment this code to add Google MapFragment
        //gmap = ((MapFragment)getFragmentManager().findFragmentById(R.layout.fragment1).getMap());
        //MapFragment mf = MapFragment.newInstance();
        
        music = MusicServiceWrapper.getInstance(this);
        music.registerTrackChangedListener(this);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
     // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchField).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return true;
    }
    
    @Override
	public void onDestroy() {
		music.cleanup();
		super.onDestroy();
	}
    
    public static Context getAppContext() {
    	return MainActivity.context;
    }
    
    public void onUpButtonClicked(View view) {
    	if (view.getId() != R.id.approval_up_button || currentTrack == null)
    		return;
    	Log.d(LOG_TAG, "up button clicked");  
    	sendApprovalRequest(true);
    }
    public void onDownButtonClicked(View view) {
    	if (view.getId() != R.id.approval_down_button || currentTrack == null)
    		return;
    	Log.d(LOG_TAG, "down button clicked");	
    	sendApprovalRequest(false);
    }
    
    public void sendApprovalRequest(boolean trackLiked) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("from", "rdio");
        params.put("id", currentTrack.getKey());
        params.put("verdict", trackLiked? "like" : "dislike");
        
        new RequestTask(this).execute(new GeaPOSTRequest(baseURL + baseRateQuery, params));
    }
    
    public void onTaskComplete(GeaServerRequest request, String result) {
    	if (result==null || request==null) {
    		Log.e(LOG_TAG, "Error fetching JSON");
    		return;
    	}
    	//JSONObject obj = (JSONObject) JSONValue.parse(result);
    	
    	
    	
    	
    	
    }
    
    private String secondsToTimeFormat(int time) {
    	int seconds = time % 60;
    	int minutes = time / 60;
    	return "" + (minutes<10 ? "0":"") + minutes + ":" + (seconds<10? "0":"") + seconds;
    }
    
    private Handler trackPositionUpdateHandler = new Handler();
    
    public void onTrackChanged(Track track) {
    	if (track==null) {
    		((TextView)findViewById(R.id.play_pause_button)).setText("Play");
        	trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
    	} else {
    		Log.d(LOG_TAG, track.toString());
    		currentTrack = track;
    		downloadAlbumArt(currentTrack.getAlbumArtURL());
	    	String trackInfo = String.format(currentTrack.toString());
	    	((TextView)findViewById(R.id.songInfoText)).setText(trackInfo);
	    	((TextView)findViewById(R.id.play_pause_button)).setText("Pause");
	    	
	    	trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
    	}
    }
    
    public void togglePaused(View view) {
    	if (view.getId() == R.id.play_pause_button)
    		music.togglePlayerPaused();
    	TextView button = (TextView)findViewById(R.id.play_pause_button);
    	if (music.playerIsPlaying()) {
    		trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
    		button.setText("Pause");
    	}
    	else {
    		trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
    		button.setText("Play");
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
    
    private Runnable updateTrackPositionTask = new Runnable() {
	    public void run() {
	    	int durationSeconds = music.getPlayerDuration()/1000;
	    	String durationString = secondsToTimeFormat(durationSeconds);
	    	((TextView)findViewById(R.id.durationText)).setText(durationString);
	    	
			int currentPositionSeconds = music.getPlayerPosition()/1000;
	    	String currentPositionString = secondsToTimeFormat(currentPositionSeconds);
			((TextView)findViewById(R.id.currentPositionText)).setText(currentPositionString);
			trackPositionUpdateHandler.postDelayed(this, 100);
			
			int progress = getProgressPercent(currentPositionSeconds, durationSeconds);
			//Log.d(LOG_TAG, ""+progress);
			((SeekBar)findViewById(R.id.progressSeekBar)).setProgress(progress);
	    }
    };
    
    private void downloadAlbumArt(String url) {
    	new DownloadImageTask((ImageView)findViewById(R.id.playerAlbumArt)).execute(url);
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
    
}

