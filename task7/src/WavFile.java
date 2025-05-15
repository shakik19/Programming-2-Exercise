import studiplayer.basic.WavParamReader;

import java.io.File;

public class WavFile extends SampledFile{
	public WavFile() {
	}
	
	public WavFile(String path) {
		super(path);
		checkReadability(path);
		checkExtension();
		readAndSetDurationFromFile();
	}
	
	private void checkExtension(){
		int index = getFilename().indexOf(".");
		String extension = getFilename().substring(index + 1);
		if (!extension.equalsIgnoreCase("wav")){
			throw new RuntimeException(String.format("%s is not an wav file", getFilename()));
		}
	}

	private void readAndSetDurationFromFile(){
		WavParamReader.readParams(getPathname());
		long numberOfFrames = WavParamReader.getNumberOfFrames();
		float frameRate = WavParamReader.getFrameRate();
		super.setDuration(computeDuration(numberOfFrames, frameRate));
	}
	
	public static long computeDuration(long numberOfFrames, float frameRate){
		// In microseconds
		return (long) ((numberOfFrames / frameRate) * 1000000);
	}
	
	@Override
	public String toString() {
		return String.format("%s - %s", super.toString(), super.formatDuration());
	}
}
