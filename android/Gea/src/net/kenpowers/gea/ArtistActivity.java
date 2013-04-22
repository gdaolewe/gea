package net.kenpowers.gea;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ArtistActivity extends SherlockActivity implements SearchCompleteListener {
	private Artist artist;
	private Album[] albums;
	private MusicServiceWrapper music;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist);
	    getSupportActionBar().setHomeButtonEnabled(true);
	    
	    Intent intent = getIntent();
	    String key = intent.getStringExtra("key");
		music = MusicServiceWrapper.getInstance();
		music.registerSearchCompleteListener(this);
		String[] keys = {key};
		music.getMusicServiceObjectsForKeys(keys);
	}

	@Override
	public void onSearchComplete(MusicServiceObject[] results) {
		if (results[0].getType().equals("artist")) {
			artist = (Artist)results[0];
			((TextView)findViewById(R.id.artist)).setText(artist.getName());
			music.getAlbumsForArtist(artist);
		} else if (results[0].getType().equals("album")) {
			albums = new Album[results.length];
			String[] albumsStrings = new String[results.length];
			for (int i=0; i<results.length; i++) {
				albums[i] = (Album)results[i];
				albumsStrings[i] = results[i].getName();
			}
			ListView list = (ListView)findViewById(R.id.albums);
			list.setAdapter(new ArrayAdapter<String>(this, R.layout.search_result, albumsStrings));
			list.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					listItemSelected(position);
				}
			});
			list.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { /*do nothing*/ }
				@Override
				public void onNothingSelected(AdapterView<?> parent) { /*do nothing*/ }
			});
		}
	}
	
	public void listItemSelected(int position) {
		Intent intent = new Intent(this, AlbumActivity.class);
		intent.putExtra("key", albums[position].getKey());
		startActivity(intent);
	}
}
