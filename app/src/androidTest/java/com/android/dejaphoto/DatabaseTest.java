package com.android.dejaphoto;

import android.app.Activity;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;

import org.junit.*;

import java.io.File;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Cyrax on 5/12/2017.
 */

public class DatabaseTest {
    DatabaseManager database;

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setUp()
    {
        //Set up the gallery and import all images into the database
        Activity mainActivity = activityTestRule.getActivity();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
        GetAllPhotosFromGallery gallery = new GetAllPhotosFromGallery(file, mainActivity.getApplicationContext());
        database = new DatabaseManager(gallery.images);
    }

    @Test
    public void testQueryDay()
    {
        //There were 5 pictures taken on Sunday
        assertEquals(5, database.queryDayOfTheWeek("Sunday").size());
        //There were 5 pictures without information
        assertEquals(5, database.queryDayOfTheWeek("Unknown").size());
    }

    @Test
    public void testQueryHour()
    {
        //There were 5 pictures taken at 13:00
        assertEquals(5, database.queryHour("13").size());
        //There were 5 pictures without information
        assertEquals(5, database.queryHour("Unknown").size());
    }

    @Test
    public void testSize()
    {
        assertEquals(10, database.size());
    }
}
