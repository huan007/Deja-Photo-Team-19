package com.android.dejaphoto;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Test functionality for FirebaseService class.
 */
@RunWith(AndroidJUnit4.class)
public class FirebaseServiceTest {

    public static String VALID = "H~e~l~l~o~";
    public static String UNVALID = "H.e.l.l.o.";
    public static String NORMAL = "Hello";
    public static String EMPTY = "";

    /**
     * Test functionality validateName().
     */
    @Test
    public void testValidateName() {
        assertEquals(VALID, FirebaseService.validateName(UNVALID));
        assertEquals(NORMAL, FirebaseService.validateName(NORMAL));
        assertEquals(EMPTY, FirebaseService.validateName(EMPTY));
    }

    /**
     * Test functionality unvalidateName().
     */
    @Test
    public void testUnvalidateName() {
        assertEquals(UNVALID, FirebaseService.unvalidateName(VALID));
        assertEquals(NORMAL, FirebaseService.unvalidateName(NORMAL));
        assertEquals(EMPTY, FirebaseService.unvalidateName(EMPTY));

    }

}
