package com.android.dejaphoto;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Queue;

public class DejaService extends Service {
    ImageController controller;
    PhotoQueue<Photo> queue;

    public class DejaThread implements Runnable
    {
        Queue<Photo> photoQueue;
        public DejaThread(Queue<Photo> queue)
        {
            //Get queue from outside
            photoQueue = queue;
        }
        @Override
        public void run() {

        }

        public void next(Context context) {
            //get next photo
            Log.d("App Widget", "nextCalled");
            Photo nextPhoto = queue.next();

            // no photos in album
            if (nextPhoto == null) {
                Log.d("App Widget", "No Photo");
                Bitmap newBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
                System.out.println(newBitmap);
                nextPhoto = new Photo();
                nextPhoto.setPhoto(newBitmap);


            }
            controller.displayImage(nextPhoto);
        }

        public void previous(Context context) {
            //get previous photo
            Log.d("App Widget", "previousCalled");
            Photo previousPhoto = queue.previous();

            // no previous photo
            if (previousPhoto == null) {
                Log.d("App Widget", "No Photo");
                Bitmap newBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
                System.out.println(newBitmap);
                previousPhoto = new Photo();
                previousPhoto.setPhoto(newBitmap);
            }
            controller.displayImage(previousPhoto);
        }
    }

    public DejaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Context context = getApplicationContext();
        initialize(context);
        if(queue != null)
            Log.d("DejaService", "queue is NOT null");
        Log.d("DejaService", "onStart(): Initialized");
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        Log.d("DejaService", "onDestroy()");
    }


    //Helpers
    public void initialize(Context context) {
        //Create controller
        controller = new ImageController(context);

        //Get list of pictures
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
        GetAllPhotosFromGallery gallery = new GetAllPhotosFromGallery(file, context);

        PhotoChooser chooser = new PhotoChooser(gallery.getImages());
        queue = new PhotoQueue<>(chooser);
        Log.d("DejaService", "initialize()");
    }



    public void show(Context context) {
        Photo photo = new Photo();
        Bitmap newBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
        photo.setPhoto(newBitmap);
        controller.displayImage(photo);
    }


}
