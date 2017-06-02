package com.android.dejaphoto;

import java.io.File;

/**
 * Created by Anna on 5/26/2017.
 */

public class Album extends AbstractAlbum {

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

}
