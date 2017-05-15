package com.android.dejaphoto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.maps.GeoApiContext;

import java.io.File;

/**
 * Long running service that updates photos according to widget button clicks.
 */
public class DejaService extends Service {
    public static final String nextAction = "NEXT";
    public static final String previousAction = "PREVIOUS";
    public static final String refreshAction = "REFRESH";
    public static final String releaseAction = "RELEASE";
    public static final String actionFlag = "ACTION_FLAG";
    public static final String karmaAction = "KARMA";

    GeoApiContext geoContext;

    ImageController controller;
    PhotoQueue<Photo> queue;
    private IBinder mBinder = new MyBinder();

    /**
     * Thread to execute widget action.
     */
    public class DejaThread implements Runnable {
        PhotoQueue<Photo> photoQueue;
        String action;

        /**
         * Default constructor.
         *
         * @param queue  queue to use
         * @param action action to perform
         */
        public DejaThread(PhotoQueue<Photo> queue, String action) {
            //Get queue from outside
            photoQueue = queue;
            this.action = action;
        }

        /**
         * Chooses action to run.
         */
        @Override
        public void run() {
            //Only run if the queue is there. Prevent null pointer
            if (queue != null) {
                if (action.equals(nextAction))
                    next();
                if (action.equals(previousAction))
                    previous();
                if (action.equals(karmaAction))
                    karma();
                if (action.equals(releaseAction))
                    release();
                if (action.equals(refreshAction))
                    refresh();
            }
        }

        /**
         * Updates wallpaper with next photo.
         */
        public void next() {
            //get next photo
            Log.d("DejaService", "next called");
            controller.displayImage(queue.next(getApplicationContext()));
        }

        /**
         * Updates wallpaper with previous photo.
         */
        public void previous() {
            //get previous photo
            Log.d("DejaService", "previous called");
            Photo previousPhoto = queue.previous();

            // no previous photo
            if (previousPhoto == null)
                Log.d("DejaService", "no previous photo");
            else
                controller.displayImage(previousPhoto);
        }

        /**
         * Update karma.
         */
        public void karma() {
            Log.d("DejaService", "karma received");

            photoQueue.getCurrentPhoto().setKarma();
        }

        /**
         * Update release.
         */
        public void release() {
            Log.d("DejaService", "photo released bye bye");

            photoQueue.getCurrentPhoto().releasePhoto();
        }

        /**
         * Refresh screen
         */
        public void refresh() {
            Log.d("DejaService", "refresh called");
            Context context = getApplicationContext();
            queue.getChooser().refresh(context);
            controller.displayImage(queue.next(getApplicationContext()));
        }
    }

    /**
     * Returns corresponding DejaService.
     */
    public class MyBinder extends Binder {
        public DejaService getService() {
            return DejaService.this;
        }
    }

    /**
     * Default constructor.
     */
    public DejaService() {
    }

    /**
     * Returns binder.
     *
     * @param intent current intent
     * @return binder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * What to do on service start.
     */
    @Override
    public void onCreate() {
        Context context = getApplicationContext();
        initialize(context);
        if (queue != null)
            Log.d("DejaService", "queue is NOT null");
        Log.d("DejaService", "onCreate(): Initialized");
        super.onCreate();
    }

    /**
     * Parses action command.
     *
     * @param intent  current intent
     * @param flags   flags
     * @param startId action to start
     * @return int
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Deciding whether to run next or previous
        Log.d("DejaService", "onStart()");
        String action = intent.getStringExtra(actionFlag);

        // starts corresponding action
        if (action != null) {
            Log.d("DejaService", "Action received: " + action);
            if (action.equals(nextAction))
                runNext();
            if (action.equals(previousAction))
                runPrevious();
            if (action.equals(karmaAction))
                runKarma();
            if (action.equals(releaseAction))
                runRelease();
            if (action.equals(refreshAction))
                runRefresh();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * What to do on service destruction.
     */
    @Override
    public void onDestroy() {
        Log.d("DejaService", "onDestroy()");
    }

    /**
     * Initializes all fields.
     *
     * @param context current context
     */
    public void initialize(Context context) {
        Log.d("DejaService", "initialize()");

        //Create controller
        controller = new ImageController(context);
        //Get GeoAPI context
        geoContext = new GeoApiContext().setApiKey("AIzaSyBHsv-_IdOMfhpCpOoLRgOi9TrlzcI7PsM");

        //Get list of pictures
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
        GetAllPhotosFromGallery gallery = new GetAllPhotosFromGallery(file, context, geoContext);

        PhotoChooser chooser = new PhotoChooser(gallery.getImages(), geoContext);
        queue = new PhotoQueue<>(chooser);
        controller.displayImage(queue.next(getApplicationContext()));
    }

    /**
     * Start next action.
     */
    public void runNext() {
        Thread worker = new Thread(new DejaThread(queue, nextAction));
        worker.start();
    }

    /**
     * Start previous action.
     */
    public void runPrevious() {
        Thread worker = new Thread(new DejaThread(queue, previousAction));
        worker.start();
    }

    /**
     * Start karma action.
     */
    public void runKarma() {
        Thread worker = new Thread(new DejaThread(queue, karmaAction));
        worker.start();
    }

    /**
     * Start release action.
     */
    public void runRelease() {
        Thread worker = new Thread(new DejaThread(queue, releaseAction));
        worker.start();
    }

    /**
     * Start refresh action.
     */
    public void runRefresh() {
        Thread worker = new Thread(new DejaThread(queue, refreshAction));
        worker.start();
    }

}
