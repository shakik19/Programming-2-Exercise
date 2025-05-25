package studiplayer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import studiplayer.audio.AudioFile;
import studiplayer.audio.ControllablePlayListIterator;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.TaggedFile;

public class TestSubtaskC {
    @Test
    public void testIterator() throws NotPlayableException {
        List<AudioFile> list = Arrays.asList(
                new TaggedFile("audiofiles/Rock 812.mp3"),
                new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg"),
                new TaggedFile("audiofiles/Eisbach Deep Snow.ogg"));

        ControllablePlayListIterator iterator = new ControllablePlayListIterator(list);
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Rock 812.mp3 as first iteration result", list.get(0), iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Motiv 5. Symphonie von Beethoven.ogg as first iteration result", list.get(1),
                iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        
        assertEquals("Should provide Eisbach Deep Snow.ogg as second iteration result", list.get(2), iterator.next());
        assertFalse("Should only provide two elements", iterator.hasNext());
    }

    @Test
    public void testJumpToAudioFile() throws NotPlayableException {
        List<AudioFile> list = Arrays.asList(
                new TaggedFile("audiofiles/Rock 812.mp3"),
                new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg"),
                new TaggedFile("audiofiles/Eisbach Deep Snow.ogg"));

        ControllablePlayListIterator iterator = new ControllablePlayListIterator(list);

        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Rock 812.mp3 as first iteration result", list.get(0), iterator.next());
        assertEquals("Should provide Rock 812.mp3 with jump to call", list.get(0),
                iterator.jumpToAudioFile(list.get(0)));
        
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Motiv 5. Symphonie von Beethoven.ogg as first iteration result", list.get(1),
                iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Eisbach Deep Snow.ogg as second iteration result", list.get(2), iterator.next());
        assertFalse("Should only provide two elements", iterator.hasNext());

        assertEquals("Should provide Eisbach Deep Snow.ogg as second iteration result", list.get(2),
                iterator.jumpToAudioFile(list.get(2)));
        assertFalse("Should not has next element after jumping to last one", iterator.hasNext());
    }
}
