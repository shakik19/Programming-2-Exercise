package studiplayer.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import studiplayer.audio.*;

import javax.swing.text.html.HTML;

public class TestSubtaskD {
    private void debug(String msg, List<AudioFile> list) {
        System.out.println("----");
        System.out.println(msg + ":");
        for (AudioFile file : list) {
            System.out.print(" -> ");
            System.out.println(file);
        }
    }

    @Test
    public void testDurationComparator() throws Exception {
        TaggedFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        TaggedFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        TaggedFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");
        List<AudioFile> list = Arrays.asList(
                tf1,
                tf2,
                tf3);
        
        list.sort(new DurationComparator());
        debug("List after using DurationComparator(false)", list);

        assertEquals("First file should be Motiv 5. Symphonie von Beethoven.ogg", tf2, list.get(0));
        assertEquals("Second file should be Eisbach Deep Snow.ogg", tf3, list.get(1));
        assertEquals("Third file should be Rock 812.mp3", tf1, list.get(2));
    }

    @Test
    public void testDurationComparatorWithMidi() throws Exception {
        TaggedFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        TaggedFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        TaggedFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");
        AudioFile tf4 = new AudioFile() {
            public void play() throws NotPlayableException {
            }

            public void togglePause() {
            }

            public void stop() {
            }

            public String formatDuration() {
                return "";
            }

            public String formatPosition() {
                return "";
            }
        };
        List<AudioFile> list = Arrays.asList(
                tf1,
                tf2,
                tf3,
                tf4);
        list.sort(new DurationComparator());

        debug("List after using DurationComparator(false)", list);

        assertEquals("First file should be file with empty data", tf4, list.get(0));
        assertEquals("Second file should be Motiv 5. Symphonie von Beethoven.ogg", tf2, list.get(1));
        assertEquals("Third file should be Eisbach Deep Snow.ogg", tf3, list.get(2));
        assertEquals("Forth file should be Rock 812.mp3", tf1, list.get(3));
    }

    @Test
    public void testAuthorComparator() throws Exception {
        TaggedFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        TaggedFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        List<AudioFile> list = Arrays.asList(
                tf1,
                tf2);
        list.sort(new AuthorComparator());

        debug("List after using AuthorComparator(false)", list);

        assertEquals("First file should be Motiv 5. Symphonie von Beethoven.ogg", tf2, list.get(0));
        assertEquals("Second file should be Rock 812.mp3", tf1, list.get(1));
    }

    @Test
    public void testTitleComparator() throws Exception {
        TaggedFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        TaggedFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        TaggedFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");
        List<AudioFile> list = Arrays.asList(
                tf1,
                tf2,
                tf3);
        list.sort(new TitleComparator());

        debug("List after using TitleComparator(false)", list);

        assertEquals("First file should be Eisbach Deep Snow.ogg", tf3, list.get(0));
        assertEquals("Second file should be Motiv 5. Symphonie von Beethoven.ogg", tf2, list.get(1));
        assertEquals("Third file should be Rock 812.mp3", tf1, list.get(2));
    }

    @Test
    public void testAlbumComparator() throws Exception {
        TaggedFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        TaggedFile tf2 = new TaggedFile("audiofiles/wellenmeister_awakening.ogg");
        WavFile tf3 = new WavFile("audiofiles/wellenmeister - tranquility.wav");
        System.out.println(tf2.getAlbum());
        List<AudioFile> list = Arrays.asList(
                tf1,
                tf2,
                tf3);

        list.sort(new AlbumComparator());

        debug("List after using AlbumComparator(false)", list);

        assertEquals("First file should be wellenmeister - tranquility.wav", tf3, list.get(0));
        assertEquals("Second file should be Rock 812.mp3", tf1, list.get(1));
        assertEquals("Third file should be wellenmeister_awakening.mp3", tf2, list.get(2));
    }
}
