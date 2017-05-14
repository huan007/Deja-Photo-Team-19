package com.android.dejaphoto;

import android.util.Log;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * Created by Andy Or on 5/10/2017.
 */

public class DejaSet {

    TreeSet<Photo> set;


    public DejaSet() {
        set = new TreeSet<>(new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                // Photos are the same; duplicates
                if (o1.location + o1.datetime == o2.location + o2.datetime)
                    return 0;

                //  First photo with more relevant location, day of week, and  time
                if (o1.weight != o2.weight)
                    return o1.weight - o2.weight;
                //  One photo has karma while the other doesn't
                else if (o1.karma != o2.karma)
                    return (o1.karma) ? 1 : -1;
                // More recent photo is greater
                else
                    return (o1.getRecency() > o2.getRecency()) ? 1 : -1;
            }
        });
    }

    public void initializeSet(List<Photo> photoList) {
        // null check
        if (photoList == null) {
            Log.d("Deja Set", "No photos");
            return;
        }

        Log.d("Deja Set", "Clearing previous existings photo set");
        // clear current set
        set.clear();

        // reset weight to 0
        for (Photo photo : photoList) {
            photo.weight = 0;
        }

        // increment weight of photos that are relevant
        for (Photo photo : photoList) {
            photo.weight++;
        }

        Log.d("Deja Set", "Inserting photos into set");
        // insert photos into BST
        for (Photo photo : photoList) {
            if (!photo.release)
                set.add(photo);
        }
    }

    public Photo next() {
        Log.d("Deja Set", "Get last photo in set");
        return set.pollLast();
    }


}
