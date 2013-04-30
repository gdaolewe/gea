package net.kenpowers.gea;

import java.io.InputStream;
import java.util.Arrays;

import net.kenpowers.gea.MusicServiceWrapper.SearchCompleteListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_album)
public class AlbumActivity extends SherlockActivity {
	private Album album;
	private Track[] tracks;
	private MusicServiceWrapper music;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
	    getSupportActionBar().setHomeButtonEnabled(true);
	    
	    setUpList();
	    
		Intent intent = getIntent();
		String key = intent.getStringExtra("key");
		music = MusicServiceWrapper.getInstance();
		setUpAlbumAndTracks(key);
	}

	private void setUpAlbumAndTracks(String albumKey) {
		String[] keys = {albumKey};
		//Gets album object for this album
		music.getMusicServiceObjectsForKeys(keys, new SearchCompleteListener() {
			@Override
			public void onSearchComplete(MusicServiceObject[] results) {
				album = (Album)results[0];
				((TextView)findViewById(R.id.artist)).setText(album.getArtist());
				((TextView)findViewById(R.id.album)).setText(album.getName());
				downloadAlbumArt(album.getAlbumArtURL());
				//And when album object is obtained, gets track objects for this album
				music.getMusicServiceObjectsForKeys(album.getTrackKeys(), new SearchCompleteListener() {
						public void onSearchComplete(MusicServiceObject[] results) {
							tracks = new Track[results.length];
							
							for (int i=0; i<results.length; i++) {
								tracks[i] = (Track)results[i];
							}
							//sorts Tracks in ascending order of trackNum
							Arrays.sort(tracks);
							
							ListView list = (ListView)findViewById(R.id.tracks);
							list.setAdapter(new SearchArrayAdapter(AlbumActivity.this, R.layout.search_result, tracks, false));
					}
				});
			}
		});
	}
	
	private void setUpList() {
		ListView list = (ListView)findViewById(R.id.tracks);
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
