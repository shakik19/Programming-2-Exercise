package studiplayer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import studiplayer.audio.AudioFile;
import studiplayer.audio.PlayList;
import studiplayer.audio.SortCriterion;
import studiplayer.audio.TaggedFile;

public class TestSubtaskG {
    @Test
    public void testEmpty() throws Exception {
        PlayList list = new PlayList();
        assertNull(list.currentAudioFile());
    }

    @Test
    public void testEmptyNextSong() throws Exception {
        PlayList list = new PlayList();
        try {
            list.nextSong();
        } catch (Exception e) {
            fail("Should be possible to call next song on empty list");
        }
        assertNull(list.currentAudioFile());
    }

    @Test
    public void testListWithOneElement() throws Exception {
        PlayList list = new PlayList();
        TaggedFile tf1 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        list.add(tf1);
        assertEquals(tf1, list.currentAudioFile());
        list.nextSong();
        assertEquals(tf1, list.currentAudioFile());
        list.nextSong();
        assertEquals(tf1, list.currentAudioFile());
    }

    @Test
    public void testNextSong() throws Exception {
        PlayList list = new PlayList();
        TaggedFile tf1 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        TaggedFile tf2 = new TaggedFile("audiofiles/Rock 812.mp3");
        list.add(tf1);
        list.add(tf2);
        assertEquals(tf1, list.currentAudioFile());
        list.setSearch("Eisbach");
        assertEquals(tf2, list.currentAudioFile());
    }

    @Test
    public void testCallCurrentAudioFileFirst() throws Exception {
        PlayList list = new PlayList();
        TaggedFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        TaggedFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        list.add(tf1);
        list.add(tf2);
        assertEquals(tf1, list.currentAudioFile());
        list.nextSong();
        assertEquals(tf2, list.currentAudioFile());
    }

    @Test
    public void testLoop() throws Exception {
        PlayList list = new PlayList();
        TaggedFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        TaggedFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        list.add(tf1);
        list.add(tf2);
        list.nextSong();
        assertEquals(tf2, list.currentAudioFile());
        list.nextSong();
        assertEquals(tf1, list.currentAudioFile());
        list.nextSong();
        assertEquals(tf2, list.currentAudioFile());
        list.nextSong();
        assertEquals(tf1, list.currentAudioFile());
        list.nextSong();
        assertEquals(tf2, list.currentAudioFile());
    }

    @Test
    public void testSnippet01() throws Exception {
        System.out.println("---- Test Snippet 01 ----");
        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");

        PlayList pl1 = new PlayList();
        pl1.add(tf1);
        System.out.println(pl1.currentAudioFile());
        assertEquals(tf1, pl1.currentAudioFile());
        pl1.nextSong();
        System.out.println(pl1.currentAudioFile());
        assertEquals(tf1, pl1.currentAudioFile());
    }

    @Test
    public void testSnippet02() throws Exception {
        System.out.println("---- Test Snippet 02 ----");
        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        AudioFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");

        PlayList pl2 = new PlayList();
        pl2.add(tf1);
        pl2.add(tf2);
        pl2.nextSong();
        System.out.println(pl2.currentAudioFile());
        assertEquals(tf2, pl2.currentAudioFile());
    }

    @Test
    public void testSnippet03() throws Exception {
        System.out.println("---- Test Snippet 03 ----");
        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        AudioFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");

        PlayList pl3 = new PlayList();
        pl3.add(tf1);
        pl3.add(tf2);
        System.out.println(pl3.currentAudioFile());
        assertEquals(tf1, pl3.currentAudioFile());
        pl3.nextSong();
        System.out.println(pl3.currentAudioFile());
        assertEquals(tf2, pl3.currentAudioFile());
        pl3.nextSong();
        System.out.println(pl3.currentAudioFile());
        assertEquals(tf1, pl3.currentAudioFile());
    }

    @Test
    public void testSnippet04() throws Exception {
        System.out.println("---- Test Snippet 04 ----");
        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        AudioFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        AudioFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");

        PlayList pl4 = new PlayList();
        pl4.add(tf1);
        pl4.add(tf2);
        pl4.add(tf3);
        System.out.println(pl4.getList());
        pl4.setSortCriterion(SortCriterion.DURATION);
        System.out.println(pl4.currentAudioFile());
        assertEquals(tf2, pl4.currentAudioFile());
        pl4.nextSong();
        System.out.println(pl4.currentAudioFile());
        assertEquals(tf3, pl4.currentAudioFile());
        pl4.nextSong();
        System.out.println(pl4.currentAudioFile());
        assertEquals(tf1, pl4.currentAudioFile());
    }

    @Test
    public void testSnippet05() throws Exception {
        System.out.println("---- Test Snippet 05 ----");
        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        AudioFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        AudioFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");

        PlayList pl5 = new PlayList();
        pl5.add(tf1);
        pl5.add(tf2);
        pl5.add(tf3);
        System.out.println(pl5.currentAudioFile());
        assertEquals(tf1, pl5.currentAudioFile());
        pl5.nextSong();
        pl5.setSearch("Eisbach");
        System.out.println(pl5.currentAudioFile());
        assertEquals(tf1, pl5.currentAudioFile());
        pl5.nextSong();
        System.out.println(pl5.currentAudioFile());
        assertEquals(tf3, pl5.currentAudioFile());
    }

    @Test
    public void testJumpTo() throws Exception {
        System.out.println("---- Test JumpTo ----");
        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        AudioFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        AudioFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");

        PlayList list = new PlayList();
        list.add(tf1);
        list.add(tf2);
        list.add(tf3);

        list.jumpToAudioFile(tf2);
        System.out.println(list.currentAudioFile());
        assertEquals(tf2, list.currentAudioFile());

        list.nextSong();
        System.out.println(list.currentAudioFile());
        assertEquals(tf3, list.currentAudioFile());

        list.nextSong();
        System.out.println(list.currentAudioFile());
        assertEquals(tf1, list.currentAudioFile());
    }
}
