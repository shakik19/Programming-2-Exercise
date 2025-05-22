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
	
	private void checkExtension() throws NotPlayableException {
		int index = getFilename().lastIndexOf(".");
		String extension = getFilename().substring(index + 1);
		if (!extension.equalsIgnoreCase("wav")) {
			throw new NotPlayableException(getPathname(), "Not a wav file");
		}
	}
	
	public void readAndSetDurationFromFile() throws NotPlayableException {
		try {
			WavParamReader.readParams(getPathname());
		} catch (Exception e){
			throw new NotPlayableException(getPathname(), "Params are not readable", e);
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
