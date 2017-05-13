package com.android.dejaphoto;

/**
 * Returns a next element.
 */
public interface Chooser<E> {

    /**
     * Returns the next element.
     *
     * @return the next element
     */
    E next();

    /**
     * Refresh the list of elements.
     */
    void refresh();
}
