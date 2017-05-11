package com.android.dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Geocoder;

import java.io.IOException;
import java.util.Locale;

/**
 * Modifies photos.
 */
public class PhotoEditor {
    private Photo photo;
    private Bitmap bitmap;

    /**
     * Hidden default constructor.
     */
    private PhotoEditor() {
    }

    /**
     * Constructor.
     *
     * @param photo  photo to modify
     * @param bitmap bitmap to modify
     */
    private PhotoEditor(Photo photo, Bitmap bitmap) {
        this.photo = photo;
        this.bitmap = bitmap;
    }

    /**
     * Create a PhotoEditor object.
     *
     * @param photo  photo to modify
     * @param bitmap bitmap to modify
     * @return new PhotoEditor object
     */
    public static PhotoEditor open(Photo photo, Bitmap bitmap) {
        return new PhotoEditor(photo, bitmap);
    }

    /**
     * Fits a whole bitmap into the dimensions of the screen.
     *
     * @param width  screen width
     * @param height screen height
     * @return bitmap fitted into black canvas
     */
    public PhotoEditor fitScreen(int width, int height) {
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
        int x = ((bitmap.getHeight() / bitmap.getWidth()) < (height / width)) ? 0 : (width / 2) - (bitmap.getWidth() / 2);
        int y = ((bitmap.getHeight() / bitmap.getWidth()) < (height / width)) ? (height / 2) - (bitmap.getHeight() / 2) : 0;
        canvas.drawBitmap(bitmap, x, y, paint);

        bitmap = background;

        return this;
    }

    /**
     * Adds location to bottom-left corner of bitmap.
     *
     * @param context current context
     * @return bitmap with location
     * @throws IOException on fail to get address
     */
    public PhotoEditor appendLocation(Context context) throws IOException {

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
     * Returns address of location.
     *
     * @param context   current context
     * @param latitude  current latitude
     * @param longitude current longitude
     * @return address of location
     * @throws IOException on fail to get address
     */
    private String getAddress(Context context, double latitude, double longitude) throws IOException {
        return new Geocoder(context, Locale.getDefault())
                .getFromLocation(latitude, longitude, 1)
                .get(0)
                .getAddressLine(0);
    }

    /**
     * Return current bitmap.
     *
     * @return current bitmap
     */
    public Bitmap finish() {
        return bitmap;
    }
}
