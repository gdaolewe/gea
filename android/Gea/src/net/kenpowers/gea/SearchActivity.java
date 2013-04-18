package net.kenpowers.gea;

//import android.app.ListActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends SherlockListActivity implements SearchCompleteListener {
	private MusicServiceObject searchResults[];
	private MusicServiceWrapper music;
	private boolean musicServiceBound = false;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.search);
	    getSupportActionBar().setHomeButtonEnabled(true);

	    // Get the intent, verify the action and get the query
	    Intent searchIntent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(searchIntent.getAction())) {
	      String query = searchIntent.getStringExtra(SearchManager.QUERY);
	    
	    Intent bindIntent = new Intent(this, MusicServiceWrapper.class);
	    bindService(bindIntent, msConnection, Context.BIND_AUTO_CREATE);
	    
	    }
	    
	    
	}
	
	private ServiceConnection msConnection = new ServiceConnection() {
    	@Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicServiceWrapper.MusicBinder binder = (MusicServiceWrapper.MusicBinder) service;
            music = binder.getService();
            music.registerSearchCompleteListener(SearchActivity.this);
            musicServiceBound = true;
            Intent searchIntent = SearchActivity.this.getIntent();
            SearchActivity.this.performSearch(searchIntent.getStringExtra(SearchManager.QUERY));
    	}
    	@Override
        public void onServiceDisconnected(ComponentName arg0) {
            musicServiceBound = false;
        }
    };
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getSupportMenuInflater().inflate(R.menu.main, menu);
	        return true;
	 }
	 
	 @Override
	 protected void onStop() {
		 super.onStop();
		 unbindService(msConnection);
	 }
	
	
	
	public void performSearch(String query) {
		if (query.length() < 1) {
			Log.e(MainActivity.LOG_TAG, "Search submitted with no search text entered");
			return;
		}
		Log.d(MainActivity.LOG_TAG, query);
		music.search(query, "Song");
	}
	
	public void onSearchComplete(MusicServiceObject[] results) {
		searchResults = results;
		
		String resultsStrings[] = new String[searchResults.length];
		for (int i=0; i<results.length; i++)
			resultsStrings[i] = results[i].toString();
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.search_result, resultsStrings));
		
		ListView listview = getListView();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listItemSelected(position);
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				listItemSelected(position);
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	
	public void listItemSelected(int position) {
		MusicServiceObject item = searchResults[position];
		if (item.getType().equals("track")) {
			music.getPlayerForTrack((Track)item);
			finish();
		}
	}
	
	public boolean onOptionsItemSelected (MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
			finish();	
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
}
