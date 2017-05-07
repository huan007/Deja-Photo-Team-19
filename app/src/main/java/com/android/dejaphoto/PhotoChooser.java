package com.android.dejaphoto;

/**
 * Returns a next photo.
 */
public class PhotoChooser implements Chooser<String> {

    /**
     * Returns the next photo.
     *
     * @return the next photo
     */
    public String next() {
        return (true) ? dejaNext() : randomNext();
    }

    /**
     * Returns the next photo based on relevant info.
     *
     * @return the next photo
     */
    private String dejaNext() {
        return null;
    }

    /**
     * Returns the next photo randomly.
     *
     * @return the next photo
     */
    private String randomNext() {
        return null;
    }

}
