package com.android.dejaphoto;

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
        return null;
    }

    //Returns a string describing current day of the week
    public static String getCurrentDay(){
        return null;
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

