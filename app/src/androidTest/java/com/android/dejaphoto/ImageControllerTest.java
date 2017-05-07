package com.android.dejaphoto;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.rule.ActivityTestRule;

import org.junit.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

//import static org.junit.Assert.assertTrue;

/**
 * Created by Cyrax on 5/6/2017.
 */

public class ImageControllerTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testDisplayImage()
    {
        Activity mainActivity = activityTestRule.getActivity();
        Drawable expected = mainActivity.getResources().getDrawable(R.drawable.apple);

        ImageController changer = new ImageController(mainActivity.getApplicationContext());
        changer.displayImage();

        Drawable result = changer.getImage();

        assertTrue(areDrawablesIdentical(expected, expected));

    }

    //Helpers

    /** Credit: vaughandroid from StackOverflow **/
    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }
}
