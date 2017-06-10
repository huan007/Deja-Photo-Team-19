package com.android.dejaphoto;

import android.app.Activity;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;

import com.google.maps.GeoApiContext;

import org.junit.*;

import java.io.File;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Cyrax on 5/12/2017.
 */

public class DatabaseTest {
    DatabaseManager database;
    GeoApiContext geoContext = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp()
    {
        //Set up the gallery and import all images into the database
        Activity mainActivity = activityTestRule.getActivity();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
        Album.GetAllPhotosFromGallery gallery = new Album.GetAllPhotosFromGallery(file, mainActivity.getApplicationContext(), geoContext);
        database = new DatabaseManager(gallery.images);
    }

    @Test
    public void testQueryDay()
    {
        //There were 5 pictures taken on Sunday
        assertEquals(5, database.queryDayOfTheWeek("Sunday").size());
        //There were 5 pictures without information
        assertEquals(7, database.queryDayOfTheWeek("Unknown").size());
    }

    @Test
    public void testQueryHour()
    {
        //There were 5 pictures taken at 13:00
        assertEquals(5, database.queryHour("13").size());
        //There were 5 pictures taken at 14:00
        assertEquals(5, database.queryHour("14").size());
        //There was 1 picture taken at 15:00
        assertEquals(1, database.queryHour("15").size());
        //There were 7 pictures without information
        assertEquals(7, database.queryHour("Unknown").size());
    }

    @Test
    public void testQueryLocation()
    {
        //There were 18 photos, 15 of which has no location
        assertEquals(15, database.queryLocation(null, null).size());
    }

    @Test
    public void testSize()
    {
        assertEquals(18, database.size());
    }
}
