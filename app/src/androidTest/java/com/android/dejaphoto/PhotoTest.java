package com.android.dejaphoto;

import android.support.test.runner.AndroidJUnit4;

import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test functionality for Photo class.
 */
@RunWith(AndroidJUnit4.class)
public class PhotoTest {

    public static final String NORMAL_DATE = "2017:01:01 10:10:10";
    public static final String EARLIER_DATE = "1997:01:01 10:10:10";
    public static final String MALFORMED_DATE = "XXXX:XX:XX XX:XX:XX";

    GeoApiContext geoContext = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");

    Photo photo;

    @Before
    public void setup() {
        photo = new Photo();
    }

    @Test
    public void testSetZipCode() {
        // test invalid input
        photo.setZipCode(geoContext, new LatLng(32.840396, -117.150627));
        assertEquals("92111", photo.getZipCode());
        photo.setZipCode(geoContext, new LatLng(37.102710, -85.301566));
        assertEquals("42728", photo.getZipCode());
    }

    @Test
    public void testGetDayOfTheWeek() {
        // test normal date
        photo.datetime = NORMAL_DATE;
        assertEquals("Sunday".toLowerCase(), photo.getDayOfTheWeek().toLowerCase());

        // test malformed date
        photo.datetime = MALFORMED_DATE;
        assertEquals(null, photo.getDayOfTheWeek());

        // test invalid input
        photo.datetime = null;
        assertEquals(null, photo.getDayOfTheWeek());
    }

    @Test
    public void testGetHour() {
        // test normal date
        photo.datetime = NORMAL_DATE;
        assertEquals("10", photo.getHour());

        // test malformed date
        photo.datetime = MALFORMED_DATE;
        assertEquals(null, photo.getHour());

        // test invalid input
        photo.datetime = null;
        assertEquals(null, photo.getHour());
    }

    @Test
    public void testGetRecency() {
        // test normal date
        photo.datetime = NORMAL_DATE;
        long later = photo.getRecency();

        photo.datetime = EARLIER_DATE;
        long earlier = photo.getRecency();
        assertTrue(earlier < later);

        // test malformed date
        photo.datetime = MALFORMED_DATE;
        assertEquals(0, photo.getRecency());

        // test invalid input
        photo.datetime = null;
        assertEquals(0, photo.getRecency());
    }

}
