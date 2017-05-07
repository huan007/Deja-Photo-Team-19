package com.android.dejaphoto;

import java.util.LinkedList;
import java.util.ListIterator;

public class PhotoQueue<E> {

    public static final int MAX_SIZE = 10;

    Chooser<E> chooser;
    LinkedList<E> queue;
    ListIterator<E> curr;

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

    public E next() {
        E next;

        if (curr.nextIndex() == queue.size()) {
            if (queue.size() == MAX_SIZE) {
                queue.removeFirst();
                curr = queue.listIterator(queue.size());
            }

            next = chooser.next();
            curr.add(next);
        } else {
            next = curr.next();
        }

        return next;
    }

    public E previous() {
        if (curr.previousIndex() == -1) {
            return queue.getFirst();
        }

        return curr.previous();
    }

    public int size() {
        return queue.size();
    }


}
