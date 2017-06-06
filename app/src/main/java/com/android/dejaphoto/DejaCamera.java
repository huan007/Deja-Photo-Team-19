package com.android.dejaphoto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class DejaCamera {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static Context context;
    private static int imageCountBefore;

    /**
     * Start the Deja Camera activity.
     */
    public static void startCamera(PreferenceFragment fragment, Context context) {
        DejaCamera.context = context;

        //current images in mediaStore
        Cursor cursor = loadCursor();
        imageCountBefore = cursor.getCount();
        cursor.close();

        // start camera activity
        Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(cameraIntent, 0);
        if (activities.size() > 0)
            fragment.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        else
            Log.d("camera activity", "no camera app");
    }

    /**
     * On camera close, get all photos taken.
     */
    public static void exitCamera() {
        Cursor cursor = loadCursor();
        List<String> paths = getImagePaths(cursor, imageCountBefore);   // get the paths to newly added images
        if (paths != null)
            process(paths);                                             // process images
        cursor.close();
    }

    /**
     * Returns cursor pointing to last photo taken.
     *
     * @return cursor pointing to last photo taken
     */
    private static Cursor loadCursor() {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        String orderBy = MediaStore.Images.Media.DATE_ADDED;
        return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
    }

    /**
     * Returns paths of images taken.
     *
     * @param cursor        cursor pointing to photos taken
     * @param startPosition most recent photo taken
     * @return paths of images taken
     */
    private static List<String> getImagePaths(Cursor cursor, int startPosition) {
        int size = cursor.getCount() - startPosition;

        if (size <= 0) return null;

        Log.d("deja camera", "getting photos");

        List<String> paths = new ArrayList<>();
        int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

        for (int i = startPosition; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            paths.add(cursor.getString(dataColumnIndex));
        }

        return paths;
    }

    /**
     * Add photos taken to DejaPhoto album
     *
     * @param paths list of photos taken
     */
    private static void process(List<String> paths) {
        Log.d("deja camera", "moving photos");
        for (String path : paths) {
            File from = new File(path);
            File to = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "DejaPhoto"+ File.separator + "DejaPhotoAlbum", from.getName());
            to.delete();
            from.renameTo(to);

            Intent serviceIntent = new Intent(context, FirebaseService.class);
            serviceIntent.putExtra(FirebaseService.ACTION, FirebaseService.ADD_PHOTO);
            serviceIntent.putExtra(FirebaseService.PHOTO, from.getName());
            serviceIntent.putExtra(FirebaseService.KARMA, 0);
            context.startService(serviceIntent);
        }
    }
}
