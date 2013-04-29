package net.kenpowers.gea;

public class Track extends MusicServiceObject implements Comparable<Track> {
	public static final int NOT_RATED = 0;
	public static final int DISLIKED = -1;
	public static final int LIKED = 1;
	private String artist, artistKey, album, albumKey, albumArtURL;
	private int duration, trackNum;
	private int liked = Track.NOT_RATED;
	public Track(String key, String type, String name, String artist, 
				 String artistKey, String album, String albumKey, 
				 String albumArtURL, int duration, int trackNum) {
		super (key, type, name);
		this.artist = artist;
		this.artistKey = artistKey;
		this.album = album;
		this.albumKey = albumKey;
		this.albumArtURL = albumArtURL;
		this.duration = duration;
		this.trackNum = trackNum;
	}

	public String getArtist() {
		return artist;
	}
	public String getArtistKey() {
		return artistKey;
	}
	public String getAlbum() {
		return album;
	}
	public String getAlbumKey() {
		return albumKey;
	}
	public String getAlbumArtURL() {
		return albumArtURL;
	}
	public int getDuration() {
		return duration;
	}
	public int getNum() {
		return trackNum;
	}
	public int isLiked() {
		return liked;
	}
	public void setLiked(int liked) {
		this.liked = liked;
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
