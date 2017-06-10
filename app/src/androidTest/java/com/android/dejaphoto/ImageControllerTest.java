package com.android.dejaphoto;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.google.maps.GeoApiContext;

import org.junit.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * Created by Cyrax on 5/6/2017.
 */

public class ImageControllerTest {
    GeoApiContext geoContext = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void testDisplayImage()
    {
        Activity mainActivity = activityTestRule.getActivity();
        Drawable expected = mainActivity.getResources().getDrawable(R.drawable.apple);

        //Create ctontroller
        ImageController controller = new ImageController(mainActivity.getApplicationContext());

        //Get list of pictures
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
        Album.GetAllPhotosFromGallery gallery = new Album.GetAllPhotosFromGallery(file, mainActivity.getApplicationContext(), geoContext);

        PhotoChooser chooser = new PhotoChooser(gallery.images, geoContext);
        PhotoQueue<Photo> queue = new PhotoQueue<>(chooser);

        List<Photo> list = new LinkedList<>();

        for(int i = 0; i < 10; i++)
        {
            list.add(queue.next(mainActivity));
        }

        for (Photo photo : list)
        {
            assertTrue(photo instanceof Photo);
            Log.d("Tester", photo.toString());
            String dow = photo.getDayOfTheWeek();
            if (dow != null) {
                Log.d("Tester", "Day of the Week: " + dow);
            }
            else
                Log.d("Tester", "Day of the Week: NULL");
        }

        assertEquals(list.size(), 10);
    }

    @Test
    public void testNothing()
    {
        System.out.print("hello");
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
