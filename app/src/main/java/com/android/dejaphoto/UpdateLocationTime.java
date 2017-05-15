package com.android.dejaphoto;
import java.util.Calendar;
import java.util.Locale;


import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.IOException;

/**
 * Class creates functionality to get users current location, time of day, and day of the week.
 */

public class UpdateLocationTime {

    //Returns a string describing what hour of the day it is
    public static String getCurrentTime(){

        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);

        String hourOfDay = Integer.toString(hour);

        return hourOfDay;
    }

    //Returns a string describing current day of the week
    public static String getCurrentDay(){

        Calendar c = Calendar.getInstance();

        //creating Locale object for the US
        Locale english = new Locale("english");
        english = Locale.US;

        //getting day of week in long format, i.e. "Thursday"
        String currDay = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, english);

        return currDay;
    }

    //Returns a string describing users current latitude
    public static String getCurrentLat()
    {//return string value
        return String.valueOf(MainActivity.returnLatitude());
    }
    //Returns a string describing users current longitude
    public static String getCurrentLong()
    {//return string value
        return String.valueOf(MainActivity.returnLong());
    }


}

