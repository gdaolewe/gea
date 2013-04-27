package net.kenpowers.gea;

public class PlayerTimeFormatter {
	
	private PlayerTimeFormatter() {}

	public static int getProgressPercent(int position, int duration) {
		double progress = ((double)position/duration)*100;
		return (int)progress;
	}

	public static int getSecondsFromProgress(int progress, int duration) {
		double seconds = (double)progress*duration / 100;
		return (int)seconds;
	}

	public static String getFormattedTimeFromSeconds(int seconds) {
		int secs = seconds % 60;
		int minutes = seconds / 60;
		return "" + (minutes<10 ? "0":"") + minutes + ":" + (secs<10? "0":"") + secs;
	}

}
