package com.android.dejaphoto;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test functionality for PhotoEditor class.
 */
@RunWith(AndroidJUnit4.class)
public class PhotoEditorTest {

    public static final int PHOTO_WIDTH = 100;
    public static final int PHOTO_HEIGHT = 100;
    public static final int SCREEN_WIDTH = 200;
    public static final int SCREEN_HEIGHT = 400;

    Photo photo;
    PhotoEditor editor;

    /**
     * Set up for testing.
     */
    @Before
    public void setup() {
        photo = new Photo();
        photo.photo = Bitmap.createBitmap(PHOTO_WIDTH, PHOTO_HEIGHT, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(photo.photo);
        canvas.drawRGB(255, 255, 255);

        editor = PhotoEditor.start(photo, photo.photo);
    }

    /**
     * Test functionality getScale() with fit = true.
     */
    @Test
    public void testGetScaleFit() {
        // test same dimension resizing
        assertEquals(1, editor.getScale(PHOTO_WIDTH, PHOTO_HEIGHT, true), 0);

        // test normal screen resizing
        assertEquals(2, editor.getScale(SCREEN_WIDTH, SCREEN_HEIGHT, true), 0);

        // test incorrect dimension resizing
        assertEquals(-1, editor.getScale(0, 0, true), 0);
    }

    /**
     * Test functionality getScale() with fit = false.
     */
    @Test
    public void testGetScaleCrop() {
        // test same dimension resizing
        assertEquals(1, editor.getScale(PHOTO_WIDTH, PHOTO_HEIGHT, false), 0);

        // test normal screen resizing
        assertEquals(4, editor.getScale(SCREEN_WIDTH, SCREEN_HEIGHT, false), 0);

        // test incorrect dimension resizing
        assertEquals(-1, editor.getScale(0, 0, false), 0);
    }

    /**
     * Test functionality resize() with fit = true.
     */
    @Test
    public void testResizeFit() {
        // test same dimension resize
        Bitmap resized = editor.resize(PHOTO_WIDTH, PHOTO_HEIGHT, true);
        assertEquals(photo.photo.getWidth(), resized.getWidth());
        assertEquals(photo.photo.getHeight(), resized.getHeight());

        // test normal screen resizing
        resized = editor.resize(SCREEN_WIDTH, SCREEN_HEIGHT, true);
        assertEquals(photo.photo.getWidth() * SCREEN_WIDTH / PHOTO_WIDTH, resized.getWidth());
        assertEquals(photo.photo.getHeight() * SCREEN_WIDTH / PHOTO_WIDTH, resized.getHeight());

        // test incorrect dimension resizing
        assertEquals(null, editor.resize(0, 0, true));
    }

    /**
     * Test functionality resize() with fit = false.
     */
    @Test
    public void testResizeCrop() {
        // test same dimension resize
        Bitmap resized = editor.resize(PHOTO_WIDTH, PHOTO_HEIGHT, false);
        assertEquals(photo.photo.getWidth(), resized.getWidth());
        assertEquals(photo.photo.getHeight(), resized.getHeight());

        // test normal screen resizing
        resized = editor.resize(SCREEN_WIDTH, SCREEN_HEIGHT, false);
        assertEquals(photo.photo.getWidth() * SCREEN_HEIGHT / PHOTO_WIDTH, resized.getWidth());
        assertEquals(photo.photo.getHeight() * SCREEN_HEIGHT / PHOTO_WIDTH, resized.getHeight());

        // test incorrect dimension resizing
        assertEquals(null, editor.resize(0, 0, false));
    }

    /**
     * Test functionality fitScreen() with fit = true.
     */
    @Test
    public void testFitScreenFit() {
        // test same dimension resize
        Bitmap resized = editor.fitScreen(PHOTO_WIDTH, PHOTO_HEIGHT, true).finish();
        for (int x = 0; x < resized.getWidth(); ++x)
            for (int y = 0; y < resized.getHeight(); ++y)
                assertEquals(photo.photo.getPixel(x, y), resized.getPixel(x, y));

        // test normal screen resize
        resized = editor.fitScreen(SCREEN_WIDTH, SCREEN_HEIGHT, true).finish();
        for (int x = 0; x < resized.getWidth(); ++x)
            for (int y = 0; y < resized.getWidth(); ++y)
                if (y < 100 || y > 300)
                    assertEquals(-16777216, resized.getPixel(x, y));

        // test incorrect dimensions resize
        assertEquals(editor, editor.fitScreen(0, 0, true));
    }

    /**
     * Test functionality fitScreen() with fit = false.
     */
    @Test
    public void testFitScreenCrop() {
        // test same dimension resize
        Bitmap resized = editor.fitScreen(PHOTO_WIDTH, PHOTO_HEIGHT, false).finish();
        for (int x = 0; x < resized.getWidth(); ++x)
            for (int y = 0; y < resized.getHeight(); ++y)
                assertEquals(photo.photo.getPixel(x, y), resized.getPixel(x, y));

        // test normal screen resize
        resized = editor.fitScreen(SCREEN_WIDTH, SCREEN_HEIGHT, true).finish();
        for (int x = 0; x < resized.getWidth(); ++x)
            for (int y = 0; y < resized.getWidth(); ++y)
                assertEquals(resized.getPixel(x, y), resized.getPixel(x, y));

        // test incorrect dimensions resize
        assertEquals(editor, editor.fitScreen(0, 0, false));
    }

    /**
     * Test functionality appendLocation().
     */
    @Test
    public void testAppendLocation() {
        // test invalid input
        try {
            Bitmap edit = editor.appendLocation(null).finish();
            for (int x = 0; x < edit.getWidth(); ++x)
                for (int y = 0; y < edit.getWidth(); ++y)
                    assertEquals(photo.photo.getPixel(x, y), edit.getPixel(x, y));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test functionality addText().
     */
    @Test
    public void testAddText() {
        // test empty string
        Bitmap edit = editor.addText("").finish();
        for (int x = 0; x < edit.getWidth(); ++x)
            for (int y = 0; y < edit.getWidth(); ++y)
                assertEquals(photo.photo.getPixel(x, y), edit.getPixel(x, y));

        // test invalid input
        assertEquals(editor, editor.addText(null));
    }

    /**
     * Test functionality finish().
     */
    @Test
    public void testFinish() {
        // test normal screen resize
        Bitmap edit = editor.finish();
        for (int x = 0; x < edit.getWidth(); ++x)
            for (int y = 0; y < edit.getWidth(); ++y)
                assertEquals(photo.photo.getPixel(x, y), edit.getPixel(x, y));
    }


}
