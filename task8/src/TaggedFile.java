import studiplayer.basic.TagReader;

import java.io.File;
import java.util.Map;

public class TaggedFile extends SampledFile {
	private String album = "";
	
	public TaggedFile() {
	}
	
	public TaggedFile(String path) {
		super(path);
		checkReadability(getPathname());
		readAndStoreTags();
	}
	
	public String getAlbum() {
		return album;
	}
	
	private void readAndStoreTags() {
		Map<String, Object> tagMap = TagReader.readTags(getPathname());
		super.setDuration((Long) tagMap.get("duration"));
		String albumTag = ((String) tagMap.get("album"));
		album = (albumTag == null) ? "" : albumTag.trim();
		
		String author = ((String) tagMap.get("author"));
		super.author = (author != null) ? author.trim() : super.author;
		
		String title = ((String) tagMap.get("title"));
		super.title = (title != null) ? title.trim() : super.title;
	}
	
	@Override
	public String toString() {
		if (getAlbum().isEmpty()) {
			return (super.toString() + " - " + super.formatDuration());
		} else {
			return (super.toString() + " - " + getAlbum() + " - " + super.formatDuration());
		}
	}
	
}
