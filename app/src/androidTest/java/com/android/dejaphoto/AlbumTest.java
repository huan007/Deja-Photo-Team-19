package com.android.dejaphoto;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Test functionality for Album class.
 */
@RunWith(AndroidJUnit4.class)
public class AlbumTest {

    public static class MockAlbum extends AbstractAlbum {
        public MockAlbum(File file, boolean includePhotos) {
            directoryFile = file;
            this.includePhotos = includePhotos;
        }

        public File findMyPhoto(String photo) {
            return new File("/test/" + photo);
        }
    }

    public static String TEST_FILE = "/test";
    public static final String NULL = "null";

    AbstractAlbum album;

    /**
     * Set up for testing.
     */
    @Before
    public void setup() {
        album = new MockAlbum(new File(TEST_FILE), true);
    }

    /**
     * Test functionality setFile().
     */
    @Test
    public void testSetFile() {
        album.setFile(new File(NULL));
        assertEquals("/" + NULL, album.getFile().getAbsolutePath());

        album = new MockAlbum(new File(NULL), false);
        album.setFile(new File(TEST_FILE));
        assertEquals(TEST_FILE, album.getFile().getAbsolutePath());
    }

    /**
     * Test functionality getFile().
     */
    @Test
    public void testGetFile() {
        assertEquals(TEST_FILE, album.getFile().getAbsolutePath());
        assertEquals("/" + NULL, new MockAlbum(new File(NULL), true).getFile().getAbsolutePath());
    }

    /**
     * Test functionality getIncludePhotos().
     */
    @Test
    public void testGetIncludePhotos() {
        assertEquals(true, album.getIncludePhotos());
        assertEquals(false, new MockAlbum(new File(NULL), false).getIncludePhotos());
    }

    /**
     * Test functionality setIncludePhotos().
     */
    @Test
    public void testSetIncludePhotos() {
        album.setIncludePhotos(false);
        assertEquals(false, album.getIncludePhotos());

        album = new MockAlbum(new File(NULL), false);
        album.setIncludePhotos(true);
        assertEquals(true, album.getIncludePhotos());
    }

    /**
     * Test functionality findMyPhoto();
     */
    @Test
    public void testFindMyPhoto() {
        String photo = "photo";
        assertEquals(TEST_FILE + "/" + photo, album.findMyPhoto(photo).getAbsolutePath());
    }


}
