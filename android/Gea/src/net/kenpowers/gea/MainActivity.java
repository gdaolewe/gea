package net.kenpowers.gea;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.*;

public class MainActivity extends Activity implements RequestTaskCompleteListener {
	
	static final String LOG_TAG = "Gea";
	TextView serverResponseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        serverResponseView = (TextView)findViewById(R.id.serverResponse);
        
        new RequestTask(this).execute("http://www.google.com");    
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onTaskComplete(String result) {
    	serverResponseView.append(result);
    }
    
}

