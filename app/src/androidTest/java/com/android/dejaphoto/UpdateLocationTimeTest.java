package com.android.dejaphoto;

import android.support.test.rule.ActivityTestRule;

import com.google.android.gms.nearby.messages.internal.Update;
import com.google.maps.GeoApiContext;

import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by chuck on 5/13/17.
 */

public class UpdateLocationTimeTest {

    GeoApiContext geoContext = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");

    //@Rule
   // public ActivityTestRule<UpdateLocationTime> updateLocationTime = new ActivityTestRule<UpidateLocationTime>(UpdateLocationTime.class);

    @Test
    public void testTimeOfDay(){

        String timeOfDay = UpdateLocationTime.getCurrentTime();
        assertEquals("19", timeOfDay);
    }

    @Test
    public void testDayOfWeek(){
        String dayOfWeek = UpdateLocationTime.getCurrentDay();
        assertEquals("Saturday", dayOfWeek);
    }

    @Test
    public void testZipCode(){


        String zipCode = UpdateLocationTime.getCurrentZip()
    }



}
