package com.android.dejaphoto;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by Cyrax on 5/4/2017.
 * Image Controller Object takes charge of changing wallpaper pictures
 */

public class ImageController {

    Context currentContext;
    WallpaperManager wallpaperManager;

    public ImageController(Context context) {
        currentContext = context;
        wallpaperManager = WallpaperManager.getInstance(currentContext);
    }

    /**
     * Change the wallpaper to specified photo.
     */
    public void displayImage(Photo photo) {
        Point size = new Point();
        ((WindowManager) currentContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);

        boolean default_background = (photo == null);
        System.out.println(photo == null);

        // create default photo if photo is null
        if (default_background) {
            Log.d("ImageController", "create default wallpaper");
            photo = new Photo();
            photo.photo = BitmapFactory.decodeResource(currentContext.getResources(), R.drawable.apple);
        }

        // set wallpaper
        try {
            Log.d("ImageController", "change wallpaper successful");
            wallpaperManager.setBitmap(PhotoEditor.start(photo)
                    .fitScreen(size.x, size.y, !default_background)
                    .appendLocation(currentContext)
                    .addText(default_background ? "No Photos in Gallery" : "")
                    .finish());
        } catch (IOException e) {
            Log.d("ImageController", "change wallpaper unsuccessful");
            e.printStackTrace();
        }
    }

    /**
     * Get current wallpaper as a drawable. Used for testing
     *
     * @return current wallpaper
     */
    public Drawable getImage() {
        wallpaperManager = WallpaperManager.getInstance(currentContext);
        return wallpaperManager.getDrawable();
    }
}
