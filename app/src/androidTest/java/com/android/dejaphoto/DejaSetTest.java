package com.android.dejaphoto;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test functionality for DejaSet class.
 */
@RunWith(AndroidJUnit4.class)
public class DejaSetTest {

    public static final String EARLIER_DATE = "1997:01:01 10:10:10";
    public static final String LATER_DATE = "2017:01:01 10:10:10";

    DejaSet set;

    /**
     * Set up for testing.
     */
    @Before
    public void setup() {
        set = new DejaSet();
    }

    /**
     * Test functionality for weight function.
     */
    @Test
    public void testWeight() {
        List<Photo> photos = new ArrayList<>();

        // weight = 3
        Photo first = new Photo();
        first.location = "first";
        photos.add(first);
        photos.add(first);
        photos.add(first);

        // weight = 2
        Photo second = new Photo();
        second.location = "second";
        photos.add(second);
        photos.add(second);

        // weight = 1
        Photo third = new Photo();
        third.location = "third";
        photos.add(third);

        set.initializeSet(photos);

        assertEquals(first, set.next());
        assertEquals(second, set.next());
        assertEquals(third, set.next());
    }

    /**
     * Test functionality for karma function.
     */
    @Test
    public void testKarma() {
        List<Photo> photos = new ArrayList<>();

        // karma = true
        Photo first = new Photo();
        first.location = "first";
        first.karma = 1;
        photos.add(first);

        // karma = false
        Photo second = new Photo();
        second.location = "second";
        second.karma = 0;
        photos.add(second);

        set.initializeSet(photos);

        assertEquals(first, set.next());
        assertEquals(second, set.next());
    }

    /**
     * Test functionality for recency function.
     */
    @Test
    public void testRecency() {
        List<Photo> photos = new ArrayList<>();

        // recency = closer to current time
        Photo first = new Photo();
        first.location = "first";
        first.datetime = LATER_DATE;
        photos.add(first);

        // recency = further from current time
        Photo second = new Photo();
        second.location = "second";
        second.location = EARLIER_DATE;
        photos.add(second);

        set.initializeSet(photos);

        assertEquals(first, set.next());
        assertEquals(second, set.next());
    }

}
