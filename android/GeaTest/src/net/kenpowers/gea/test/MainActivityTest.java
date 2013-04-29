//
//package net.kenpowers.gea.test;
//
//import java.util.Random;
//
//import android.test.ActivityUnitTestCase;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import net.kenpowers.gea.*;
//
//public class MainActivityTest extends ActivityUnitTestCase<MainActivity>{
//	private MainActivity activity;
//	
//	private Button play_pauseButton;
//	private Button upButton;
//	private Button downButton;
//	private Button prevButton;
//	private Button nextButton;
//	private SeekBar progressSeekBar;
//	private SeekBar volumeSeekBar;
//	private TextView currentPositionText;
//	private TextView durationText;
//	private TextView separator;
//	private ImageView albumArtImage;
//	
//	public MainActivityTest(){
//		super(MainActivity.class);
//	}
//	
//	@Override
//	protected void setUp() throws Exception{
//		super.setUp();
//		activity = getActivity();
//		
//		//null pointer exception
//		//play_pauseButton = (Button) (activity.findViewById(net.kenpowers.gea.R.id.play_pause_button));
//		//upButton = (Button) activity.findViewById(net.kenpowers.gea.R.id.approval_up_button);
//		//downButton = (Button) activity.findViewById(net.kenpowers.gea.R.id.approval_down_button);
//		//prevButton = (Button) activity.findViewById(net.kenpowers.gea.R.id.prevButton);
//		//nextButton = (Button) activity.findViewById(net.kenpowers.gea.R.id.nextButton);
//		//progressSeekBar = (SeekBar) activity.findViewById(net.kenpowers.gea.R.id.progressSeekBar);
//		//volumeSeekBar = (SeekBar) activity.findViewById(net.kenpowers.gea.R.id.volumeSeekBar);
//		//currentPositionText = (TextView) activity.findViewById(net.kenpowers.gea.R.id.currentPositionText);
//		//durationText = (TextView) activity.findViewById(net.kenpowers.gea.R.id.durationText);
//		//separator = (TextView) activity.findViewById(net.kenpowers.gea.R.id.position_durationTextSeparator);
//		//albumArtImage = (ImageView) activity.findViewById(net.kenpowers.gea.R.id.playerAlbumArt);
//	}
//	
//	@Override
//	protected void tearDown() throws Exception{
//		super.tearDown();
//	}
//	
//	public void testInitialGuiState(){
//		/*assertTrue(play_pauseButton.getText().equals(net.kenpowers.gea.R.string.play_button_text));
//		assertTrue(upButton.getText().equals(net.kenpowers.gea.R.string.up_button_text));*/
//	}
//	
//}
