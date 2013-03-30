package net.kenpowers.gea;

public interface TrackChangedPublisher {
	
	public void registerTrackChangedListener(TrackChangedListener listener);
	
	public void notifyTrackChangedListeners(Track track);
}
