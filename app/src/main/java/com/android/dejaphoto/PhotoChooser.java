package com.android.dejaphoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.maps.GeoApiContext;

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

    public PhotoChooser(List<Photo> photos, GeoApiContext geoContext) {
        this.photos = photos;
        dejaPhotos = new DejaSet();
        this.geoContext = geoContext;
        dejaMode = false;
    }

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    @Override
    public Photo next(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        return (dejaMode =(sharedPreferences.getBoolean("dejavu", false))) ? dejaNext(context) : randomNext();
    }

    /**
     * Returns the next photo based on relevant info.
     *
     * @return the next photo
     */
    private Photo dejaNext(Context context) {
        Log.d("Photo Chooser", "Using Deja Algorithm to select next photo");
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        if( sharedPreferences.getBoolean("location", false) ) {
            // If location has changed, reinitialize set

        }
        if( sharedPreferences.getBoolean("day", false) ) {
            // If day of week has changed, reinitialize it

        }
        if( sharedPreferences.getBoolean("time", false) ) {
            // If time has changed, reinitialize set
        }
        // get next photo from DejaSet
        return (photos.size() > 0) ? dejaPhotos.next() : null;
        //return (photos.size() > 0) ? photos.get(new Random(System.currentTimeMillis()).nextInt(photos.size())) : null;

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
    public void refresh() {
        if (dejaMode) {
            Log.d("Photo Chooser", "updating set of Deja Photos");
            dejaPhotos.initializeSet(null);   // TODO pass new list of photos
        } else
            Collections.shuffle(photos, new Random(System.currentTimeMillis()));
    }

}
