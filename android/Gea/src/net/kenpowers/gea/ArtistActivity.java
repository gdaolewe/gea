package net.kenpowers.gea;

import java.io.InputStream;

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

@EActivity(R.layout.activity_artist)
public class ArtistActivity extends SherlockActivity {
	private Artist artist;
	private Album[] albums;
	private MusicServiceWrapper music;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist);
	    getSupportActionBar().setHomeButtonEnabled(true);
	    
	    setUpList();
	    
	    Intent intent = getIntent();
	    String key = intent.getStringExtra("key");
		music = MusicServiceWrapper.getInstance();
		setUpArtistAndAlbums(key);
	}

	private void setUpArtistAndAlbums(String artistKey) {
		String[] keys = {artistKey};
		//Gets artist object for this artist
		music.getMusicServiceObjectsForKeys(keys, new SearchCompleteListener() {
			@Override
			public void onSearchComplete(MusicServiceObject[] results) {
				artist = (Artist)results[0];
				((TextView)findViewById(R.id.artist)).setText(artist.getName());
				downloadArtistImage(artist.getImageURL());
				//And when artist object is obtained, gets album objects for this artist
				music.getAlbumsForArtist(artist, new SearchCompleteListener() {
					@Override
					public void onSearchComplete(MusicServiceObject[] results) {
						albums = new Album[results.length];
						for (int i=0; i<results.length; i++)
							albums[i] = (Album)results[i];
						ListView list = (ListView)findViewById(R.id.albums);
						list.setAdapter(new SearchArrayAdapter(ArtistActivity.this, R.layout.search_result, albums, false));
					}
				});
				
			}
		});
	}
	
	private void setUpList() {
		ListView list = (ListView)findViewById(R.id.albums);
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
		Intent intent = new Intent(this, AlbumActivity_.class);
		intent.putExtra("key", albums[position].getKey());
		startActivity(intent);
	}
	
	public boolean onOptionsItemSelected (MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();	
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@ViewById(R.id.image)
	ImageView image;
	
	@Background
	void downloadArtistImage(String url) {
		Bitmap bmp = null;
		try {
            InputStream in = new java.net.URL(url).openStream();
            bmp = BitmapFactory.decodeStream(in);
            setImage(bmp);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
	}
	@UiThread
	void setImage(Bitmap bmp) {
		image.setImageBitmap(bmp);
	}
}
