package com.android.dejaphoto;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class PhotoQueueTest {

    public static final int TRIALS = 1000;

    /**
     * Choose the next element from a set of ints.
     */
    public static class IntChooser implements Chooser<Integer> {

        public static final int MAX_RAND = 100;

        public ArrayList<Integer> history;

        /**
         * Default constructor.
         */
        public IntChooser() {
            history = new ArrayList<>();
        }

        /**
         * Returns the next int.
         *
         * @return the next int
         */
        @Override
        public Integer next() {
            int next = new Random().nextInt(MAX_RAND);
            history.add(next);
            return next;
        }
    }

    IntChooser chooser;
    PhotoQueue<Integer> queue;

    /**
     * Setup for testers.
     */
    @Before
    public void setup() {
        chooser = new IntChooser();
        queue = new PhotoQueue<>(chooser);
    }

    /**
     * Test functionality for next().
     */
    @Test
    public void nextTest() {
        for (int i = 0; i < TRIALS; ++i) {
            assertEquals(queue.next(), chooser.history.get(chooser.history.size() - 1));
            assertEquals(queue.size(), (i < PhotoQueue.MAX_SIZE) ? i + 1 : PhotoQueue.MAX_SIZE);
        }
    }

    /**
     * Test functionality for previous().
     */
    @Test
    public void previousTest() {
        nextTest();
        for (int i = 0; i < TRIALS; ++i) {
            assertEquals(queue.previous(), chooser.history.get(chooser.history.size() - 1 - ((i < PhotoQueue.MAX_SIZE) ? i : PhotoQueue.MAX_SIZE - 1)));
            assertEquals(queue.size(), PhotoQueue.MAX_SIZE);
        }
    }

}