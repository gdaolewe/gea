package net.kenpowers.gea;

import org.json.simple.*;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.Menu;
import android.widget.*;

public class MainActivity extends Activity implements RequestTaskCompleteListener {
	
	static final String LOG_TAG = "Gea";
	final String baseURL = "http://gea.kenpowers.net";
	TextView serverResponseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(MainActivity.LOG_TAG, "activity started");
                
        //serverResponseView = (TextView)findViewById(R.id.titleText);
        
        String[] params = {"song","1"};
        new RequestTask(this).execute(new GeaGETRequest(baseURL, params));   
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onTaskComplete(GeaServerRequest request, String result) {
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

