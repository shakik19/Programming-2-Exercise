package studiplayer.audio;

import java.util.Comparator;

public class DurationComparator implements Comparator<AudioFile> {
	@Override
	public int compare(AudioFile o1, AudioFile o2) {
		if (o1 == null) {
			throw new RuntimeException("First AudioFile cannot be null");
		}
		if (o2 == null) {
			throw new RuntimeException("Second AudioFile cannot be null");
		}
		
		if (o1 == o2) return 0;
		
	/*
	Long d1 = (o1 instanceof SampledFile sf) ? sf.getDuration() : null;
	Long d2 = (o2 instanceof SampledFile sf) ? sf.getDuration() : null;
	*/
		Long d1 = null;
		if (o1 instanceof SampledFile) {
			d1 = ((SampledFile) o1).getDuration();
		}
		
		Long d2 = null;
		if (o2 instanceof SampledFile) {
			d2 = ((SampledFile) o2).getDuration();
		}
		
		if (d1 == null ^ d2 == null) {
			return d1 == null ? -1 : 1;
		}
		if (d1 == null) {
			return 0;
		}
		
		return Long.compare(d1, d2);
	}
}
