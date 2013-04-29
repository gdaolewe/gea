package net.kenpowers.gea;

import java.io.InputStream;
import java.util.Arrays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_album)
public class AlbumActivity extends SherlockActivity implements SearchCompleteListener {
	private Album album;
	private Track[] tracks;
	private MusicServiceWrapper music;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
	    getSupportActionBar().setHomeButtonEnabled(true);
	    
		Intent intent = getIntent();
		String key = intent.getStringExtra("key");
		music = MusicServiceWrapper.getInstance();
		music.registerSearchCompleteListener(this);
		String[] keys = {key};
		music.getMusicServiceObjectsForKeys(keys, this);
	}
	
	public void onSearchComplete(MusicServiceObject[] results) {
		if (results[0].getType().equals("album")) {
			album = (Album)results[0];
			((TextView)findViewById(R.id.artist)).setText(album.getArtist());
			((TextView)findViewById(R.id.album)).setText(album.getName());
			music.getMusicServiceObjectsForKeys(album.getTrackKeys(), this);
			downloadAlbumArt(album.getAlbumArtURL());
		} else if (results[0].getType().equals("track")) {
			tracks = new Track[results.length];
			
			for (int i=0; i<results.length; i++) {
				tracks[i] = (Track)results[i];
			}
			//sorts Tracks in ascending order of trackNum
			Arrays.sort(tracks);
			String[] tracksStrings = new String[tracks.length];										// sets up list of display strings
			for (int i=0; i<tracks.length; i++)														// for list view in same order
				tracksStrings[i] = tracks[i].getNum() + " - " + tracks[i].getName();				// as Tracks
			ListView list = (ListView)findViewById(R.id.tracks);
			list.setAdapter(new ArrayAdapter<String>(this, R.layout.search_result, tracksStrings));
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
		music.getPlayerForTrack(tracks[position]);
		music.setPlaylist(tracks, position);
		startActivity(new Intent(this, MainActivity_.class));
	}
	
	public boolean onOptionsItemSelected (MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();	
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@ViewById(R.id.albumArt)
	ImageView albumArt;
	
	@Background
	void downloadAlbumArt(String url) {
		Bitmap bmp = null;
		try {
            InputStream in = new java.net.URL(url).openStream();
            bmp = BitmapFactory.decodeStream(in);
            setAlbumArt(bmp);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
		
	}
	@UiThread
	void setAlbumArt(Bitmap bmp) {
		albumArt.setImageBitmap(bmp);
	}
}
