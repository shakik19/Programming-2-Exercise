package studiplayer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import studiplayer.audio.AudioFileFactory;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.TaggedFile;
import studiplayer.audio.WavFile;

public class TestSubtaskB {
    @Test
    public void testFactoryException() {
        try {
            AudioFileFactory.createAudioFile("does not exist.mp3");
            fail("Expected exception NotPlayableException");
        } catch (NotPlayableException e) {
            // Expected
        }
    }

    @Test
    public void testWavFileException() {
        try {
            new WavFile("does not exist.wav");
            fail("Expected exception NotPlayableException");
        } catch (NotPlayableException e) {
            // Expected
        }
    }

    @Test
    public void testTaggedFileException() {
        try {
            new WavFile("does not exist.mp3");
            fail("Expected exception NotPlayableException");
        } catch (NotPlayableException e) {
            // Expected
        }
    }

    @Test
    public void testPlayException() throws IOException {
        File file = File.createTempFile("test", "mp3");
        try {
            TaggedFile taggedFile = new TaggedFile();
            taggedFile.parsePathname(file.getAbsolutePath());
            taggedFile.parseFilename(taggedFile.getFilename());
            taggedFile.play();
            fail("Expected exception NotPlayableException");
        } catch (NotPlayableException e) {
            // Expected
        }
    }

    @Test
    public void testReadAndStoreTagsException() throws IOException {
        File file = File.createTempFile("test", "mp3");
        try {
            TaggedFile taggedFile = new TaggedFile();
            taggedFile.parsePathname(file.getAbsolutePath());
            taggedFile.parseFilename(taggedFile.getFilename());
            taggedFile.readAndStoreTags();
            fail("Expected exception NotPlayableException");
        } catch (NotPlayableException e) {
            // Expected
        }
    }

    @Test
    public void testReadAndSetDurationException() throws IOException {
        File file = File.createTempFile("test", "wav");
        try {
            WavFile wavFile = new WavFile();
            wavFile.parsePathname(file.getAbsolutePath());
            wavFile.parseFilename(wavFile.getFilename());
            wavFile.readAndSetDurationFromFile();
            fail("Expected exception NotPlayableException");
        } catch (NotPlayableException e) {
            // Expected
        }
    }

    @Test
    public void testPlayListCanReadParts() throws IOException {
        File file = File.createTempFile("test", "m3u");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(new StringBuilder()
                    .append("audiofiles/Rock 812.mp3\n")
                    .append("audiofiles/wellenmeister_awakening.ogg\n")
                    .append("audiofiles/unknown.mp3\n")
                    .append("audiofiles/other.abc\n")
                    .toString());
            writer.close();
        }
        PlayList pl = new PlayList();
        try {
            pl.loadFromM3U(file.getAbsolutePath());
            System.out.println(pl.getList().toString());
        } catch (Throwable t) {
            fail("We should not get an exception");
        }
        assertEquals(2, pl.size());
    }
}
