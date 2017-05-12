package com.android.dejaphoto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

public class DejaService extends Service {
    public static final String nextAction = "NEXT";
    public static final String previousAction = "PREVIOUS";
    public static final String actionFlag = "ACTION_FLAG";

    ImageController controller;
    PhotoQueue<Photo> queue;
    private IBinder mBinder = new MyBinder();


    public class DejaThread implements Runnable {
        PhotoQueue<Photo> photoQueue;
        String action;


        public DejaThread(PhotoQueue<Photo> queue, String action) {
            //Get queue from outside
            photoQueue = queue;
            this.action = action;
        }

        @Override
        public void run() {
            //Only run if the queue is there. Prevent null pointer
            if (queue != null) {
                if (action.equals(nextAction))
                    next();
                if (action.equals(previousAction))
                    previous();
            }
        }

        public void next() {
            //get next photo
            Log.d("App Widget", "nextCalled");
            Photo nextPhoto = queue.next();
            controller.displayImage(nextPhoto);
        }

        public void previous() {
            //get previous photo
            Log.d("App Widget", "previousCalled");
            Photo previousPhoto = queue.previous();

            // no previous photo
            if (previousPhoto == null)
                Log.d("App Widget", "No Photo");
            else
                controller.displayImage(previousPhoto);
        }
    }

    public class MyBinder extends Binder {
        public DejaService getService() {
            return DejaService.this;
        }
    }

    public DejaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        Context context = getApplicationContext();
        initialize(context);
        if (queue != null)
            Log.d("DejaService", "queue is NOT null");
        Log.d("DejaService", "onCreate(): Initialized");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Deciding whether to run next or previous
        Log.d("DejaService", "onStart()");
        String action = intent.getStringExtra(actionFlag);
        if (action != null) {
            Log.d("DejaService", "Action received: " + action);
            if (action.equals(nextAction))
                runNext();
            if (action.equals(previousAction))
                runPrevious();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
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

    public void runNext() {
        Thread worker = new Thread(new DejaThread(queue, nextAction));
        worker.start();
    }

    public void runPrevious() {
        Thread worker = new Thread(new DejaThread(queue, previousAction));
        worker.start();
    }

    /*
    public void show(Context context) {
        Photo photo = new Photo();
        Bitmap newBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
        photo.setPhoto(newBitmap);
        controller.displayImage(photo);
    }*/


}
