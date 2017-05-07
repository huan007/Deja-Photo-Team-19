package com.android.dejaphoto;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Stores history of photos.
 */
public class PhotoQueue<E> {

    public static final int MAX_SIZE = 10;

    Chooser<E> chooser;         // chooses next E
    LinkedList<E> queue;        // stores up to max E
    ListIterator<E> curr;       // current E

    /**
     * Default contructor.
     *
     * @param chooser chooses next element
     */
    public PhotoQueue(Chooser<E> chooser) {
        this.chooser = chooser;
        queue = new LinkedList<E>();
        curr = queue.listIterator();
    }

    /**
     * Returns the next element in the list and advances the cursor position.
     *
     * @return the next element in the list
     */
    public E next() {
        E next;

        // if curr is end of queue
        if (curr.nextIndex() == queue.size()) {
            // queue is at max size
            if (queue.size() == MAX_SIZE) {
                queue.removeFirst();
                curr = queue.listIterator(queue.size());
            }

            // get next element
            next = chooser.next();
            curr.add(next);
        } else {
            next = curr.next();
        }

        return next;
    }

    /**
     * Returns the previous element in the list and moves the cursor position backwards.
     *
     * @return the previous element in the list
     */
    public E previous() {
        // curr is at beginning of list
        if (curr.previousIndex() == -1) {
            return queue.getFirst();
        }

        return curr.previous();
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return queue.size();
    }


}
