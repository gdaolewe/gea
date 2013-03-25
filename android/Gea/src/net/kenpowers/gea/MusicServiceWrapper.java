package net.kenpowers.gea;
import android.media.MediaPlayer;
import android.content.Intent;

import com.rdio.android.api.Rdio;
import com.rdio.android.api.RdioApiCallback;
import com.rdio.android.api.RdioListener;
import com.rdio.android.api.services.RdioAuthorisationException;
import com.rdio.android.api.RdioSubscriptionType;


public class MusicServiceWrapper implements RdioListener {
	private final String rdioKey = "9d3ayynanambvwxq5wpnw82y";
	private final String rdioSecret = "CdNPjjcrPW";
	
	private Rdio rdio = new Rdio(rdioKey, rdioSecret, null, null, null, this);
	
	public static final MusicServiceWrapper INSTANCE = new MusicServiceWrapper();
	
	private MusicServiceWrapper () {}
	
	public MediaPlayer getPlayerForTrack(String trackKey, String sourceKey) {
		return null;
	}
	
	public void onRdioAuthorised(String accessToken, String accessTokenSecret) {
		
	}
	
	public void onRdioReady() {
		
	}
	
	public void onRdioUserAppApprovalNeeded(Intent authorisationIntent) {
		
	}
	
	public void onRdioUserPlayingElsewhere() {
		
	}
	
}
