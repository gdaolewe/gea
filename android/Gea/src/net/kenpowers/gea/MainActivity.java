package net.kenpowers.gea;

import org.json.simple.*;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity implements RequestTaskCompleteListener, 
														TrackChangedListener {
	
	private static Context context;
	static final String LOG_TAG = "Gea";
	final String baseURL = "http://gea.kenpowers.net";
	TextView serverResponseView;
	MusicServiceWrapper music;
	private Track currentTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        
        Log.d(MainActivity.LOG_TAG, "activity started");
                
        //serverResponseView = (TextView)findViewById(R.id.titleText);
        
        String[] params = {"song","1"};
        new RequestTask(this).execute(new GeaGETRequest(baseURL, params));
        
        music = MusicServiceWrapper.getInstance(this);
        music.registerTrackChangedListener(this);
        //music.search("Lethargica", "Song");
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
    
    /*public void searchSubmitClicked(View view) {
    	if (view.getId() == R.id.searchSubmit) {
    		String searchText = ((EditText)findViewById(R.id.searchField)).getText().toString();
    		if (searchText.length() < 1) {
    			Log.e(LOG_TAG, "Search submitted with no search text entered");
    			return;
    		}
    		Log.d(LOG_TAG, ((EditText)findViewById(R.id.searchField)).getText().toString() );
    		music.search( ((EditText)findViewById(R.id.searchField)).getText().toString(), "Song");
    	}
    }*/
    
    public void onTaskComplete(GeaServerRequest request, String result) {
    	if (result==null || request==null) {
    		Log.e(LOG_TAG, "Error fetching JSON");
    		return;
    	}
    	JSONObject obj = (JSONObject) JSONValue.parse(result);
    	
    	
    	/*if ( request.getRequestMethod().equals("GET") ) {
    		if ( ((GeaGETRequest)request).getParameterAtIndex(0).equals("song") ) {
    			((TextView)findViewById(R.id.titleText)).setText("Title: " + obj.get("title"));
        		String[] params = {"artist", obj.get("artistID").toString() };
        		new RequestTask(this).execute(new GeaGETRequest(baseURL, params));
        		params = new String[]{"album", (String)obj.get("albumID").toString() };
        		new RequestTask(this).execute(new GeaGETRequest(baseURL, params));
    		} else if ( ((GeaGETRequest)request).getParameterAtIndex(0).equals("artist") ) {
    			((TextView)findViewById(R.id.artistText)).setText("Artist: " + obj.get("name").toString() );
    		} else if ( ((GeaGETRequest)request).getParameterAtIndex(0).equals("album") ) {
    			((TextView)findViewById(R.id.albumText)).setText("Album: " + obj.get("title").toString() );
    		}
    			
    	}*/
    	
    	
    }
    
    public void onTrackChanged(Track track) {
    	if (track== null) {
    		((TextView)findViewById(R.id.songInfoText)).setText("Nothing playing");
        	((TextView)findViewById(R.id.durationText)).setText("");
        	((TextView)findViewById(R.id.play)).setText("Play");
    	} else {
    		currentTrack = track;
	    	String trackInfo = String.format("%s - %s (%s)", currentTrack.getName(), 
	    			currentTrack.getArtist(), currentTrack.getAlbum());
	    	((TextView)findViewById(R.id.songInfoText)).setText(trackInfo);
	    	((TextView)findViewById(R.id.durationText)).setText(""+track.getDuration());
	    	((TextView)findViewById(R.id.play)).setText("Pause");
    	}
    }
    
}

