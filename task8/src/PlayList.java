import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

public class PlayList {
	public int current;
	private LinkedList<AudioFile> playList = new LinkedList<>();
	
	public PlayList() {
		this.current = 0;
	}
	
	public PlayList(String m3uPathname) {
		loadFromM3U(m3uPathname);
	}
	
	public void add(AudioFile audioFile) {
		playList.add(audioFile);
	}
	
	public void remove(AudioFile audioFile) {
		playList.remove(audioFile);
	}
	
	public int size() {
		return playList.size();
	}
	
	public AudioFile currentAudioFile() {
		return (size() == 0) ? null : playList.get(current);
	}
	
	public void nextSong() {
		if (current > size()) {
			current = 0;
			return;
		}
		current = ++current % playList.size();
	}
	
	public void loadFromM3U(String m3uPathname) {
		TaggedFile.checkReadability(m3uPathname);
		Scanner sc = null;
		List<String> pathNames = new ArrayList<>();
		try {
			sc = new Scanner(new File(m3uPathname));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line != null && !(line.startsWith("#") || line.isBlank())) {
					pathNames.add(line);
				}
			}
			playList.clear();
			playList = pathNames.stream()
							.map(AudioFileFactory::createAudioFile)
							.collect(Collectors.toCollection(LinkedList<AudioFile>::new));
			
			setCurrent(0);
		} catch (Exception ignored) {
		} finally {
			try {
				System.out.println("File: \"" + m3uPathname + "\" read successfully");
				Objects.requireNonNull(sc).close();
			} catch (Exception ignored) {
			}
		}
	}
	
	public void saveAsM3U(String m3uPathname) {
		FileWriter fileWriter = null;
		String sysLineSep = System.lineSeparator();
		try {
			fileWriter = new FileWriter(m3uPathname);
			for (AudioFile audioFile : playList) {
				fileWriter.write(audioFile.getPathname() + sysLineSep);
			}
		} catch (Exception e) {
			System.out.println("An error occurred while opening or writing to the file: " + m3uPathname + "\nException: " + e);
		} finally {
			try {
				System.out.println("Playlist Successfully Written to: " + m3uPathname);
				assert fileWriter != null;
				fileWriter.close();
			} catch (Exception e) {
				System.out.println("An error occurred while closing the file: " + m3uPathname + "\nException: " + e);
			}
		}
	}
	
	public List<AudioFile> getList() {
		return playList;
	}
	
	public int getCurrent() {
		return current;
	}
	
	public void setCurrent(int value) {
		current = value;
	}
}