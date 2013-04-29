package net.kenpowers.gea;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.app.SearchManager;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.EActivity;

@EActivity
public class MainActivity extends SherlockFragmentActivity implements TrackChangedListener {
	
	private static Context context;
	static final String LOG_TAG = "Gea";
	private String baseURL;
	
	MusicServiceWrapper music;
	private Track currentTrack;
	
	private GoogleMap gmap;
	private Marker here;
	private HashMap<String, BasicTrack[]> markers;
	
	private int volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        
        MainActivity.context = getApplicationContext();
        
        Log.i(LOG_TAG, "MainActivity started");
        
        //If in debug mode, connect to Gea server running on localhost, else connect to production server
        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
        if (isDebuggable)
        	baseURL = GeaServerHandler.LOCALHOST_BASE_URL;
        else
        	baseURL = GeaServerHandler.NET_BASE_URL;
        
        //For testing physical device against local server
        baseURL = "http://10.1.40.121:3000";
        //baseURL = "http://192.168.1.115:3000";
        
        music = MusicServiceWrapper.getInstance();
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment searchContext = getSupportFragmentManager().findFragmentById(R.id.searchContext);
        searchContext.getView().bringToFront();
        int color = getResources().getColor(R.color.abs__background_holo_light);
        searchContext.getView().setBackgroundColor(color);
        ft.hide(searchContext);
        Fragment launchFragment = getSupportFragmentManager().findFragmentById(R.id.launch);
        launchFragment.getView().bringToFront();
        Fragment topTracks = getSupportFragmentManager().findFragmentById(R.id.topTracks);
        topTracks.getView().bringToFront();
        ft.hide(topTracks);
        if (music.isReady())
        	ft.hide(launchFragment);
        ft.commit();
        
        
        volume = 100;
        
        music.registerMusicServiceReadyListener(new MusicServiceReadyListener() {
			@Override
			public void onMusicServiceReady() {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();	
				Fragment launchFragment = getSupportFragmentManager().findFragmentById(R.id.launch);
				ft.hide(launchFragment);
				ft.commit();
			}
        	
        });
        music.registerTrackChangedListener(this);
        setUpSeekBarListeners();
        
