package net.kenpowers.gea;

import org.json.simple.*;

import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.Menu;
import android.widget.*;

public class MainActivity extends Activity implements RequestTaskCompleteListener {
	
	static final String LOG_TAG = "Gea";
	final String baseURL = "http://10.0.2.2:3000";
	TextView serverResponseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(MainActivity.LOG_TAG, "activity started");
                
        //serverResponseView = (TextView)findViewById(R.id.titleText);
        
        new RequestTask(this).execute(baseURL + "/song/1");   
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onTaskComplete(String result) {
    	JSONObject obj = (JSONObject) JSONValue.parse(result);
    	if (obj.get("title") != null && obj.get("songID") != null) {
    		((TextView)findViewById(R.id.titleText)).setText("Title: " + obj.get("title"));
    		new RequestTask(this).execute(baseURL + "/artist/" + obj.get("artistID"));
    		new RequestTask(this).execute(baseURL + "/album/" + obj.get("albumID"));
    	}
    	else if (obj.get("name") != null) {
    		((TextView)findViewById(R.id.artistText)).setText("Artist: " + obj.get("name"));
    	} else if (obj.get("title") != null) {
    		((TextView)findViewById(R.id.albumText)).setText("Album: " + obj.get("title"));
    	}
    	
    	
    }
    
}

