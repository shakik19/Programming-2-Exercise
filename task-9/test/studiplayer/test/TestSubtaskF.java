package studiplayer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import studiplayer.audio.*;

public class TestSubtaskF {

    private void debug(String msg, PlayList list) {
/*        System.out.println(msg + ":");
        for (AudioFile file : list) {
            System.out.print(" -> ");
            System.out.println(file);
        }*/
    }

    @Test
    public void testSearch() throws NotPlayableException {
        PlayList list = new PlayList();

        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        AudioFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        AudioFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");

        list.add(tf1);
        list.add(tf2);
        list.add(tf3);

        list.setSearch("Eisbach");

        debug("Your iteration result for search 'Eisbach' is", list);

        Iterator<AudioFile> iterator = list.iterator();
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Rock 812.mp3 as first iteration result", tf1, iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Eisbach Deep Snow.ogg as second iteration result", tf3, iterator.next());
        assertFalse("Should only provide two elements", iterator.hasNext());

        assertEquals("Number of elements should not be changed, three elements were added", 3, list.getList().size());
    }

    @Test
    public void testSortDuration() throws NotPlayableException {
        PlayList list = new PlayList();

        AudioFile tf1 = new TaggedFile("audiofiles/Rock 812.mp3");
        AudioFile tf2 = new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg");
        AudioFile tf3 = new TaggedFile("audiofiles/Eisbach Deep Snow.ogg");

        list.add(tf1);
        list.add(tf2);
        list.add(tf3);

        list.setSortCriterion(SortCriterion.DURATION);

        debug("Your iteration result for sort by duration is", list);

        Iterator<AudioFile> iterator = list.iterator();
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Motiv 5. Symphonie von Beethoven.ogg as first iteration result", tf2,
                iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Eisbach Deep Snow.ogg as second iteration result", tf3, iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Rock 812.mp3 as third iteration result", tf1, iterator.next());
        assertFalse("Should only provide two elements", iterator.hasNext());
        assertNotEquals(
                "Sorting by duration should not change the list in PlayList, only the iteration should be changed!",
                list.getList().get(0), tf2);
    }

    @Test
    public void testSortAndSearch() throws NotPlayableException {
        PlayList list = new PlayList();

        List<AudioFile> check = Arrays.asList(
                new TaggedFile("audiofiles/Rock 812.mp3"),
                new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg"),
                new TaggedFile("audiofiles/Eisbach Deep Snow.ogg"));

        for (AudioFile file : check) {
            list.add(file);
        }

        list.setSortCriterion(SortCriterion.TITLE);
        list.setSearch("Eisbach");

        debug("Your iteration result for sort by album and search for 'Eisbach' is", list);

        List<AudioFile> ref = list.getList();
        assertEquals(check.size(), ref.size());

        Iterator<AudioFile> iterator = list.iterator();
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Eisbach Deep Snow.ogg as first iteration result", check.get(2), iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Rock 812.mp3 as second iteration result", check.get(0), iterator.next());
        assertFalse("Should only provide two elements", iterator.hasNext());

        assertEquals(
                "Sorting by title and filter by 'Eisbach' should not change the list in PlayList, only the iteration should be changed!",
                list.getList().get(0), check.get(0));
    }

    @Test
    public void testListShouldNotChange() throws NotPlayableException {
        PlayList list = new PlayList();

        List<AudioFile> check = Arrays.asList(
                new TaggedFile("audiofiles/Rock 812.mp3"),
                new TaggedFile("audiofiles/Motiv 5. Symphonie von Beethoven.ogg"),
                new TaggedFile("audiofiles/Eisbach Deep Snow.ogg"));

        for (AudioFile file : check) {
            list.add(file);
        }

        list.setSortCriterion(SortCriterion.TITLE);
        list.setSearch("Eisbach");

        list.setSortCriterion(SortCriterion.DEFAULT);
        list.setSearch("");

        debug("Your iteration result for sort by album and search for 'Eisbach' is", list);

        Iterator<AudioFile> iterator = list.iterator();
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Eisbach Deep Snow.ogg as first iteration result", check.get(0), iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Motiv 5. Symphonie von Beethoven.ogg as second iteration result", check.get(1),
                iterator.next());
        assertTrue("Iterator should provide one more result", iterator.hasNext());
        assertEquals("Should provide Eisbach Deep Snow.ogg as third iteration result", check.get(2), iterator.next());
        assertFalse("Should only provide two elements", iterator.hasNext());
    }
}
