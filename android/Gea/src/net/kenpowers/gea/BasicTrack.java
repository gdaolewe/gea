package net.kenpowers.gea;

public class BasicTrack extends MusicServiceObject {
	private String artist, album, albumArtURL;
	public BasicTrack(String key, String type, String name, String artist, String album, String albumArtURL) {
		super(key, type, name);
		this.artist = artist;
		this.album = album;
		this.albumArtURL = albumArtURL;
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
	public String toString() {
		return artist + " - " + super.toString() + " (" + album + ")";
	}
}
