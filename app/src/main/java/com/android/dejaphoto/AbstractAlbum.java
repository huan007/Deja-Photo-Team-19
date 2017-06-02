package com.android.dejaphoto;

import java.io.File;

/**
 * Created by Anna on 5/26/2017.
 */

public abstract class AbstractAlbum
{
    File directoryFile;
    boolean includePhotos; // should be set based on user settings

    public File getFile()
    {
        return directoryFile;
    }

    public void setFile(File file)
    {
        directoryFile = file;
    }

    public boolean getIncludePhotos()
    {
        return includePhotos;
    }

    public void setIncludePhotos(boolean value)
    {
        includePhotos = value;
    }

}