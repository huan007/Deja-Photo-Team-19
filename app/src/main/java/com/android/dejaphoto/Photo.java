package com.android.dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Phillip on 5/3/17.
 */

public class Photo {
    String location;
    String datetime;
    String latitude;
    String longitude;
    Bitmap photo;
    boolean karma;
    boolean release;
    int weight;

    // Release photo
    public void releasePhoto(){ release = true; }

    public void setKarma(){ karma = true; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    // Returns a photo with location in bottom-left corner
    public Bitmap appendLocation(Context context, Bitmap bitmap) throws IOException {

        // if no location data return normal photo
        if (latitude == null || latitude.length() == 0 || longitude == null || longitude.length() == 0)
            return bitmap;

        // get address from location data
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address address = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1).get(0);
        String location = address.getAddressLine(0);

        // if no such location return normal photo
        if (location == null)
            return bitmap;

        // add location to bottom-left of photo
        Bitmap edit = Bitmap.createBitmap(bitmap);
        Canvas canvas = new Canvas(edit);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((float) (canvas.getWidth() * 0.05));
        canvas.drawText(location, (float) (canvas.getWidth() * 0.1), (float) (canvas.getHeight() * 0.8), paint);

        return edit;
    }

    public Uri getImageUri(Context inContext) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

}
