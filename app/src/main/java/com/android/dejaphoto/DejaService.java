package com.android.dejaphoto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.maps.GeoApiContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
    public static final String copyAction = "COPY";
    public static final String addPhoto = "ADD";
    public static final String friend = "FRIEND";
    public static final String photoFile = "PHOTO";

    GeoApiContext geoContext;

    ImageController controller;
    PhotoQueue<Photo> queue;
    public Album dejaPhotoAlbum, dejaPhotoFriends, dejaPhotoCopied, dejaPhotoDCIM;
    private IBinder mBinder = new MyBinder();
    Uri data;
    String name;

    /**
     * Thread to execute widget action.
     */
    public class DejaThread implements Runnable {
        PhotoQueue<Photo> photoQueue;
        String action;
        String[] extra;

        /**
         * Default constructor.
         *
         * @param queue  queue to use
         * @param action action to perform
         */
        public DejaThread(PhotoQueue<Photo> queue, String action, String... extra) {
            //Get queue from outside
            photoQueue = queue;
            this.action = action;
            this.extra = extra;
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
                if (action.equals(addPhoto)) {
                    addPhoto(extra[0], extra[1]);
                }
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
            Photo photo = photoQueue.getCurrentPhoto();
            photo.setKarma();
            Intent serviceIntent = new Intent(getBaseContext(), FirebaseService.class);
            serviceIntent.putExtra(FirebaseService.ACTION, FirebaseService.UPDATE_KARMA);
            serviceIntent.putExtra(FirebaseService.PHOTO, photo.name);
            serviceIntent.putExtra(FirebaseService.KARMA, photo.karma);
            getBaseContext().startService(serviceIntent);
        }

        /**
         * Update release.
         */
        public void release() {
            Log.d("DejaService", "photo released bye bye");

            photoQueue.getCurrentPhoto().releasePhoto();
        }

        /**
         * Refresh screen.
         */
        public void refresh() {
            Log.d("DejaService", "refresh called");
            Context context = getApplicationContext();
            queue.getChooser().refresh(context);
            controller.displayImage(queue.next(getApplicationContext()));
        }

        /**
         * Add photo.
         */
        public void addPhoto(String friend, String photo) {
            Log.d("DejaService", "addPhoto called");
            Photo photo1 = new Photo(friend, photo, getBaseContext());
            PhotoChooser.photos.add(photo1);
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
            if (action.equals(copyAction)) {
                Log.e("DejaCopy", "Get extras (the file)");
                data = (Uri) intent.getExtras().get("File");
                name = (String) intent.getExtras().get("Name");
                runCopy();
            }
            if (action.equals(addPhoto))
                runAddPhoto(intent.getStringExtra(friend), intent.getStringExtra(photoFile));
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

        File appDirectory = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DejaPhoto");
        boolean appDirCreated;
        if (!appDirectory.exists()) {
            appDirCreated = appDirectory.mkdirs();
        }


        File dejaPhotoAlbumFile = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DejaPhoto" + File.separator + "DejaPhotoAlbum");
        if (!dejaPhotoAlbumFile.exists()) {
            dejaPhotoAlbumFile.mkdirs();
        }
        dejaPhotoAlbum = new Album(dejaPhotoAlbumFile, false);


        File dejaPhotoFriendsFile = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DejaPhoto" + File.separator + "DejaPhotoFriends");
        if (!dejaPhotoFriendsFile.exists()) {
            dejaPhotoFriendsFile.mkdirs();

        }
        dejaPhotoFriends = new Album(dejaPhotoAlbumFile);


        File dejaPhotoCopiedFile = new File(Environment.getExternalStorageDirectory() +
                File.separator + "DejaPhoto" + File.separator + "DejaPhotoCopied");
        if (!dejaPhotoCopiedFile.exists()) {
            dejaPhotoCopiedFile.mkdirs();
        }
        dejaPhotoCopied = new Album(dejaPhotoCopiedFile);

        File dejaPhotoDCIMFile = new File(Environment.DIRECTORY_DCIM + "/camera");

        dejaPhotoDCIM = new Album(dejaPhotoDCIMFile);

        GetAllPhotosFromGallery dejaGallery, friendsGallery, copiedGallery;

        //Get list of picture
        // Check whether to include albums
       /* if (dejaPhotoAlbum.includePhotos)
        {
            dejaGallery = new GetAllPhotosFromGallery(dejaPhotoAlbum.directoryFile, context, geoContext);
        }
        // To get code in photochooser line to work for now
        else
            dejaGallery = null;

        if (dejaPhotoFriends.includePhotos)
        {
            friendsGallery = new GetAllPhotosFromGallery(dejaPhotoFriends.directoryFile, context, geoContext);
        }
        if (dejaPhotoCopied.includePhotos)
        {
            copiedGallery = new GetAllPhotosFromGallery(dejaPhotoCopied.directoryFile, context, geoContext);
        }

        // TODO needs to change to support multiple albums
        // Album object has boolean for whether to include photos
        PhotoChooser chooser = new PhotoChooser(dejaGallery.getImages(), geoContext);
        queue = new PhotoQueue<>(chooser);
        controller.displayImage(queue.next(getApplicationContext()));*/


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

    /*
    * Start copy action.
    */
    public void runCopy() {
        Thread worker = new Thread(new DejaCopyThread(copyAction));
        worker.start();
    }

    /**
     * Start addPhoto action.
     */
    public void runAddPhoto(String friend, String photo) {
        Thread worker = new Thread(new DejaThread(queue, addPhoto, friend, photo));
        worker.start();
    }

    public class DejaCopyThread implements Runnable {
        String action;

        public DejaCopyThread(String action) {
            this.action = action;
        }

        public void run() {
            if (action.equals(copyAction))
                copyToAlbum();
        }

        public void copyToAlbum() {
            OutputStream out = null;
            try {
                File file = new File(dejaPhotoCopied.getFile() + name);
                file.createNewFile();
                out = new FileOutputStream(file);
                InputStream in = getContentResolver().openInputStream(data);
                int b = 0;
                while (b != -1) {
                    b = in.read();
                    out.write(b);
                }
                out.close();

                Intent serviceIntent = new Intent(getBaseContext(), FirebaseService.class);
                serviceIntent.putExtra(FirebaseService.ACTION, FirebaseService.ADD_PHOTO);
                serviceIntent.putExtra(FirebaseService.PHOTO, file.getName());
                serviceIntent.putExtra(FirebaseService.KARMA, 0);
                startService(serviceIntent);
            } catch (Exception e) {
                Log.e("DejaCopy", e.toString());
            }
        }
    }

}
