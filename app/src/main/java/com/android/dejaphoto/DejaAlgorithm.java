package com.android.dejaphoto;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.TreeSet;

/**
 * Created by Andy Or on 5/10/2017.
 */

public class DejaAlgorithm {

    TreeSet<Photo> set;

    public DejaAlgorithm() {
        set = new TreeSet<>(new Comparator<Photo>()  {
            @Override
            public int compare(Photo o1, Photo o2) {
                if (o1.location + o1.datetime == o2.location + o2.datetime)
                    return 0;

                if( o1.weight != o2.weight)
                    return o1.weight - o2.weight;
                else if( o1.karma != o2.karma)
                    return (o1.karma) ? 1 : -1;
                else  // TODO RECENCY
                    return 1;
            }
        });
    }

    public void fill(List<Photo> photoList) {

        // reset weight to 0
        for ( Photo photo: photoList ) {
            photo.weight = 0;
        }

        // increment weight of photos that are relevant
        for ( Photo photo: photoList ) {
            photo.weight++;
        }

        // insert photos into BST
        for ( Photo photo: photoList ) {
            if(!photo.release)
                set.add(photo);
        }
    }

    public Photo next() {
        return set.pollLast();
    }


}
