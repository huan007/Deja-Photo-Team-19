package com.android.dejaphoto;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.dejaphoto.MainActivity;
import com.android.dejaphoto.R;

import java.io.IOException;

/**
 * Created by Cyrax on 5/4/2017.
 * Image Controller Object takes charge of changing wallpaper pictures
 */

public class ImageController {

    Context currentContext;
    WallpaperManager wallpaperManager;

    public ImageController(Context context)
    {
        currentContext = context;
        wallpaperManager = WallpaperManager.getInstance(currentContext);
    }

    /**
     * Change the wallpaper
     */
    public void displayImage(Photo photo){


        try{
            wallpaperManager.setBitmap(photo.photo);
            Log.d("ImageController", "Wallpaper Changed");
        }
        catch (IOException e)
        {
            Log.d("ImageController", "Failed to change wallpaper");
            e.printStackTrace();
        }
    }

    /** Get current wallpaper as a drawable. Used for testing
     *
     * @return current wallpaper
     */
    public Drawable getImage()
    {
        wallpaperManager = WallpaperManager.getInstance(currentContext);
        return wallpaperManager.getDrawable();
    }
}
