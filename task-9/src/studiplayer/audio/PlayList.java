package studiplayer.audio;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayList {
	private ControllablePlayListIterator itr;
	private String search;
	private SortCriterion sortCriterion = SortCriterion.DEFAULT;
	private List<AudioFile> playList = new LinkedList<>();
	
	public PlayList() {
		this.itr = new ControllablePlayListIterator(playList);
	}
	
	public PlayList(String m3uPathname) {
		try {
			loadFromM3U(m3uPathname);
			itr = new ControllablePlayListIterator(playList);
		} catch (NotPlayableException e) {
			throw new RuntimeException("Failed to load playlist from: " + m3uPathname, e);
		}
	}
	
	public void add(AudioFile audioFile) {
		playList.add(audioFile);
		itr = new ControllablePlayListIterator(playList);
	}
	
	public void remove(AudioFile audioFile) {
		playList.remove(audioFile);
		itr = new ControllablePlayListIterator(playList);
	}
	
	public int size() {
		return playList.size();
	}
	
	public AudioFile currentAudioFile() {
		if (itr.getList().isEmpty()) {
			return null;
		} else if (itr.getItrPosition() >= itr.getList().size()) {
			itr = new ControllablePlayListIterator(new ArrayList<>(playList), search, sortCriterion);
		}
		return itr.getList().get(itr.getItrPosition());
	}
	
	public void nextSong() {
		itr.setItrPosition(itr.getItrPosition() + 1);
	}
	
	public void loadFromM3U(String m3uPathname) throws NotPlayableException {
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
							.flatMap(path -> {
								try {
									return Stream.of(AudioFileFactory.createAudioFile(path));
								} catch (NotPlayableException ignored) {
									return Stream.empty();
								}
							})
							.collect(Collectors.toCollection(LinkedList::new));
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
	
	public String getSearch() {
		return search;
	}
	
	public void setSearch(String sc) {
		this.search = sc;
		itr = new ControllablePlayListIterator(playList, search, sortCriterion);
	}
	
	public SortCriterion getSortCriterion() {
		return sortCriterion;
	}
	
	public void setSortCriterion(SortCriterion sc) {
		this.sortCriterion = sc;
		System.out.println("Sort: " + itr.hashCode());
		itr = new ControllablePlayListIterator(new ArrayList<>(playList), search, sc);
		System.out.println("~Sort: " + itr.hashCode());
	}
	
	public void jumpToAudioFile(AudioFile af) {
		itr.setItrPosition(itr.getList().indexOf(af));
	}
	
	public Iterator<AudioFile> iterator() {
		return new ControllablePlayListIterator(new ArrayList<>(playList), search, sortCriterion);
	}
	
	@Override
	public String toString() {
		return playList.toString();
	}
}