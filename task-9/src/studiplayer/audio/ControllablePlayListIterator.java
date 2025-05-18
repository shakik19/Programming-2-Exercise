package studiplayer.audio;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ControllablePlayListIterator implements Iterator {
	private final List<AudioFile> list;
	private int itrPosition = 0;
	
	public ControllablePlayListIterator(List<AudioFile> list) {
		this.list = list;
	}
	
	public ControllablePlayListIterator(List<AudioFile> audioFiles, String search, SortCriterion sort) {
		this.list = filterBySearch(audioFiles, search);
		
		if (sort != SortCriterion.DEFAULT) {
			switch (sort) {
				case ALBUM -> this.list.sort(new AlbumComparator());
				case AUTHOR -> this.list.sort(new AuthorComparator());
				case DURATION -> this.list.sort(new DurationComparator());
				case TITLE -> this.list.sort(new TitleComparator());
			}
		}
	}
	
	private List<AudioFile> filterBySearch(List<AudioFile> audioFiles, String search) {
		if (search == null || search.trim().isEmpty()){
			return audioFiles;
		} else {
			return audioFiles.stream()
							.filter(Objects::nonNull)
							.filter(file -> matchSearch(file, search.toLowerCase()))
							.collect(Collectors.toList());
		}
	}
	
	private boolean matchSearch(AudioFile audioFile, String search) {
		return ((audioFile.getFilename() != null && audioFile.getFilename().toLowerCase().contains(search))
						|| (audioFile.getTitle() != null && audioFile.getTitle().toLowerCase().contains(search))
						|| (audioFile.getAuthor() != null && audioFile.getAuthor().toLowerCase().contains(search))
						|| (audioFile instanceof TaggedFile)) && ((TaggedFile) audioFile).getAlbum() != null && ((TaggedFile) audioFile).getAlbum().toLowerCase().contains(search);
	}
	
	@Override
	public boolean hasNext() {
		return itrPosition < list.size();
	}
	
	@Override
	public AudioFile next() {
		if (itrPosition >= list.size()) {
			throw new RuntimeException("Iteration already completed");
		}
		return list.get(itrPosition++);
	}
	
	public AudioFile jumpToAudioFile(AudioFile audioFile) {
		int targetItrPos = list.indexOf(audioFile);
		if (targetItrPos != -1) {
			itrPosition = targetItrPos + 1;
			return list.get(targetItrPos);
		} else {
			return null;
		}
	}
}
