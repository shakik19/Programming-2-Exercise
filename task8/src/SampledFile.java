import studiplayer.basic.BasicPlayer;

import java.io.File;

public abstract class SampledFile extends AudioFile {
	private long duration;
	
	public SampledFile() {
	}
	
	public SampledFile(String path) {
		super(path);
	}
	
	@Override
	public void play() {
		BasicPlayer.play(super.getPathname());
	}
	
	@Override
	public void togglePause() {
		BasicPlayer.togglePause();
	}
	
	@Override
	public void stop() {
		BasicPlayer.stop();
	}
	
	@Override
	public String formatDuration() {
		return timeFormatter(getDuration());
	}
	
	@Override
	public String formatPosition() {
		return timeFormatter(BasicPlayer.getPosition());
	}
	
	public static String timeFormatter(long timeInMicroSeconds) throws RuntimeException {
		if (timeInMicroSeconds < 0) {
			throw new RuntimeException(String.format("%d is not a valid time", timeInMicroSeconds));
		} else if (timeInMicroSeconds >= 6000000000L) {
			throw new RuntimeException(String.format("%d Time value overflows format", timeInMicroSeconds));
		}
		long milliseconds = timeInMicroSeconds / 1000;
		long seconds = milliseconds / 1000;
		long minutes = seconds / 60;
		seconds = seconds % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}
	
	public static void checkReadability(String filepath) {
		File file = new File(filepath);
		if (!file.canRead()) {
			throw new RuntimeException("File not readable");
		}
	}
	
	protected long getDuration() {
		return duration;
	}
	
	protected void setDuration(long duration) {
		this.duration = duration;
	}
}
