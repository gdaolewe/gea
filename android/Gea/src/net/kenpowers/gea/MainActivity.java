package net.kenpowers.gea;

import java.util.HashMap;

import org.json.simple.*;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.*;

public class MainActivity extends FragmentActivity implements RequestTaskCompleteListener {
	
	static final String LOG_TAG = "Gea";
	final String baseURL = "http://gea.kenpowers.net";
	TextView serverResponseView;
	MusicServiceWrapper music;
	private GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(MainActivity.LOG_TAG, "activity started");
                
        //serverResponseView = (TextView)findViewById(R.id.titleText);
        
        String[] params = {"song","1"};
        new RequestTask(this).execute(new GeaGETRequest(baseURL, params));
        
        //example for how to format HashMap for POST request
        HashMap<String, String> examplePOSTRequest = new HashMap<String, String>();
        examplePOSTRequest.put("from", "rdio");
        examplePOSTRequest.put("id", "t2491851");
        examplePOSTRequest.put("verdict", "like");
        
        //uncomment this code to add Google MapFragment
        //gmap = ((MapFragment)getFragmentManager().findFragmentById(R.layout.fragment1).getMap());
        
        music = new MusicServiceWrapper(this);
        //music.search("Lethargica", "Song");
        
        //MapFragment mf = MapFragment.newInstance();
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void searchSubmitClicked(View view) {
    	if (view.getId() == R.id.searchSubmit) {
    		Log.d(LOG_TAG, ((EditText)findViewById(R.id.searchField)).getText().toString() );
    		music.search( ((EditText)findViewById(R.id.searchField)).getText().toString(), "Song");
    	}
    }
    
    public void onTaskComplete(GeaServerRequest request, String result) {
    	if (result==null || request==null) {
    		Log.e(LOG_TAG, "Error fetching JSON");
    		return;
    	}
    	JSONObject obj = (JSONObject) JSONValue.parse(result);
    	
    	
    	if ( request.getRequestMethod().equals("GET") ) {
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
    			
    	}
    	
    	
    }
    
}

