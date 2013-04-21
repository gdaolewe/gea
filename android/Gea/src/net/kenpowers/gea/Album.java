package net.kenpowers.gea;

public class Album extends MusicServiceObject {
	private String artist, artistKey, albumArtURL;
	private String[] trackKeys;
	public Album(String key, String type, String name, String artist, String artistKey, String albumArtURL, String[] trackKeys) {
		super(key, type, name);
		this.artist = artist;
		this.artistKey = artistKey;
		this.albumArtURL = albumArtURL;
		this.trackKeys = trackKeys;
	}
	
	public String getArtist() {
		return artist;
	}
	public String getArtistKey() {
		return artistKey;
	}
	public String getAlbumArtURL() {
		return albumArtURL;
	}
	public String[] getTrackKeys() {
		return trackKeys;
	}
	public String toString() {
		return artist + " - " + super.toString();
	}
}
