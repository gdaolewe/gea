package net.kenpowers.gea;

public class Album extends MusicServiceObject {
	private String artist, albumArtURL;
	public Album(String key, String type, String name, String artist, String albumArtURL) {
		super(key, type, name);
		this.artist = artist;
		this.albumArtURL = albumArtURL;
	}
	
	public String getArtist() {
		return artist;
	}
	public String getAlbumArtURL() {
		return albumArtURL;
	}
}
