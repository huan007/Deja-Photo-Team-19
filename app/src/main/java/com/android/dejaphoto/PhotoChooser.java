package com.android.dejaphoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.nearby.messages.internal.Update;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    String prevDay;
    String prevLat;
    String prevLong;

    public PhotoChooser(List<Photo> photos, GeoApiContext geoContext) {
        this.photos = photos;
        dejaPhotos = new DejaSet();
        dejaPhotos.initializeSet(photos);
        dejaMode = false;
        this.geoContext = geoContext;
        database = new DatabaseManager(photos);
        prevHour = UpdateLocationTime.getCurrentTime();
        prevDay = UpdateLocationTime.getCurrentDay();
        prevLat = UpdateLocationTime.getCurrentLat();
        prevLong = UpdateLocationTime.getCurrentLong();
    }

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    @Override
    public Photo next(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        return (dejaMode = (sharedPreferences.getBoolean("dejavu", true))) ? dejaNext(context) : randomNext();
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

        String currLat = UpdateLocationTime.getCurrentLat();
        String currLong = UpdateLocationTime.getCurrentLong();
        String currDay = UpdateLocationTime.getCurrentDay();
        String currHour = UpdateLocationTime.getCurrentTime();

        //if (!prevLat.equals(currLat) || !prevLong.equals(currLong) || !prevDay.equals(currDay) || !prevHour.equals(currHour)) {

        System.out.println(currLat + " " + currLong);

        for (Photo photo : photos) {
            photo.weight = 0;

            if (sharedPreferences.getBoolean("location", true)) { // location is a factor
                Log.d("Photo Chooser", "updating photos based on location");
                if (currLat != null && currLong != null && photo.longitude != null && photo.latitude != null)
                    photo.weight += DatabaseManager.weighLocation(Double.valueOf(currLat),
                            Double.valueOf(currLong),
                            Double.valueOf(photo.latitude),
                            Double.valueOf(photo.longitude));
            }

            if (sharedPreferences.getBoolean("day", true)) {  // day of week is a factor
                Log.d("Photo Chooser", "updating photos based on day");
                System.out.println(photo.getDayOfTheWeek());
                if (currDay != null && photo.getDayOfTheWeek() != null)
                    photo.weight += DatabaseManager.weighDay(photo.getDayOfTheWeek(), currDay);
            }

            if (sharedPreferences.getBoolean("time", true)) { // time is a factor
                Log.d("Photo Chooser", "updating photos based on time");
                if (currHour != null && photo.getHour() != null)
                    photo.weight += DatabaseManager.weighHour(Integer.valueOf(currHour), Integer.valueOf(photo.getHour()));
            }
        }

        Collections.sort(photos, new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                // Photos are the same; duplicates
                if ((o1.location + o1.datetime).equals(o2.location + o2.datetime))
                    return 0;

                //  First photo with more relevant location, day of week, and  time
                if (o1.weight != o2.weight)
                    return Math.round(Math.round(o1.weight - o2.weight));
                    //  One photo has karma while the other doesn't
                else if (o1.karma != o2.karma)
                    return (o1.karma) ? 1 : -1;
                    // More recent photo is greater
                else
                    return (o1.getRecency() > o2.getRecency()) ? 1 : -1;
            }
        });
        //}

        return photos.get(photos.size() - 1);
    }

    /**
     * Returns the next photo randomly.
     *
     * @return the next element
     */
    private Photo randomNext() {
        Log.d("Photo Chooser", "Using random algorithm to select next photo");
        System.out.println("FUCK");
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
