package net.kenpowers.gea;

public class Artist extends MusicServiceObject {
	private String imageURL;
	public Artist(String key, String type, String name, String imageURL) {
		super(key, type, name);
		this.imageURL = imageURL;
	}
	public String getImageURL() {
		return imageURL;
	}
}
