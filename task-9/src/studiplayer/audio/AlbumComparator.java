package studiplayer.audio;

import javax.swing.text.html.HTML;
import java.util.Comparator;

public class AlbumComparator implements Comparator<AudioFile> {
	@Override
	public int compare(AudioFile o1, AudioFile o2) {
		if (o1 instanceof TaggedFile && o2 instanceof TaggedFile){
			String a1 = ((TaggedFile) o1).getAlbum();
			String a2 = ((TaggedFile) o2).getAlbum();
			if (a1 == null || a2 == null){
				throw new RuntimeException("Tagged AudioFile provided contains 'null' Album value");
			}
			return a1.compareTo(a2);
		} else if (o1 instanceof TaggedFile) {
			return 1;
		} else if (o2 instanceof TaggedFile){
			return -1;
		} else {
			return 0;
		}
	}
}
