//
//package net.kenpowers.gea.test;
//
//import android.test.AndroidTestCase;
//import net.kenpowers.gea.*;
//
//public class MusicServiceWrapperTest extends AndroidTestCase{
//	MusicServiceWrapper msw;
//	
//	protected void setUp(){
//		msw = MusicServiceWrapper.getInstance(getContext());
//	}
//	
//	public void testInstantiation(){
//		msw = MusicServiceWrapper.getInstance(getContext());
//		assertTrue(msw != null);
//	}
//	
//	public void testSearchCompleteListener(){
//		Track[] track = new Track[1];
//		msw.registerSearchCompleteListener(new SearchCompleteListener(){
//			public void onSearchComplete(MusicServiceObject[] obj){
//				obj[0] = new Track(null, null, null, null, null, null, 0);
//			}
//		});
//		msw.notifySearchCompleteListeners(track);
//		assertTrue(track[0] != null);
//	}
//	
//	public void testTrackChangedListener(){
//		
//	}
//	
//}
