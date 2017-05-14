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

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Phillip on 5/3/17.
 */

public class Photo {
    String location;
    String datetime;
    String latitude;
    String longitude;

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(GeoApiContext context, LatLng location) {
        if (location != null)
        {
            //Begin to parse location into zipcode string
            try {
                GeocodingResult results[] = GeocodingApi.reverseGeocode(context, location).await();
                int numOfComponents = results[0].addressComponents.length;
                zipCode = results[0].addressComponents[numOfComponents-1].longName;
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else zipCode = null;
    }

    String zipCode;
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

    public String getDayOfTheWeek()
    {
        if (datetime != null) {
            SimpleDateFormat rawFormat = new SimpleDateFormat("yyyy:MM:dd");
            SimpleDateFormat dowFormat = new SimpleDateFormat("EEEE");

            try {
                Date rawDate = rawFormat.parse(datetime);
                String newDate = dowFormat.format(rawDate);
                return newDate;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        else return null;
    }

    public String getHour()
    {
        if (datetime != null)
        {
            SimpleDateFormat rawFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            SimpleDateFormat hourFormat = new SimpleDateFormat("kk");
            try
            {
                Date rawDate = rawFormat.parse(datetime);
                String hour = hourFormat.format(rawDate);
                return hour;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else return null;
    }


    public long getRecency() {
        if (datetime != null)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
            try {
                Date rawDate = dateFormat.parse(datetime);
                long unixTime = (long) rawDate.getTime()/1000;
                return unixTime;
            } catch (ParseException e)
            {
                e.printStackTrace();
                return 0;
            }

        } else
            return 0;
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

    public Uri getImageUri(Context inContext) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }



}
