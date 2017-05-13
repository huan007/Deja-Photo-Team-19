package com.android.dejaphoto;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Returns a next photo.
 */
public class PhotoChooser implements Chooser<Photo> {

    List<Photo> photos;
    DejaAlgorithm dejaPhotos;

    public PhotoChooser(List<Photo> photos) {
        this.photos = photos;
        dejaPhotos = new DejaAlgorithm();
    }

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    @Override
    public Photo next() {
        return (false) ? dejaNext() : randomNext();
    }

    /**
     * Returns the next photo based on relevant info.
     *
     * @return the next photo
     */
    private Photo dejaNext() {
        return dejaPhotos.next();
    }

    /**
     * Returns the next photo randomly.
     *
     * @return the next element
     */
    private Photo randomNext() {
        return (photos.size() > 0) ? photos.get(new Random(System.currentTimeMillis()).nextInt(photos.size())) : null;
    }

    /**
     * Refresh the list of photos.
     */
    @Override
    public void refresh() {
        if (false)
            dejaPhotos.initializeSet(null);
        else
            Collections.shuffle(photos, new Random(System.currentTimeMillis()));
    }

}
