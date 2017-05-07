package com.android.dejaphoto;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Returns a next photo.
 */
public class PhotoChooser implements Chooser<Photo> {

    List<Photo> images;

    public PhotoChooser(GetAllPhotosFromGallery photos) {
        images = photos.images;
    }

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    public Photo next() {
        return (true) ? dejaNext() : randomNext();
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
        return images.get(new Random(Calendar.getInstance().get(Calendar.SECOND)).nextInt(images.size()));
    }

}
