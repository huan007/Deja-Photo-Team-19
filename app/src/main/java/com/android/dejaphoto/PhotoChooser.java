package com.android.dejaphoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * Returns a next photo.
 */
public class PhotoChooser implements Chooser<Photo> {

    List<Photo> photos;
    DejaSet dejaPhotos;
    boolean dejaMode;
    GeoApiContext geoContext;
    DatabaseManager database;
    String prevHour;
    String prevLocation;
    String prevDay;

    public PhotoChooser(List<Photo> photos, GeoApiContext geoContext) {
        this.photos = photos;
        dejaPhotos = new DejaSet();
        dejaPhotos.initializeSet(photos);
        dejaMode = false;
        this.geoContext = geoContext;
        database = new DatabaseManager(photos);
        prevHour = UpdateLocationTime.getCurrentTime();
        prevLocation = UpdateLocationTime.getCurrentZip(geoContext);
        prevDay = UpdateLocationTime.getCurrentDay();
    }

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    @Override
    public Photo next(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        return (dejaMode = (sharedPreferences.getBoolean("dejavu", false))) ? dejaNext(context) : randomNext();
    }

    /**
     * Returns the next photo based on relevant info.
     *
     * @return the next photo
     */
    private Photo dejaNext(Context context) {
        // no photos
        if (photos.isEmpty())
            return null;

        Log.d("Photo Chooser", "Using Deja Algorithm to select next photo");

        List relevantPhotos = new ArrayList<Photo>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);

        String currLocation = UpdateLocationTime.getCurrentZip(geoContext);
        String currDay = UpdateLocationTime.getCurrentDay();
        String currHour = UpdateLocationTime.getCurrentTime();

        if (!prevLocation.equals(currLocation) || !prevDay.equals(currDay) || !prevHour.equals(currHour)) {
            if (sharedPreferences.getBoolean("location", false)) { // location is a factor
                // If location has changed, reinitialize set
                Log.d("Photo Chooser", "updating photos based on location");
                List<Photo> locationlist = database.queryLocation(currLocation);
                if (locationlist != null)
                    relevantPhotos.addAll(locationlist);
            }
            if (sharedPreferences.getBoolean("day", false)) {  // day of week is a factor
                // If day of week has changed, reinitialize it
                Log.d("Photo Chooser", "updating photos based on day");
                List<Photo> dayList = database.queryDayOfTheWeek(currDay);
                if (dayList != null)
                    relevantPhotos.addAll(dayList);
            }
            if (sharedPreferences.getBoolean("time", false)) { // time is a factor
                // If time has changed, reinitialize set
                Log.d("Photo Chooser", "updating photos based on time");
                List<Photo> hourList = database.queryHour(currHour);
                if (hourList != null)
                    relevantPhotos.addAll(hourList);
            }
        }
        // reinitialize set if there are any changes
        if (!relevantPhotos.isEmpty())
            dejaPhotos.initializeSet(relevantPhotos);

        // 85% chance for location, day of week, and time to to be relevant
        return (new Random(System.currentTimeMillis()).nextInt(100) < 85) ? dejaPhotos.next() : randomNext();
    }

    /**
     * Returns the next photo randomly.
     *
     * @return the next element
     */
    private Photo randomNext() {
        Log.d("Photo Chooser", "Using random algorithm to select next photo");
        return (photos.size() > 0) ? photos.get(new Random(System.currentTimeMillis()).nextInt(photos.size())) : null;
    }

    /**
     * Refresh the list of photos.
     */
    @Override
    public void refresh(Context context) {
        if (dejaMode) {
            Log.d("Photo Chooser", "updating set of Deja Photos");
            dejaNext(context);
        } else {
            Log.d("Photo Chooser", "updating set of normal photos");
            Collections.shuffle(photos, new Random(System.currentTimeMillis()));
        }
    }

}
