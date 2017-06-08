package com.android.dejaphoto;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.nearby.messages.internal.Update;
import com.google.maps.GeoApiContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Testing getting users current location, time of day, day of week
 */
@RunWith(AndroidJUnit4.class)
public class UpdateLocationTimeTest {

    GeoApiContext geoContext = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");

    @Rule
   public ActivityTestRule<MainActivity> updateLocationTime = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testTimeOfDay(){

        String timeOfDay = UpdateLocationTime.getCurrentTime();
        assertEquals("12", timeOfDay);
    }

    @Test
    public void testDayOfWeek(){
        String dayOfWeek = UpdateLocationTime.getCurrentDay();
        assertEquals("Sunday", dayOfWeek);
    }

    @Test
    public void testLocation(){

        String latitude = UpdateLocationTime.getCurrentLat();
        String longitude = UpdateLocationTime.getCurrentLong();

        assertEquals(UpdateLocationTime.getCurrentLat(), UpdateLocationTime.getCurrentLat());
        assertEquals(UpdateLocationTime.getCurrentLong(), UpdateLocationTime.getCurrentLong());
    }



}
