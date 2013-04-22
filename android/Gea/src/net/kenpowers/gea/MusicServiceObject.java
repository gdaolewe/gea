package net.kenpowers.gea;

public abstract class MusicServiceObject {
	private String key, type, name;
	
	public MusicServiceObject(String key, String type, String name) {
		this.key = key;
		this.type = type;
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}
