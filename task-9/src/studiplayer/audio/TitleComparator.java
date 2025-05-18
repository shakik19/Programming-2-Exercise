package studiplayer.audio;

import java.util.Comparator;

public class TitleComparator implements Comparator<AudioFile> {
	@Override
	public int compare(AudioFile o1, AudioFile o2) {
		if (o1.getTitle() == null || o2.getTitle() == null) {
			throw new RuntimeException("AudioFile provided contains 'null' Title value, comparison not possible");
		}
		return o1.getTitle().compareTo(o2.getTitle());
	}
}
