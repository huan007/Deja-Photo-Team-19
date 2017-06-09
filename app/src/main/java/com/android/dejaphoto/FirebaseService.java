package com.android.dejaphoto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Long runnning service that handles syncing with Firebase.
 */
public class FirebaseService extends Service {

    public static final String ACTION = "ACTION";
    public static final String FRIEND = "FRIEND";
    public static final String PHOTO = "PHOTO";
    public static final String KARMA = "KARMA";

    public static final int ADD_FRIEND = 1;
    public static final int ADD_PHOTO = 2;
    public static final int UPDATE_KARMA = 3;
    public static final int REMOVE_PHOTOS = 4;


    private static final String PHOTOS = "photos";
    private static final String NULL = "null";
    private static final String FRIENDS = "friends";

    private String user;
    private boolean karmaChange;

    private Map<String, ValueEventListener> listeners;

    private DatabaseReference database;
    private FirebaseStorageAdapterInterface storage;

    /**
     * Removes all invalid characters from email.
     *
     * @param s user email
     * @return new ID
     */
    public static String validateName(String s) {
        return s.replace('.', '~');
    }

    public static String unvalidateName(String s) {
        return s.replace('~', '.');
    }

    /**
     * Makes new user in Firebase database.
     *
     * @param user current user
     */
    public static void makeUser(final String user) {
        if (user == null)
            return;

        // add empty photo to new user
        final Map<String, Object> photos = new HashMap<>();
        photos.put(NULL, NULL);
        FirebaseDatabase.getInstance().getReference().child(user).child(PHOTOS).updateChildren(photos);
    }

    public FirebaseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // initialize Firebase interfaces
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorageAdapter.getInstance();

        user = getSharedPreferences("user", MODE_PRIVATE).getString("email", null);
        karmaChange = true;

        if (user == null)
            return;

        listeners = new HashMap<>();

        // add listeners for current friends
        Log.d("Firebase Service", "adding listener for current friends");
        database.child(user).child(FRIENDS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    listeners.put(snapshot.getKey(), listenForPhotos(snapshot.getKey()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // listen for new friends
        Log.d("Firebase Service", "adding listener for new friends");
        database.child(user).child(FRIENDS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // find and listen to new friends
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    if (!listeners.containsKey(snapshot.getKey()))
                        listeners.put(snapshot.getKey(), listenForPhotos(snapshot.getKey()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // listen for photo changes
        Log.d("Firebase Service", "adding listener for own photo changes");
        database.child(user).child(PHOTOS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (karmaChange) {
                    // check for difference in photos
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren())
                        if (!snapshot.getKey().equals(NULL))
                            try {
                                Photo photo = PhotoChooser.getPhoto(snapshot.getKey());
                                if ((int) ((long) snapshot.getValue()) != photo.karma)
                                    photo.karma = (int) ((long) snapshot.getValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                } else {
                    karmaChange = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return super.onStartCommand(intent, flags, startId);

        // run action
        Log.d("Firebase Service", "starting action" + intent.getIntExtra(ACTION, -1));
        switch (intent.getIntExtra(ACTION, -1)) {
            case ADD_FRIEND:
                addFriend(intent.getStringExtra(FRIEND));
                break;
            case ADD_PHOTO:
                try {
                    addPhoto(intent.getStringExtra(PHOTO), intent.getIntExtra(KARMA, 0));
                } catch (Exception e) {
                    addPhoto(intent.getStringExtra(PHOTO), 0);
                }
                break;
            case UPDATE_KARMA:
                updateKarma(intent.getStringExtra(PHOTO), intent.getIntExtra(KARMA, 0));
                break;
            case REMOVE_PHOTOS:
                removePhotos();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Add friend between users.
     *
     * @param friend friend to add
     */
    private void addFriend(String friend) {
        if (friend == null)
            return;

        // add friend to user
        Log.d("Firebase Service", "add friend to user");
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put(friend, NULL);
        database.child(user).child(FRIENDS).updateChildren(friendMap);

        listeners.put(friend, listenForPhotos(friend));

        // add user to friend
        Log.d("Firebase Service", "add user to friend");
        friendMap = new HashMap<>();
        friendMap.put(user, NULL);
        database.child(friend).child(FRIENDS).updateChildren(friendMap);
    }

    /**
     * Add new photo.
     *
     * @param photo
     * @param karma
     */
    private void addPhoto(String photo, int karma) {
        if (photo == null)
            return;

        // add photo to database
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("share", true)) {
            Log.d("Firebase Service", "add photo to Firebase");
            Map<String, Object> photoMap = new HashMap<>();
            photoMap.put(validateName(photo), Integer.valueOf(karma));
            database.child(user).child(PHOTOS).updateChildren(photoMap);
            storage.uploadPhotoFile(new Album().findMyPhoto(photo));
            System.out.println(photo + " " + new Album().findMyPhoto(photo));
            karmaChange = false;
        }
    }

    /**
     * Updates friend karma.
     */
    private void updateKarma(String photo, final int karma) {
        if (photo == null)
            return;

        final String fPhoto = validateName(photo);

        Log.d("Firebase Service", "update akrma");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot friend : dataSnapshot.getChildren())
                    for (DataSnapshot photos : friend.child(PHOTOS).getChildren())
                        try {
                            if (photos.getKey().equals(fPhoto))
                                database.child(friend.getKey()).child(PHOTOS).child(fPhoto).setValue(karma);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Removes all photos from user.
     */
    private void removePhotos() {
        // remove photo and add default null value
        Log.d("Firebase Service", "remove all photos from Firebase");
        database.child(user).child(PHOTOS).removeValue();
        final Map<String, Object> photos = new HashMap<>();
        photos.put(NULL, NULL);
        database.child(user).child(PHOTOS).updateChildren(photos);
    }

    /**
     * Listen to changes in friends' photos.
     *
     * @param friend friend to listen to
     * @return friends' photo listener
     */
    private ValueEventListener listenForPhotos(final String friend) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if friend has photos then check for difference
                // else remove all friends' photos from user storage
                if (dataSnapshot.getChildrenCount() != 1) {
                    Log.d("Firebase Service", "photo listener not empty");
                    // for each photo if photo exists and karma changed then get new karma value
                    // else download new photo
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren())
                        if (!snapshot.getKey().equals(NULL)) {
                            if (storage.checkPhotoExistInCurrentUser(snapshot.getKey())) {
                                try {
                                    Log.d("Firebase Service", "photo listener update karma");
                                    Photo photo = PhotoChooser.getPhoto(snapshot.getKey());
                                    if ((int) ((long) snapshot.getValue()) != photo.karma)
                                        photo.karma = (int) ((long) snapshot.getValue());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d("Firebase Service", "photo listener download photo");
                                FirebaseStorageAdapter.getInstance()
                                        .downloadPhotoFromUser(unvalidateName(friend),
                                                unvalidateName(snapshot.getKey()),
                                                getBaseContext());

                                //PhotoChooser.photos.add(new Photo(friend, unvalidateName(snapshot.getKey()), getBaseContext()));
                            }
                        }
                } else {
                    Log.d("Firebase Service", "photo listener empty");
                    File directory = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "DejaPhoto" + File.separator + "DejaPhotoAlbum");

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        if (directory.isDirectory())
                            for (final File file : directory.listFiles())
                                if (file.getName().equals(unvalidateName(snapshot.getKey()))) {
                                    file.delete();
                                    break;
                                }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        database.child(friend).child(PHOTOS).addValueEventListener(valueEventListener);
        return valueEventListener;
    }
}
