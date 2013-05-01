package net.kenpowers.gea;

import java.util.Arrays;

import net.kenpowers.gea.MusicServiceWrapper.SearchCompleteListener;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockListActivity implements SearchCompleteListener {
	private MusicServiceObject searchResults[];
	private MusicServiceWrapper music;
	String query;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.search);
	    getSupportActionBar().setHomeButtonEnabled(true);
	    
	    setUpList();
	    
	    handleIntent(getIntent());
	}
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getSupportMenuInflater().inflate(R.menu.main, menu);
	        
	        SearchManager searchManager = (SearchManager) getSystemService(MainActivity_.SEARCH_SERVICE);
	        com.actionbarsherlock.widget.SearchView searchView = (com.actionbarsherlock.widget.SearchView) menu.findItem(R.id.searchField)
					   .getActionView();
	        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	        searchView.setSubmitButtonEnabled(true);
	        searchView.setQueryRefinementEnabled(true);
	        searchView.setQuery(query, false);
	        
	        return true;
	 }
	 
	 @Override
	 protected void onNewIntent(Intent intent) {
		 boolean shouldSearchForSong 	= ((CheckBox)findViewById(R.id.songCheckBox)).isChecked();
		 boolean shouldSearchForAlbum 	= ((CheckBox)findViewById(R.id.albumCheckBox)).isChecked();
		 boolean shouldSearchForArtist 	= ((CheckBox)findViewById(R.id.artistCheckBox)).isChecked();
		 intent.putExtra("Song", shouldSearchForSong);
		 intent.putExtra("Album", shouldSearchForAlbum);
		 intent.putExtra("Artist", shouldSearchForArtist);
		 setIntent(intent);
		 handleIntent(intent);
	 }
	 
	 private void handleIntent(Intent intent) {
		 
		 if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			 boolean shouldSearchForSong = intent.getBooleanExtra("Song", false);
			 boolean shouldSearchForAlbum = intent.getBooleanExtra("Album", false);
			 boolean shouldSearchForArtist = intent.getBooleanExtra("Artist", false);
			 ((CheckBox)findViewById(R.id.songCheckBox)).setChecked(shouldSearchForSong);
	    	 ((CheckBox)findViewById(R.id.albumCheckBox)).setChecked(shouldSearchForAlbum);
	    	 ((CheckBox)findViewById(R.id.artistCheckBox)).setChecked(shouldSearchForArtist);
			 
	    	 String type = "";
	    	 if (shouldSearchForSong) {
	    		 type += "Song";
	    		 if (shouldSearchForAlbum || shouldSearchForArtist)
	    			 type += ",";
	    	 }
	    	 if (shouldSearchForAlbum) {
	    		 type += "Album";
	    		 if (shouldSearchForArtist)
	    			 type +=  ",";
	    	 }
	    	 if (shouldSearchForArtist) {
	    		 type += "artist";
	    	 }
	    	 if (! (shouldSearchForSong | shouldSearchForAlbum | shouldSearchForArtist) )
				 type = "Song";
		      music = MusicServiceWrapper.getInstance();
		      query = intent.getStringExtra(SearchManager.QUERY);
		      performSearch(query, type);
		 }
	 }
	
	public void performSearch(String query, String type) {
		if (query.length() < 1) {
			Log.e(MainActivity_.LOG_TAG, "Search submitted with no search text entered");
			return;
		}
		Log.i(MainActivity_.LOG_TAG, "Searched for '" + query + "' with types " + type);
		music.search(query, type, new SearchCompleteListener() {
			@Override
			public void onSearchComplete(MusicServiceObject[] results) {
				Log.i(MainActivity_.LOG_TAG, "Search returned " + results.length + " results");
				if (results.length < 1) {
					Log.i(MainActivity_.LOG_TAG, "No search results");
					String[] resultsStrings = {"No search results"};
					setListAdapter(new ArrayAdapter<String>(SearchActivity.this, R.layout.search_result, resultsStrings));
					return;
				}
				searchResults = results;
				setListAdapter(new SearchArrayAdapter(SearchActivity.this, R.layout.search_result, results, true));
			}
		});
	}
	
	public void onSearchComplete(MusicServiceObject[] results) {
		Log.i(MainActivity_.LOG_TAG, "Search returned " + results.length + " results");
		if (results.length < 1) {
			Log.i(MainActivity_.LOG_TAG, "No search results");
			String[] resultsStrings = {"No search results"};
			setListAdapter(new ArrayAdapter<String>(this, R.layout.search_result, resultsStrings));
			return;
		}
			
		searchResults = results;
		
		setListAdapter(new SearchArrayAdapter(this, R.layout.search_result, results, true));
		
		ListView listview = getListView();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listItemSelected(position);
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { /*do nothing*/ }
			public void onNothingSelected(AdapterView<?> parent) { /*do nothing*/ }
		});
	}
	
	public void listItemSelected(final int position) {
		Log.d(MainActivity_.LOG_TAG, "item selected");
		MusicServiceObject item = searchResults[position];
		if (item.getType().equals("track")) {
			Track track = (Track)item;
			String[] keys = {track.getAlbumKey()};
			music.getMusicServiceObjectsForKeys(keys, new SearchCompleteListener() {
				@Override
				public void onSearchComplete(MusicServiceObject[] results) {
					String[] trackKeys = ((Album)results[0]).getTrackKeys();
					music.getMusicServiceObjectsForKeys(trackKeys, new SearchCompleteListener() {
						@Override
						public void onSearchComplete(MusicServiceObject[] results) {
							Track[] tracks = new Track[results.length];
							for (int i=0; i<results.length; i++)
								tracks[i] = (Track)results[i];
							Arrays.sort(tracks);
							music.setPlaylist(tracks, position);
						}
					});
				}
			});
			music.getPlayerForTrack(track);
			finish();
		} else if (item.getType().equals("album")) {
			Intent intent = new Intent(this, AlbumActivity_.class);
			intent.putExtra("key", item.getKey());
			startActivity(intent);
		} else if (item.getType().equals("artist")) {
			Intent intent = new Intent(this, ArtistActivity_.class);
			intent.putExtra("key", item.getKey());
			startActivity(intent);
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

	private void setUpList() {
		ListView listview = getListView();
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listItemSelected(position);
			}
		});
		listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { /*do nothing*/ }
			public void onNothingSelected(AdapterView<?> parent) { /*do nothing*/ }
		});
	}
	
}
