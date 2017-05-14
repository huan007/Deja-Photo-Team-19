package com.android.dejaphoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

    public PhotoChooser(List<Photo> photos) {
        this.photos = photos;
        dejaPhotos = new DejaSet();
    }

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    @Override
    public Photo next(Context context) {
        //SharedPreferences sharedPreferences = MainActivity.getAppContext().getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", MODE_PRIVATE);
        return (sharedPreferences.getBoolean("dejavu", true)) ? dejaNext() : randomNext();
    }

    /**
     * Returns the next photo based on relevant info.
     *
     * @return the next photo
     */
    private Photo dejaNext() {
        Log.d("Photo Chooser", "Using Deja Algorithm to select next photo");
        return (photos.size() > 0) ? dejaPhotos.next() : null;
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
        if (false) {
            Log.d("Photo Chooser", "updating set of Deja Photos");
            dejaPhotos.initializeSet(null);   // TODO pass new list of photos
        } else
            Collections.shuffle(photos, new Random(System.currentTimeMillis()));
    }

}
