package com.android.dejaphoto;

import android.content.Context;
import android.util.Log;

/**
 * Returns a next element.
 */
public interface Chooser<E> {

    /**
     * Returns the next element.
     *
     * @return the next element
     */
    E next(Context context);

    /**
     * Refresh the list of elements.
     */
    void refresh();
}
