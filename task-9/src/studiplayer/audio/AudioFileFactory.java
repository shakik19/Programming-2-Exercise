package studiplayer.audio;

public class AudioFileFactory {
	public AudioFileFactory() {
	}
	
	public static AudioFile createAudioFile(String path) throws NotPlayableException {
		/*SampledFile.checkReadability(path);*/
		String fileExtension = path.substring(path.lastIndexOf(".") + 1).trim().toLowerCase();
		if (fileExtension.equals("wav")) {
			return new WavFile(path);
		} else if (fileExtension.equals("mp3") || fileExtension.equals("ogg")) {
			return new TaggedFile(path);
		} else {
			throw new NotPlayableException(path, String.format("Unknown suffix for AudioFile \"%s\"", path));
		}
	}
}
