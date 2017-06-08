package com.android.dejaphoto;

import android.os.Environment;

import java.io.File;

/**
 * Created by Anna on 5/26/2017.
 */

public class Album extends AbstractAlbum {

    // By default we're setting includePhotos to true, but this should ultimately be based on user settings

    Album() {
        directoryFile = null;
        includePhotos = true;
    }

    Album(File file, boolean value) {
        directoryFile = file;
        includePhotos = value;
    }

    Album(File file) {
        directoryFile = file;
        includePhotos = true;
    }

    public File findMyPhoto(String photo) {
        File file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DejaPhoto" + File.separator + "DejaPhotoAlbum", photo);

        if (file.exists())
            return file;

        file = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DejaPhoto" + File.separator + "DejaPhotoCopied", photo);

        if (file.exists())
            return file;

        return null;
    }

}
