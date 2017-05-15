package com.android.dejaphoto;

import android.util.Log;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * This class sorts a list of photos by increasing weight, then karma, then recency.
 * User can then call to get the next more relavent photo.
 */
public class DejaSet {

    TreeSet<Photo> set;

    /**
     * Default Constructor. Sort photos by increasing weight, then karma, then recency.
     */
    public DejaSet() {
        set = new TreeSet<>(new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                // Photos are the same; duplicates
                if ((o1.location + o1.datetime).equals(o2.location + o2.datetime))
                    return 0;

                //  First photo with more relevant location, day of week, and  time
                if (o1.weight != o2.weight)
                    return Math.round(Math.round(o1.weight - o2.weight));
                //  One photo has karma while the other doesn't
                else if (o1.karma != o2.karma)
                    return (o1.karma) ? 1 : -1;
                // More recent photo is greater
                else
                    return (o1.getRecency() > o2.getRecency()) ? 1 : -1;
            }
        });
    }

    /**
     * Initialize and order photo set.
     *
     * @param photoList list of photos
     */
    public void initializeSet(List<Photo> photoList) {
        // null check
        if (photoList == null) {
            Log.d("Deja Set", "No photos");
            return;
        }

        // clear current set
        Log.d("Deja Set", "Clearing previous existings photo set");
        set.clear();

        // reset photo weights
        Log.d("Deja Set", "Reseting photo weights");
        for (Photo photo : photoList)
            photo.weight = 0;

        // calculate photo weights
        Log.d("Deja Set", "Calculating photo weights");
        for (Photo photo : photoList)
            photo.weight++;

        // insert photos into BST
        Log.d("Deja Set", "Inserting photos into set");
        for (Photo photo : photoList) {
            if (!photo.release)
                set.add(photo);
        }
    }

    /**
     * Get the next photo.
     *
     * @return next photo
     */
    public Photo next() {
        Log.d("Deja Set", "Get last photo in set");
        return set.pollLast();
    }


}
