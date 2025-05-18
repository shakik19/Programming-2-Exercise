package studiplayer.audio;

import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile {
	public WavFile() {
	}
	
	public WavFile(String path) throws NotPlayableException {
		super(path);
		checkExtension();
		readAndSetDurationFromFile();
	}
	
	private void checkExtension() {
		int index = getFilename().lastIndexOf(".");
		String extension = getFilename().substring(index + 1);
		if (!extension.equalsIgnoreCase("wav")) {
			throw new RuntimeException(String.format("%s is not an wav file", getFilename()));
		}
	}
	
	public void readAndSetDurationFromFile() throws NotPlayableException {
		try {
			WavParamReader.readParams(getPathname());
		} catch (Exception e){
			throw new NotPlayableException(getPathname(),
							String.format("Params are not readable\n Cause: %s", e.getMessage()), e);
		}
		long numberOfFrames = WavParamReader.getNumberOfFrames();
		float frameRate = WavParamReader.getFrameRate();
		super.setDuration(computeDuration(numberOfFrames, frameRate));
	}
	
	public static long computeDuration(long numberOfFrames, float frameRate) {
		// In microseconds
		return (long) ((numberOfFrames / frameRate) * 1000000);
	}
	
	@Override
	public String toString() {
		return String.format("%s - %s", super.toString(), super.formatDuration());
	}
}
