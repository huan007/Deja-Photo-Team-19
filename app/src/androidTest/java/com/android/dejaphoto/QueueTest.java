package com.android.dejaphoto;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class QueueTest {

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
        public Integer next(Context context) {
            int next = new Random().nextInt(MAX_RAND);
            history.add(next);
            return next;
        }

        /**
         * Refresh the list of elements.
         */
        @Override
        public void refresh(Context context) {
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
            assertEquals(queue.next(null), chooser.history.get(chooser.history.size() - 1));
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
            int q = queue.previous();
            int c = chooser.history.get((i < 10) ? chooser.history.size() - 2 - i : chooser.history.size() - PhotoQueue.MAX_SIZE);
            assertEquals(q, c);
            assertEquals(queue.size(), PhotoQueue.MAX_SIZE);
        }
    }

}
