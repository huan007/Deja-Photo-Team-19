package com.android.dejaphoto;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

/**
 * Created by Cyrax on 5/4/2017.
 * Image Controller Object takes charge of changing wallpaper pictures
 */

public class ImageController {

    Target target;
    Context currentContext;
    WallpaperManager wallpaperManager;

    public ImageController(Context context) {
        currentContext = context;
        wallpaperManager = WallpaperManager.getInstance(currentContext);
    }

    /**
     * Change the wallpaper
     */
    /**
     * Change the wallpaper.
     *
     * @param photo new wallpaper photo
     */
    public void displayImage(Photo photo) {
        // get screen dimensions
        Display display = ((WindowManager) currentContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // set wallpaper when image done loading
        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    Log.d("ImageController", "Wallpaper Changed");
                    wallpaperManager.setBitmap(PhotoEditor.open(photo, bitmap)
                            .fitScreen(width, height)
                            .appendLocation(currentContext)
                            .finish());
                } catch (IOException e) {
                    Log.d("ImageController", "Wallpaper Not Changed");
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("ImageController", "Wallpaper Not Changed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        // resize image on main thread
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Picasso.with(currentContext)
                        .load(photo.getPhotoUri(currentContext))
                        .resize(width, height)
                        .centerInside()
                        .into(target);
            }
        };

        new Handler(currentContext.getMainLooper()).post(runnable);
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
