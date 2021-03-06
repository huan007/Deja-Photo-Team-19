package com.android.dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Phillip on 5/3/17.
 */


/**
 * Class Photo objects represent a single photo from the album, along with information about that
 * photo. For example, they can return information about the photo's location and time, as well as
 * a bitmap representation of the photo.
 */

public class Photo {

    String name;
    String location;
    String datetime;
    String latitude;
    String longitude;
    double distanceFromOrigin;

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(GeoApiContext context, LatLng location) {
        if (location != null) {
            //Begin to parse location into zipcode string
            try {
                GeocodingResult results[] = GeocodingApi.reverseGeocode(context, location).await();
                AddressComponent[] components = results[0].addressComponents;
                int i = 0;
                for (AddressComponent component : components) {//Find the zip
                    //Get the types of the component
                    AddressComponentType[] types = component.types;
                    for (AddressComponentType type : types) {//If the component is the zip code, then make it the zip code
                        if (type.equals(AddressComponentType.POSTAL_CODE))
                            zipCode = results[0].addressComponents[i].longName;
                    }
                    i++;
                }

            } catch (ApiException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else zipCode = null;
    }

    String zipCode;
    Bitmap photo;
    int karma;
    boolean karmaSet;
    boolean release;
    double weight;

    public Photo() {
        location = null;
        karma = 0;
        karmaSet = false;
    }

    // Release photo
    public void releasePhoto() {
        location = null;
        release = true;
    }

    // Sets karma value
    public void setKarma() {
        if (!karmaSet) {
            ++karma;
            karmaSet = true;
        }
    }

    // Returns the location value for the photo
    public String getLocation() {
        return location;
    }

    // Sets location value of the photo
    public void setLocation(String location) {
        this.location = location;
    }

    // Returns the date/time the photo was taken
    public String getDatetime() {
        return datetime;
    }

    // Returns the day of the week the photo was taken
    public String getDayOfTheWeek() {
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
        } else {
            return null;
        }
    }

    // Returns the hour the photo was taken
    public String getHour() {
        if (datetime != null) {
            SimpleDateFormat rawFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            SimpleDateFormat hourFormat = new SimpleDateFormat("kk");
            try {
                Date rawDate = rawFormat.parse(datetime);
                String hour = hourFormat.format(rawDate);
                return hour;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else return null;
    }


    public long getRecency() {
        if (datetime != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            try {
                Date rawDate = dateFormat.parse(datetime);
                long unixTime = (long) rawDate.getTime() / 1000;
                return unixTime;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }

        } else
            return 0;
    }

    // Sets date time for photo
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    // Returns latitude for photo
    public String getLatitude() {
        return latitude;
    }

    // Set latitude value for photo
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    // Returns longitude value for photo
    public String getLongitude() {
        return longitude;
    }

    // Sets longitude value for photo
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    // Returns a bitmap representation of the photo
    public Bitmap getPhoto() {
        return photo;
    }

    // Sets photo value
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    // Returns a URI for the photo
    public Uri getImageUri(Context inContext) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    public Photo(final String friend, final String name, final Context context) {
        GeoApiContext geoContext = geoContext = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");

        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DejaPhoto" + File.separator + "DejaPhotoFriends", name);

        if (file.exists()) {
            try {
                // Get bitmap
                Bitmap bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), Uri.fromFile(file));

                // Create Photo object
                photo = bitmap;
                this.name = name;
                // update karma to firebase data
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child(FirebaseService.validateName(friend)).child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            if (snapshot.getKey().equals(FirebaseService.validateName(name)))
                                karma = (int) ((long) snapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


                // Get exif info
                ExifInterface exifInterface = null;
                exifInterface = (ExifInterface) new ExifInterface(file.getAbsolutePath());
                datetime = exifInterface.getAttribute(android.media.ExifInterface.TAG_DATETIME);
                latitude = exifInterface.getAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE);
                longitude = exifInterface.getAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE);


                if (latitude != null && longitude != null) {
                    //Parse the sign of lat and long
                    String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                    String longRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                    boolean isNorth = false;
                    boolean isEast = false;

                    if (latRef.equals("N"))
                        isNorth = true;
                    if (longRef.equals("E"))
                        isEast = true;

                    double lat = Album.convertDMStoDouble(latitude, isNorth);
                    double longtitude = Album.convertDMStoDouble(longitude, isEast);

                    //Update lat and long
                    latitude = String.valueOf(lat);
                    longitude = String.valueOf(longtitude);
                    LatLng location = new LatLng(lat, longtitude);
                    setZipCode(geoContext, location);
                } else
                    setZipCode(geoContext, null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
