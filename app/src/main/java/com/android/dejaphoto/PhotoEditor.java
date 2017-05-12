package com.android.dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;

/**
 * Modifies photos.
 */
public class PhotoEditor {

    private Photo photo;
    private Bitmap bitmap;

    /**
     * Hidden default constructor
     */
    private PhotoEditor() {
    }

    /**
     * Main constructor.
     *
     * @param photo photo to edit
     */
    private PhotoEditor(Photo photo) {
        this.photo = photo;
        this.bitmap = Bitmap.createBitmap(photo.photo).copy(Bitmap.Config.ARGB_8888, true);
    }

    /**
     * Create a PhotoEditor object.
     *
     * @param photo photo to edit
     * @return PhotoEditor object
     */
    public static PhotoEditor start(Photo photo) {
        return new PhotoEditor(photo);
    }

    /**
     * Centers images to fit inside canvas of input dimensions.
     *
     * @param width  width dimension
     * @param height width dimension
     * @return modified image
     */
    public PhotoEditor fitScreen(int width, int height, boolean fit) {
        Log.d("PhotoEditor", "fitting photo to screen");

        // create blank canvas
        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // set paint details for bitmap
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // draw black background and bitmap on canvas
        Canvas canvas = new Canvas(background);
        canvas.drawRGB(0, 0, 0);
        bitmap = resize(width, height, fit);
        int x = ((bitmap.getHeight() / bitmap.getWidth()) < (height / width)) ? 0 : (width / 2) - (bitmap.getWidth() / 2);
        int y = ((bitmap.getHeight() / bitmap.getWidth()) < (height / width)) ? (height / 2) - (bitmap.getHeight() / 2) : 0;
        canvas.drawBitmap(bitmap, x, y, paint);

        bitmap = background;

        return this;
    }

    /**
     * Resizes images to fit within dimensions.
     *
     * @param width  width dimension
     * @param height width dimension
     * @return resized image
     */
    private Bitmap resize(int width, int height, boolean fit) {
        Log.d("PhotoEditor", "resizing photo with fit = " + fit);

        // get scale factor to match screen size
        float scale;
        if ((bitmap.getHeight() / bitmap.getWidth()) < (height / width))
            scale = fit ? (float) width / bitmap.getWidth() : (float) height / bitmap.getHeight();
        else
            scale = fit ? (float) height / bitmap.getHeight() : (float) width / bitmap.getWidth();

        // create new resized bitmap
        return Bitmap.createScaledBitmap(bitmap,
                (int) (scale * bitmap.getWidth()),
                (int) (scale * bitmap.getHeight()),
                false);
    }

    /**
     * Adds location of photo to bottom-left corner
     *
     * @param context current context
     * @return edited photo
     * @throws IOException on fail to get location
     */
    public PhotoEditor appendLocation(Context context) throws IOException {
        Log.d("PhotoEditor", "appending location to photo");

        // if no location data return normal photo
        if (photo.latitude == null || photo.latitude.length() == 0 || photo.longitude == null || photo.longitude.length() == 0)
            return this;

        String location = getAddress(context, Double.valueOf(photo.latitude), Double.valueOf(photo.longitude));

        // if no such location return normal photo
        if (location == null)
            return this;

        // add location to bottom-left of photo
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((float) (canvas.getWidth() * 0.05));
        canvas.drawText(location, (float) (canvas.getWidth() * 0.1), (float) (canvas.getHeight() * 0.8), paint);

        return this;
    }

    /**
     * Add text to center of bitmap
     *
     * @param string text to add
     * @return edited bitmap
     */
    public PhotoEditor addText(String string) {
        Log.d("PhotoEditor", "adding text to photo");

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((float) (canvas.getWidth() * 0.05));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(string, (float) (canvas.getWidth() / 2), (float) (canvas.getHeight() / 2) - (paint.getTextSize() / 2), paint);

        return this;
    }

    private String getAddress(Context context, double latitude, double longitude) throws IOException {
        return new Geocoder(context, Locale.getDefault())
                .getFromLocation(latitude, longitude, 1)
                .get(0)
                .getAddressLine(0);
    }

    /**
     * Returns current bitmap.
     *
     * @return current bitmap.
     */
    public Bitmap finish() {
        return bitmap;
    }
}
