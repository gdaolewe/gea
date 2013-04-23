package net.kenpowers.gea;

public class Track extends MusicServiceObject implements Comparable<Track> {
	private String artist, album, albumArtURL;
	private int duration, trackNum;
	public Track(String key, String type, String name, String artist, 
			String album, String albumArtURL, int duration, int trackNum) {
		super (key, type, name);
		this.artist = artist;
		this.album = album;
		this.albumArtURL = albumArtURL;
		this.duration = duration;
		this.trackNum = trackNum;
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
	public int compareTo(Track o) {
		if (trackNum == o.trackNum)
			return 0;
		else if (trackNum > o.trackNum)
			return 1;
		else
			return -1;
	}
	public String toString() {
		return artist + " - " + super.toString() + " (" + album + ")";
	}
}
