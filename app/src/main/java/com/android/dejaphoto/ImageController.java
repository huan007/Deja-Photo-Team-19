package com.android.dejaphoto;

import android.app.Activity;
import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.dejaphoto.MainActivity;
import com.android.dejaphoto.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

/**
 * Created by Cyrax on 5/4/2017.
 * Image Controller Object takes charge of changing wallpaper pictures
 */

public class ImageController {

    Target target;
    Callback callback;
    Context currentContext;
    WallpaperManager wallpaperManager;

    public ImageController(Context context) {
        currentContext = context;
        wallpaperManager = WallpaperManager.getInstance(currentContext);
    }

    /**
     * Change the wallpaper
     */
    public void displayImage(Photo photo) {
        try {

            Display display = ((WindowManager) currentContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        System.out.println(bitmap.getWidth() + " " + bitmap.getHeight());
                        wallpaperManager.setBitmap(photo.appendLocation(currentContext, bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Picasso.with(currentContext)
                            .load(Photo.getImageUri(currentContext, photo.photo))
                            .resize(width, height)
                            .centerCrop()
                            .into(target);
                }
            };

            new Handler(currentContext.getMainLooper()).post(runnable);
            Log.d("ImageController", "Wallpaper Changed");
        } catch (Exception e) {
            Log.d("ImageController", "Failed to change wallpaper");
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
