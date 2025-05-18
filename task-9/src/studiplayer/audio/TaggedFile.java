package studiplayer.audio;

import studiplayer.basic.TagReader;
import java.util.Map;

public class TaggedFile extends SampledFile {
	private String album = "";
	
	public TaggedFile() {
	}
	
	public TaggedFile(String path) throws NotPlayableException {
		super(path);
		readAndStoreTags();
	}
	
	public String getAlbum() {
		return album;
	}
	
	public void readAndStoreTags() throws NotPlayableException {
		Map<String, Object> tagMap = null;
		try {
			tagMap = TagReader.readTags(getPathname());
		} catch (Exception e){
			throw new NotPlayableException(getPathname(),
							String.format("Tags are not readable\n Cause: %s", e.getMessage()), e);
		}
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
