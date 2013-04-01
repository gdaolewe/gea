package net.kenpowers.gea;

public interface SearchCompletePublisher {
	
	public void registerSearchCompleteListener(SearchCompleteListener listener);
	
	public void notifySearchCompleteListeners(MusicServiceObject[] results);
}
