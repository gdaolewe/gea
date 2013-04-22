package net.kenpowers.gea;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class LaunchActivity extends Activity implements MusicServiceReadyListener {
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

	@Override
	public void onMusicServiceReady() {
		
		intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onStop()
	{
	     //unregisterReceiver();
		//intent.getB
	    super.onStop();
	}

}
