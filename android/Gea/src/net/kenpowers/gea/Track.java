package net.kenpowers.gea;

public class Track extends MusicServiceObject {
	private String artist, album, albumArtURL;
	private int duration;
	public Track(String key, String type, String name, String artist, 
			String album, String albumArtURL, int duration) {
		super (key, type, name);
		this.artist = artist;
		this.album = album;
		this.albumArtURL = albumArtURL;
		this.duration = duration;
	}

	public String getArtist() {
		return artist;
	}
	public String getAlbum() {
		return album;
	}
	public String getAlbumArtURL() {
		return albumArtURL;
	}
	public int getDuration() {
		return duration;
	}
	
	public String toString() {
		return artist + " - " + super.toString() + " (" + album + ")";
	}
}
