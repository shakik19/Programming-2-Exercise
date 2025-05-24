package studiplayer.audio;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ControllablePlayListIterator implements Iterator<AudioFile> {
	private final List<AudioFile> list;
	private int itrPosition = 0;
	
	public ControllablePlayListIterator(List<AudioFile> list) {
		this.list = list;
	}
	
	public ControllablePlayListIterator(List<AudioFile> audioFiles, String search, SortCriterion sort) {
		this.list = filterBySearch(audioFiles, search);
		
		if (sort != SortCriterion.DEFAULT) {
			switch (sort) {
				case ALBUM:
					this.list.sort(new AlbumComparator());
					break;
				case AUTHOR:
					this.list.sort(new AuthorComparator());
					break;
				case DURATION:
					this.list.sort(new DurationComparator());
					break;
				case TITLE:
					this.list.sort(new TitleComparator());
					break;
			}
		}
	}
	
	private List<AudioFile> filterBySearch(List<AudioFile> audioFiles, String search) {
		if (search == null || search.trim().isEmpty()) {
			return audioFiles;
		} else {
			return audioFiles.stream()
							.filter(Objects::nonNull)
							.filter(file -> matchSearch(file, search.toLowerCase()))
							.collect(Collectors.toList());
		}
	}
	
	private boolean matchSearch(AudioFile audioFile, String search) {
		boolean matchesBasic = (audioFile.getFilename() != null && audioFile.getFilename().toLowerCase().contains(search))
						|| (audioFile.getTitle() != null && audioFile.getTitle().toLowerCase().contains(search))
						|| (audioFile.getAuthor() != null && audioFile.getAuthor().toLowerCase().contains(search));
		
		boolean matchesAlbum = false;
/*		if (audioFile instanceof TaggedFile taggedFile) {
			matchesAlbum = taggedFile.getAlbum() != null && taggedFile.getAlbum().toLowerCase().contains(search);
		}*/
		if (audioFile instanceof TaggedFile) {
			TaggedFile taggedFile = (TaggedFile) audioFile;
			matchesAlbum = taggedFile.getAlbum() != null && taggedFile.getAlbum().toLowerCase().contains(search);
		}
		return matchesBasic || matchesAlbum;
	}
	
	
	@Override
	public boolean hasNext() {
		return itrPosition < list.size();
	}
	
	@Override
	public AudioFile next() {
		if (list.isEmpty()) {
			throw new RuntimeException("There is no Audiofile in the playlist");
		} else if (itrPosition >= list.size()) {
			itrPosition = 0;
			return list.get(itrPosition++);
		} else {
			return list.get(itrPosition++);
		}
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
	
	public int getItrPosition() {
		return itrPosition;
	}
	
	public void setItrPosition(int newPos) {
		this.itrPosition = newPos;
	}
	
	public List<AudioFile> getList() {
		return list;
	}
	
	@Override
	public String toString() {
		return list.toString();
	}
}
