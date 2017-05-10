package com.android.dejaphoto;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Returns a next photo.
 */
public class PhotoChooser implements Chooser<Photo> {

    List<Photo> photos;

    public PhotoChooser(List<Photo> photos) {
        this.photos = photos;
    }

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    public Photo next() {
        return (false) ? dejaNext() : randomNext();
    }

    /**
     * Returns the next photo based on relevant info.
     *
     * @return the next photo
     */
    private Photo dejaNext() {
        return null;
    }

    /**
     * Returns the next photo randomly.
     *
     * @return the next element
     */
    private Photo randomNext() {
        return photos.get(new Random(Calendar.getInstance().get(Calendar.SECOND)).nextInt(photos.size()));
    }

}
