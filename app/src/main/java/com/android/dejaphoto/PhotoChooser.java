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

    /**
     * Default constructor.
     *
     * @param photos list of photos
     * @param geoContext current geoContext
     */
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
        // determine whether to get a dejavu photo or random photo
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

        // initialize helper check file
        Check check = new Check();
        check.location = check.day = check.time = true;

        // update weights
        while (!updateWeights(context, check));

        // sort photos by increasing weights
        Collections.sort(photos, new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                // photos are the same; duplicates
                if ((o1.photo).equals(o2.photo))
                    return 0;

                //  first photo with more relevant location, day of week, and  time
                if (o1.weight != o2.weight)
                    return Double.compare(o1.weight, o2.weight);
                    //  one photo has karma while the other doesn't
                else if (o1.karma != o2.karma)
                    return (o1.karma) ? 1 : -1;
                    // more recent photo is greater
                else
                    return (o1.getRecency() > o2.getRecency()) ? 1 : -1;
            }
        });

        // get highest weighted photo that has no been released
        Photo next = null;
        for (int i = 0; i < photos.size(); ++i) {
            next = photos.get(photos.size() - 1 - i);
            if (!next.release)
                break;
        }

        // return highest weighted photo
        return next;
    }

    /**
     * Updates weights of all photos
     *
     * @param context current context
     * @param check what to check
     * @return successful or not
     */
    private boolean updateWeights(Context context, Check check) {

        // current information
        String currLat = UpdateLocationTime.getCurrentLat();
        String currLong = UpdateLocationTime.getCurrentLong();
        String currDay = UpdateLocationTime.getCurrentDay();
        String currHour = UpdateLocationTime.getCurrentTime();

        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        Log.d("Photo Chooser", "updating weights");

        // update weight of each photo with location, date, time
        for (Photo photo : photos) {
            photo.weight = 0;

            // update time weight for each photo
            if (sharedPreferences.getBoolean("location", true) && check.location) {
                Log.d("Photo Chooser", "updating photos based on location");
                if (currLat != null && currLong != null && photo.longitude != null && photo.latitude != null)
                    photo.weight += DatabaseManager.weighLocation(Double.valueOf(currLat),
                            Double.valueOf(currLong),
                            Double.valueOf(photo.latitude),
                            Double.valueOf(photo.longitude));
                else {  // not all photos, ignore location
                    Log.d("Photo Chooser", "not consider location");
                    check.location = false;
                    return false;
                }
            }

            // update time weight for each photo
            if (sharedPreferences.getBoolean("day", true) && check.day) {
                Log.d("Photo Chooser", "updating photos based on day");
                if (currDay != null && photo.getDayOfTheWeek() != null)
                    photo.weight += DatabaseManager.weighDay(photo.getDayOfTheWeek(), currDay);
                else {  // not all photos, ignore day
                    Log.d("Photo Chooser", "not consider day");
                    check.day = false;
                    return false;
                }
            }

            // update time weight for each photo
            if (sharedPreferences.getBoolean("time", true) && check.time) {
                Log.d("Photo Chooser", "updating photos based on time");
                if (currHour != null && photo.getHour() != null)
                    photo.weight += DatabaseManager.weighHour(Integer.valueOf(currHour), Integer.valueOf(photo.getHour()));
                else {  // not all photos, ignore time
                    Log.d("Photo Chooser", "not consider time");
                    check.time = false;
                    return false;
                }
            }
        }

        // successful update
        return true;
    }

    /**
     * Returns the next photo randomly.
     *
     * @return the next element
     */
    private Photo randomNext() {
        Log.d("Photo Chooser", "Using random algorithm to select next photo");

        List<Photo> unreleased = new ArrayList<>();
        for (Photo photo : photos)
            if (!photo.release)
                unreleased.add(photo);

        if (unreleased.isEmpty())
            return null;

        return unreleased.get(new Random(System.currentTimeMillis()).nextInt(unreleased.size()));
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

    /**
     * Helper class to hold checking data.
     */
    private static class Check {
        boolean location;
        boolean day;
        boolean time;
    }

}