        setUpMapIfNeeded();
        getApprovalRequest(5);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	((SeekBar)findViewById(R.id.volumeSeekBar)).setProgress(volume);
    	music = MusicServiceWrapper.getInstance();
    	music.setPlayerVolume(volume);
    	onTrackChanged(music.getCurrentTrack());
    	if (currentTrack != null) {
    		onTrackChanged(currentTrack);
    		ImageButton upButton = ((ImageButton)findViewById(R.id.approval_up_button));
    		ImageButton downButton = ((ImageButton)findViewById(R.id.approval_down_button));
    		switch(currentTrack.isLiked()) {
	    		case Track.NOT_RATED:
	    			upButton.setImageResource(R.drawable.thumbup);
	    			downButton.setImageResource(R.drawable.thumbdown);
	    			break;
	    		case Track.LIKED:
	    			upButton.setImageResource(R.drawable.thumbup_over);
	    			downButton.setImageResource(R.drawable.thumbdown);
	    			break;
	    		case Track.DISLIKED:
	    			downButton.setImageResource(R.drawable.thumbdown_over);
	    			upButton.setImageResource(R.drawable.thumbup);
	    			break;
    		}
    	}
    }
    
    private void setUpSeekBarListeners() {
    	//set up change listener for song progress seek bar
    	((SeekBar)findViewById(R.id.progressSeekBar)).setOnSeekBarChangeListener(
        		new SeekBar.OnSeekBarChangeListener() {
        			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        				if (fromUser) {	//only seek if bar moved by user
        					int seconds = PlayerTimeFormatter.getSecondsFromProgress(progress, music.getPlayerDuration()/1000);
        					String position = PlayerTimeFormatter.getFormattedTimeFromSeconds(seconds);
        					((TextView) findViewById(R.id.currentPositionText)).setText(position);
        				}
        			}
        			public void onStartTrackingTouch(SeekBar seekBar) {
        				trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
        			}
        			public void onStopTrackingTouch(SeekBar seekBar) {
        				music.seekPlayerTo(PlayerTimeFormatter.getSecondsFromProgress(seekBar.getProgress(),
        							music.getPlayerDuration()/1000));
        				trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
        			}
        });
        //set up change listener for volume control seek bar
        ((SeekBar)findViewById(R.id.volumeSeekBar)).setOnSeekBarChangeListener(
        		new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {}
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						if (fromUser) {
							volume = progress;
							music.setPlayerVolume(progress);
						}
					}
		});
    }
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
    	SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (gmap == null) {
            gmap = mapFrag.getMap();
            mapFrag.getView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					setSearchContextVisible(false);
				}
            	
            });
            // Check if we were successful in obtaining the map.
            if (gmap != null) {
                // The Map is verified. It is now safe to manipulate the map.
            	Criteria criteria = new Criteria();
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);
                LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
                gmap.moveCamera(center);
                gmap.moveCamera(zoom);
                gmap.setOnMarkerClickListener(new OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {
						final BasicTrack[] topTracks = markers.get(marker.getTitle());
						String[] tracksStrings = new String[topTracks.length];
						for (int i=0; i<topTracks.length; i++)
							tracksStrings[i] = topTracks[i].toString();
						ListView list = (ListView)findViewById(R.id.topTracksList);
						list.setAdapter(new ArrayAdapter<String>(getAppContext(), R.layout.search_result, tracksStrings));
						list.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, 
													final int position, long id) {
								setTopTracksVisible(false);
								String[] trackKeys = new String[topTracks.length];
								for (int i=0; i<topTracks.length; i++)
									trackKeys[i] = topTracks[i].getKey();
								music.getMusicServiceObjectsForKeys(trackKeys, new SearchCompleteListener() {
									@Override
									public void onSearchComplete(MusicServiceObject[] results) {
										HashMap<String, Track> orderTracksMap = new HashMap<String, Track>();
										for (int i=0; i<results.length; i++) {
											Track result = (Track)results[i];
											orderTracksMap.put(result.getKey(), result);
										}
										Track[] tracks = new Track[results.length];
										for (int i=0; i<results.length; i++) {
											tracks[i] = orderTracksMap.get(topTracks[i].getKey());
											Log.d(LOG_TAG, tracks[i].getKey());
										}
										music.getPlayerForTrack(tracks[position]);
										music.setPlaylist(tracks, position);
									}
								});
							}
							
						});
						Button done = (Button)findViewById(R.id.topTracksDone);
						done.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
								Fragment topTracksFragment = getSupportFragmentManager().findFragmentById(R.id.topTracks);
								ft.hide(topTracksFragment);
								ft.commit();
							}
						});
						FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
						Fragment topTracksFragment = getSupportFragmentManager().findFragmentById(R.id.topTracks);
						ft.show(topTracksFragment);
						ft.commit();
						return true;
					}
                	
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        
     // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(MainActivity.SEARCH_SERVICE);
        com.actionbarsherlock.widget.SearchView searchView = (com.actionbarsherlock.widget.SearchView) menu.findItem(R.id.searchField)
        																								   .getActionView();
        searchView.setOnQueryTextFocusChangeListener(new com.actionbarsherlock.widget.SearchView.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				if (!hasFocus) {
					Log.d(LOG_TAG, "Search lost focus");
			        ft.hide(getSupportFragmentManager().findFragmentById(R.id.searchContext));
			        ft.commit();
				}
			}
        	
        });
        searchView.setOnQueryTextListener(new com.actionbarsherlock.widget.SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Search suggestions
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				if (newText.length() > 0) {
			        ft.show(getSupportFragmentManager().findFragmentById(R.id.searchContext));
			        ft.commit();
				} else {
			        ft.hide(getSupportFragmentManager().findFragmentById(R.id.searchContext));
			        ft.commit();
				}
				return false;
			}
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return true;
    }
    
    private void setSearchContextVisible(boolean visible) {
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	Fragment searchContext = getSupportFragmentManager().findFragmentById(R.id.searchContext);
    	if (visible)
    		ft.show(searchContext);
    	else
    		ft.hide(searchContext);
    	ft.commit();
    }
    
    private void setTopTracksVisible(boolean visible) {
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	Fragment topTracks = getSupportFragmentManager().findFragmentById(R.id.topTracks);
    	if (visible)
    		ft.show(topTracks);
    	else
    		ft.hide(topTracks);
    	ft.commit();
    }
    
    @Override
    public void startActivity(Intent intent) {      
        // check if search intent
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	boolean shouldSearchForSong   = ((CheckBox)findViewById(R.id.songCheckBox)).isChecked();
        	boolean shouldSearchForAlbum  = ((CheckBox)findViewById(R.id.albumCheckBox)).isChecked();
        	boolean shouldSearchForArtist = ((CheckBox)findViewById(R.id.artistCheckBox)).isChecked();
            intent.putExtra("Song",   shouldSearchForSong);
            intent.putExtra("Album",  shouldSearchForAlbum);
            intent.putExtra("Artist", shouldSearchForArtist);
        }

        super.startActivity(intent);
    }
    
    public static Context getAppContext() {
    	return MainActivity.context;
    }
    
    public void onUpButtonClicked(View view) {
    	ImageButton upButton = (ImageButton)view;
    	if (upButton.getId() != R.id.approval_up_button)
    		return;
    	if (currentTrack != null) {
    		currentTrack.setLiked(Track.LIKED);
    		highlightApprovalButton(upButton);
    		sendApprovalRequest(true);
    	}
    }
    public void onDownButtonClicked(View view) {
    	ImageButton downButton = (ImageButton)view;
    	if (downButton.getId() != R.id.approval_down_button)
    		return;
    	if (currentTrack != null) {
    		currentTrack.setLiked(Track.DISLIKED);
    		highlightApprovalButton(downButton);
    		sendApprovalRequest(false);
    	}
    }
    
    private void highlightApprovalButton(ImageButton button) {
    	switch(button.getId()) {
    	case R.id.approval_up_button:
    		button.setImageResource(R.drawable.thumbup_over);
    		((ImageButton)findViewById(R.id.approval_down_button)).setImageResource(R.drawable.thumbdown);
    		break;
    	case R.id.approval_down_button:
    		button.setImageResource(R.drawable.thumbdown_over);
    		((ImageButton)findViewById(R.id.approval_up_button)).setImageResource(R.drawable.thumbup);
    		break;
    	}
    }
    
    @Background
    void sendApprovalRequest(boolean trackLiked) {
    	BasicNameValuePair[] params = new BasicNameValuePair[3];
    	params[0] = new BasicNameValuePair("from", "rdio");
    	params[1] = new BasicNameValuePair("id", currentTrack.getKey());
    	params[2] = new BasicNameValuePair("verdict", trackLiked? "like" : "dislike");
        String url = GeaServerHandler.getURLStringForParams(baseURL + GeaServerHandler.BASE_RATE_QUERY, params);
        GeaServerHandler.sendRequest(url, GeaServerHandler.RequestMethod.POST);
    }
    
    /**
     * Queries GEA server for top num songs.
     * @param num number of top songs to pull from GEA server.
     */
    @Background
    void getApprovalRequest(int num){
    	JSONObject json;
	    BasicNameValuePair param = new BasicNameValuePair("limit", String.valueOf(num));
	    BasicNameValuePair[] params = {param};
	    String url = GeaServerHandler.getURLStringForParams(baseURL + GeaServerHandler.BASE_RATE_QUERY, params);
        json = GeaServerHandler.getJSONForRequest(url, GeaServerHandler.RequestMethod.GET);
        if (json == null) {
        	Log.e(LOG_TAG, "Error retrieving JSON from Gea Server");
        	return;
        }
        populateMap(json);
    	
    }
   
   @UiThread
   void populateMap(JSONObject json) {
	  if (markers == null)
		  markers = new HashMap<String, BasicTrack[]>();
	  Iterator<?> stateKeys = json.keys(); 
	  while (stateKeys.hasNext()) {
		  String stateCoord = (String)stateKeys.next();
		  Log.d(LOG_TAG, "state: " + stateCoord);
		  try {
			  JSONArray tracks = json.getJSONArray(stateCoord);
			  BasicTrack[] topForThisState = new BasicTrack[tracks.length()];
			  for (int i=0; i<tracks.length(); i++) {
				  JSONObject track = tracks.getJSONObject(i);
				  topForThisState[i] = new BasicTrack(track.getString("rdioId"), "track", 
						  							  track.getString("title"), track.getString("artist"), 
						  							  track.getString("album"), track.getString("image"));
			  }
			  LatLng coord = GeaServerHandler.getLatLngForCoordinateString(stateCoord);
			  Marker marker = gmap.addMarker(new MarkerOptions().position(coord).title(stateCoord));
			  markers.put(stateCoord, topForThisState);
		  } catch(JSONException e) {
			  
		  }
	  }
		  
   }
   
   public void onPrevButtonClicked(View view) {
	   if (view.getId() != R.id.prevButton)
		   return;
	   ((ImageButton)view).setImageResource(R.drawable.prev_over);
	   new Handler().postDelayed(new Runnable() {
		@Override
		public void run() {
			((ImageButton)findViewById(R.id.prevButton)).setImageResource(R.drawable.prev);
		}
	   }, 100);
	   if (music.getPlayerPosition() < 10000)
		   music.playPreviousTrack();
	   else
		   music.seekPlayerTo(0);
   }
   public void onNextButtonClicked(View view) {
	   if (view.getId() != R.id.nextButton)
		   return;
	   ((ImageButton)view).setImageResource(R.drawable.next_over);
	   new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				((ImageButton)findViewById(R.id.nextButton)).setImageResource(R.drawable.next);
			}
		   }, 100);
	   music.playNextTrack();
   }
    
    public void togglePaused(View view) {
    	if (view.getId() == R.id.play_pause_button)
    		music.togglePlayerPaused();
    	ImageButton button = (ImageButton)findViewById(R.id.play_pause_button);
    	if (music.playerIsPlaying()) {
    		trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
    		
    		button.setImageResource(R.drawable.pause);
    	}
    	else {
    		trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
    		button.setImageResource(R.drawable.play);
    	}
    }
    
    private Handler trackPositionUpdateHandler = new Handler();
    
    //If track is not null, a new track has been sent to player and should start playing
    public void onTrackChanged(Track track) {
    	if (track==null) {
    		((ImageButton)findViewById(R.id.play_pause_button)).setImageResource(R.drawable.play);
        	trackPositionUpdateHandler.removeCallbacks(updateTrackPositionTask);
    	} else {
    		Log.i(LOG_TAG, "Now playing " + track.toString());
    		currentTrack = track;
    		downloadAlbumArt(currentTrack.getAlbumArtURL());
	    	String trackInfo = String.format(currentTrack.toString());
	    	((TextView)findViewById(R.id.songInfoText)).setText(trackInfo);
	    	((ImageButton)findViewById(R.id.play_pause_button)).setImageResource(R.drawable.pause);
	    	
	    	trackPositionUpdateHandler.postDelayed(updateTrackPositionTask, 0);
    	}
    }
    
    private Runnable updateTrackPositionTask = new Runnable() {
	    public void run() {
	    	int durationSeconds = music.getPlayerDuration()/1000;
	    	String durationString = PlayerTimeFormatter.getFormattedTimeFromSeconds(durationSeconds);
	    	((TextView)findViewById(R.id.durationText)).setText(durationString);
	    	
			int currentPositionSeconds = music.getPlayerPosition()/1000;
	    	String currentPositionString = PlayerTimeFormatter.getFormattedTimeFromSeconds(currentPositionSeconds);
			((TextView)findViewById(R.id.currentPositionText)).setText(currentPositionString);
			trackPositionUpdateHandler.postDelayed(this, 100);
			
			int progress = PlayerTimeFormatter.getProgressPercent(currentPositionSeconds, durationSeconds);
			((SeekBar)findViewById(R.id.progressSeekBar)).setProgress(progress);
	    }
    };
    
    @Background
    void downloadAlbumArt(String url) {
		try {
            InputStream in = new java.net.URL(url).openStream();
            Bitmap bmp = BitmapFactory.decodeStream(in);
            setAlbumArt(bmp);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
	}
    
    @ViewById(R.id.playerAlbumArt)
    ImageView albumArtView;
    
    @UiThread
    void setAlbumArt(Bitmap bmp) {
    	albumArtView.setImageBitmap(bmp);
    }
    
    public boolean onOptionsItemSelected (MenuItem item) {
		return true;
	}
}