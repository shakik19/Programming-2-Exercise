package studiplayer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import studiplayer.audio.PlayList;
import studiplayer.audio.SortCriterion;

public class TestSubtaskE {
    @Test
    public void testSearch() {
        PlayList pl = new PlayList();
        assertTrue(pl.getSearch() == null || "".equals(pl.getSearch()));
        pl.setSearch("Something");
        assertEquals("Something", pl.getSearch());
    }

    @Test
    public void testSort() {
        PlayList pl = new PlayList();
        assertEquals(SortCriterion.DEFAULT, pl.getSortCriterion());
        pl.setSortCriterion(SortCriterion.ALBUM);
        assertEquals(SortCriterion.ALBUM, pl.getSortCriterion());
    }
}
