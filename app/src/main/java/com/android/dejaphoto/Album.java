package com.android.dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Anna on 5/26/2017.
 */

public class Album extends AbstractAlbum {

    List<Photo> images = null;

    // By default we're setting includePhotos to true, but this should ultimately be based on user settings
    Album()
    {
        directoryFile = null;
        includePhotos = true;
    }

    Album(File file, boolean value)
    {
        directoryFile = file;
        includePhotos = value;
    }

    Album(File file)
    {
        directoryFile = file;
        includePhotos = true;
    }

    // Creates a Photo object for each file in the directory and returns a list of them
    public void populatePhotos(Context context, GeoApiContext geoContext)
    {
        images = new ArrayList<Photo>();

        if(directoryFile.isDirectory() == true) {
            for (File file : directoryFile.listFiles()) {
                if (file.isFile()) {
                    try {
                        // Get bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), Uri.fromFile(file));

                        // Create Photo object
                        Photo photo = new Photo();
                        photo.photo = bitmap;

                        // Get exif info
                        ExifInterface exifInterface = (ExifInterface) new ExifInterface( file.getAbsolutePath() );
                        photo.datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                        photo.latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                        photo.longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);


                        if (photo.latitude != null && photo.longitude != null) {
                            //Parse the sign of lat and long
                            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                            String longRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                            boolean isNorth = false;
                            boolean isEast = false;

                            if (latRef.equals("N"))
                                isNorth = true;
                            if (longRef.equals("E"))
                                isEast = true;

                            double lat = convertDMStoDouble(photo.latitude, isNorth);
                            double longtitude = convertDMStoDouble(photo.longitude, isEast);

                            //Update lat and long
                            photo.latitude = String.valueOf(lat);
                            photo.longitude = String.valueOf(longtitude);
                            LatLng location = new LatLng(lat, longtitude);
                            photo.setZipCode(geoContext, location);
                        }
                        else
                            photo.setZipCode(geoContext, null);


                        images.add(photo);

                    } catch (Exception e) {
                        // Log error
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public List<Photo> getImages()
    {
        return images;
    }

    // Converts given DMS to Double
    public static double convertDMStoDouble(String raw, boolean isPositive)
    {
        Scanner rawScanner = new Scanner(raw).useDelimiter(",");
        double reverseDMS[] = new double[3];
        Log.d("Get Photo", "Raw: " + raw);

        //Get DMS individually
        int i = 0;
        while (rawScanner.hasNext())
        {
            String individual = rawScanner.next();
            Log.d("Get Photo", individual);
            Scanner individualScanner = new Scanner(individual).useDelimiter("/");
            Double value1 = new Double(individualScanner.next());
            Double value2 = new Double(individualScanner.next());
            reverseDMS[i]  = value1/value2;
            i++;
        }
        double result = reverseDMS[0] + (reverseDMS[1]/60) + (reverseDMS[2]/3600);
        if (!isPositive)
        {
            result = 0 - result;
        }
        Log.d("Get Photo", String.valueOf(result));
        return result;
    }
}
