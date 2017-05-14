package com.android.dejaphoto;

import android.util.Log;

import java.util.LinkedList;

/**
 * Stores history of photos.
 */
public class PhotoQueue<E> {

    public static final int MAX_SIZE = 11;  // 10 previous photos + current photo

    private Chooser<E> chooser;         // chooses next E
    private LinkedList<E> prevQ;        // LinkedList of previously seen photos + current photo
    private LinkedList<E> nextQ;        // LinkedList of photos populated when calling previous

    /**
     * Default constructor.
     *
     * @param chooser chooses next element
     */
    public PhotoQueue(Chooser<E> chooser) {
        this.chooser = chooser;
        nextQ = new LinkedList<>();
        prevQ = new LinkedList<>();
    }

    /**
     * Returns the next element in the list and advances the cursor position.
     *
     * @return the next element in the list
     */
    public E next() {
        Log.d("Photo Queue", "Getting next photo");
        Log.i("Photo Queue", "Size of queue is " + size());
        // if curr is end of queue
        if (nextQ.isEmpty()) {
            // queue is at max size
            if (prevQ.size() == MAX_SIZE)
                prevQ.pollFirst();

            // get next element
            E temp = chooser.next();

            prevQ.addLast(temp);
        } else {
            prevQ.addLast(nextQ.pollFirst());
        }

        return prevQ.getLast();
    }

    /**
     * Returns the previous element in the list and moves the cursor position backwards.
     *
     * @return the previous element in the list
     */
    public E previous() {
        // size of queue is 0
        if (prevQ.isEmpty())
            return null;

        Log.d("Photo Queue", "Getting previous photo");
        Log.i("Photo Queue", "Size of queue is " + size());
        // curr is at beginning of list
        if (prevQ.size() == 1) {
            return prevQ.getFirst();
        } else {
            nextQ.addFirst(prevQ.pollLast());
            return prevQ.getLast();
        }
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return prevQ.size() + nextQ.size();
    }

    /**
     * Returns the photo at iterator
     *
     * @return photo iterator is currently at
     */
    public E getCurrentPhoto() {
        return (prevQ == null) ? null : prevQ.getLast();
    }

    /**
     * Returns the chooser.
     *
     * @return chooser
     */
    public Chooser<E> getChooser() {
        return chooser;
    }

}

