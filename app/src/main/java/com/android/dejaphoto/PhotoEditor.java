package com.android.dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Modifies photos.
 */
public class PhotoEditor {

    public static final double BOTTOM_MARGIN = 0.75;
    public static final double SIDE_MARGIN = 0.05;
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
    private PhotoEditor(Photo photo, Bitmap bitmap) {
        this.photo = photo;
        this.bitmap = bitmap;
    }

    /**
     * Create a PhotoEditor object.
     *
     * @param photo photo to edit
     * @return PhotoEditor object
     */
    public static PhotoEditor start(Photo photo, Bitmap bitmap) {
        // if null then create blank photo
        if (photo == null) {
            Bitmap background = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(background);
            canvas.drawRGB(0, 0, 0);

            photo = new Photo();
            bitmap = background;
        }

        return new PhotoEditor(photo, bitmap);
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

        // incorrect params
        if (width <= 0 || height <= 0)
            return this;

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
        boolean horizontal = (((double) bitmap.getHeight()) / bitmap.getWidth()) < (((double) height) / width);
        int x = (horizontal) ? 0 : (width / 2) - (bitmap.getWidth() / 2);
        int y = (horizontal) ? (height / 2) - (bitmap.getHeight() / 2) : 0;
        canvas.drawBitmap(bitmap, x, y, paint);

        bitmap = background;

        return this;
    }

    /**
     * Resizes images to fit within dimensions.
     *
     * @param width  width dimension
     * @param height width dimension
     * @param fit    fit or centerCrop
     * @return resized image
     */
    public Bitmap resize(int width, int height, boolean fit) {
        Log.d("PhotoEditor", "resizing photo with fit = " + fit);

        // incorrect params
        if (width <= 0 || height <= 0)
            return null;

        // get scale factor to match screen size
        float scale = getScale(width, height, fit);

        // create new resized bitmap
        return Bitmap.createScaledBitmap(bitmap,
                (int) (scale * bitmap.getWidth()),
                (int) (scale * bitmap.getHeight()),
                false);
    }

    /**
     * Gets scale factor to resize image to display size.
     *
     * @param width  width of display
     * @param height height of display
     * @param fit    fit or centerCrop
     * @return scale factor
     */
    public float getScale(int width, int height, boolean fit) {
        // incorrect params
        if (width <= 0 || height <= 0)
            return -1;

        // fit width if photo is more square than screen otherwise fit height
        float scale;
        if ((((double) bitmap.getHeight()) / bitmap.getWidth()) < (((double) height) / width))
            scale = fit ? (float) width / bitmap.getWidth() : (float) height / bitmap.getHeight();
        else
            scale = fit ? (float) height / bitmap.getHeight() : (float) width / bitmap.getWidth();
        return scale;
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
        if (location == null && photo.location == null)
            return this;

        Log.i("PhotoEditor", "Location appended to photo is " + ((photo.location == null) ? location : photo.location));
        // add location to bottom-left of photo
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((float) (canvas.getWidth() * 0.05));
        canvas.drawText((photo.location == null) ? location : photo.location,
                (float) (canvas.getWidth() * SIDE_MARGIN),
                (float) (canvas.getHeight() * BOTTOM_MARGIN),
                paint);

        return this;
    }
    /**
     * Adds karma of photo to bottom-right corner
     *
     * @return edited photo
     */
    public PhotoEditor appendKarma() {
        Log.i("PhotoEditor", "Karma appended to photo is " + photo.karma);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((float) (canvas.getWidth() * 0.05));
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Karma: " + photo.karma,
                (float) (canvas.getWidth() * (1 - 2*SIDE_MARGIN)),
                (float) (canvas.getHeight() * BOTTOM_MARGIN),
                paint);

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

        // null check
        if (string == null)
            return this;

        Canvas canvas = new Canvas(bitmap);

        // set text style
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((float) (canvas.getWidth() * 0.05));
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(string, (float) (canvas.getWidth() / 2), (float) (canvas.getHeight() / 2) - (paint.getTextSize() / 2), paint);

        return this;
    }

    /**
     * Get street address at specified longtiude and latitude
     *
     * @param context   current state of application
     * @param latitude  distance north or south of equator
     * @param longitude distance west or east of equator
     * @return address at specified longitude and latitude
     * @throws IOException
     */
    private String getAddress(Context context, double latitude, double longitude) throws IOException {
        List<Address> addresses = new Geocoder(context, Locale.getDefault()).getFromLocation(latitude, longitude, 1);
        return (addresses.isEmpty()) ? null : addresses.get(0).getAddressLine(0);
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
