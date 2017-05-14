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
 * Created by chuck on 5/13/17.
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

    //Returns a string describing users current zip code
    public static String getCurrentZip(GeoApiContext geoContext){
        Double latitude = MainActivity.returnLatitude();
        Double longitude = MainActivity.returnLong();

        LatLng location = new LatLng(latitude, longitude);

        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoContext, location).await();
            int numOfComponents = results[0].addressComponents.length;
            return results[0].addressComponents[numOfComponents-1].longName;
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


}

