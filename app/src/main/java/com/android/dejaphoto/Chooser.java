package com.android.dejaphoto;

import android.content.Context;

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
