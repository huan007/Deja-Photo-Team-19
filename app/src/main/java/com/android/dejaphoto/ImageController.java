package com.android.dejaphoto;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;

/**
 * Image Controller Object takes charge of changing wallpaper pictures.
 */
public class ImageController {

    Context currentContext;
    WallpaperManager wallpaperManager;

    /**
     * Default constructor.
     *
     * @param context current context
     */
    public ImageController(Context context) {
        currentContext = context;
        wallpaperManager = WallpaperManager.getInstance(currentContext);
    }

    /**
     * Change the wallpaper to specified photo.
     */
    public void displayImage(Photo photo) {
        // get size of display
        final Point size = new Point();
        ((WindowManager) currentContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);

        final boolean default_background = (photo == null);
        final Photo background = (default_background ? new Photo() : photo);

        // prep and resize the photo using Glide image library
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("ImageController", "changing wallpaper");
                Glide.with(currentContext)
                        .load(default_background ? R.drawable.apple : background.getImageUri(currentContext))   // load background
                        .asBitmap()                                                                             // as bitmap
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                try {
                                    wallpaperManager.setBitmap(PhotoEditor.start(background, resource)          // modify bitmap
                                            .fitScreen(size.x, size.y, !default_background)                     // fit to screen
                                            .appendLocation(currentContext)                                     // add location
                                            .addText((default_background) ? "No Photos in Gallery" : "")        // add error
                                            .finish());                                                         // set wallpaper
                                    Log.d("ImageController", "change wallpaper successful");
                                } catch (IOException e) {
                                    Log.d("ImageController", "change wallpaper unsuccessful");
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        };

        // set wallpaper using the main thread
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
