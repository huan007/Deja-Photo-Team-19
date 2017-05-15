package com.android.dejaphoto;

import android.content.Context;

/**
 * Returns a next element and refresh list of elements.
 */
public interface Chooser<E> {

    /**
     * Returns the next element.
     *
     * @param context current state of application
     * @return the next element
     */
    E next(Context context);

    /**
     * Refresh the list of elements.
     *
     * @param context current state of application
     */
    void refresh(Context context);
}
