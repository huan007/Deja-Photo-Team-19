package com.android.dejaphoto;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.IOException;

/**
 * Created by Cyrax on 5/13/2017.
 */

public class Geocoding {

    public static void main(String args[]) {
        GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");
        try {
            LatLng location = new LatLng(32.881187, -117.237457);
            GeocodingResult[] results = GeocodingApi.geocode(context, "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
            GeocodingResult[] reverseResults = GeocodingApi.reverseGeocode(context, location).await();
            System.out.println("Address: " + results[0].formattedAddress);
            System.out.println("Reverse Address: " + reverseResults[0].formattedAddress);
            AddressComponent[] components = reverseResults[0].addressComponents;

            for (AddressComponent c : components)
            {
                System.out.println("Component: " + c.toString());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        context = null;
    }

}
