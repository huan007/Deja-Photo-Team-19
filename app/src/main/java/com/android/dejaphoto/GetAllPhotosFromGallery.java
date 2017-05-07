package com.android.dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phillip on 5/3/17.
 */

public class GetAllPhotosFromGallery {
    File directory;
    List<Photo> images;

    GetAllPhotosFromGallery() {
        directory = null;
        images = null;
    }

    GetAllPhotosFromGallery(File directoryName, Context context) {
        directory = directoryName;

        images = new ArrayList<Photo>();

            if(directory.isDirectory() == true) {

                for (File file : directory.listFiles()) {


                if (file.isFile()) {
                    try {
                        // Get bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), Uri.fromFile(file));

                        Photo photo = new Photo();
                        photo.photo = bitmap;

                        // Get exif info
                        ExifInterface exifInterface = (ExifInterface) new ExifInterface( file.getAbsolutePath() );
                        photo.datetime = exifInterface.getAttribute(android.media.ExifInterface.TAG_DATETIME);
                        photo.latitude = exifInterface.getAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE);
                        photo.longitude = exifInterface.getAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE);


                        images.add(photo);

                    } catch (Exception e) {
                        // Not handled well
                    }
                }


                }
            }

    }
}
