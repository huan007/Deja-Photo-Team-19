package com.android.dejaphoto;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Cyrax on 6/2/2017.
 */

class FirebaseDatabaseAdapter implements FirebaseDatabaseAdapterInterface {
    private static FirebaseDatabaseAdapter ourInstance = null;

    //Used to take care of Storage references
    private static String currUserEmail;
    private static DatabaseReference currUserDir;
    private static DatabaseReference rootDir;

    //Tag used for debug
    private String debug_tag = "AdapterDatabase";

    static FirebaseDatabaseAdapter getInstance() {

        //Using lazy instantiation
        if (ourInstance == null)
        {
            ourInstance = new FirebaseDatabaseAdapter();

            //Used to take care of Storage references
            currUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            currUserDir = FirebaseDatabase.getInstance().getReference().child(currUserEmail);
            rootDir = FirebaseDatabase.getInstance().getReference();
        }
        return ourInstance;
    }

    private FirebaseDatabaseAdapter() {
    }

    /**
     * Used to get user's info stored in database every time user log in
     *
     * @param email E-mail address of the user requested
     * @return User object that is stored on the Database. Will return NULL if the user requested
     * does not exist
     */
    @Override
    public User getUserFromDatabase(String email) {

        DatabaseReference specifiedUser = rootDir.child(email);

        final User[] currUser = new User[1];

        ValueEventListener userListener = new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                currUser[0] = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        specifiedUser.addListenerForSingleValueEvent(userListener);

        return currUser[0];
    }

    /**
     * Create a new user entry in the database
     *
     * @param email   E-mail address of the new user
     * @param newUser User object that need to be injected into database. Contains list of friends
     *                and list of photos
     * @return
     */
    @Override
    public boolean createNewUser(String email, User newUser) {

        User currUser;
        if((currUser = getUserFromDatabase(email)) != null){return false;}

        currUserDir.setValue(newUser);

        return true;
    }

    /**
     * Return a list of photos shared by the specified user
     *
     * @param email E-mail address of the specified user
     * @return List of photos shared by the user
     */
    @Override
    public List<Photo> getListOfPhotoFromUser(String email) {

        final List<Photo> photoList = new ArrayList<Photo>();
        rootDir.child(email).child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    photoList.add(snapshot.getValue(Photo.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return photoList;
    }

    /**
     * Extract information from Photo object and create a new entry in current user's list of photo
     *
     * @param newPhoto new photo that will be added to the databse
     * @return returns TRUE if the photo was successfully added. FALSE if not.
     */
    @Override
    public boolean addNewPhotoEntry(Photo newPhoto) {
        final Photo passPhoto = newPhoto;
        final List<Photo> photoList = new ArrayList<Photo>();
        currUserDir.child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    photoList.add(snapshot.getValue(Photo.class));
                    photoList.add(passPhoto);


                currUserDir.child("photos").setValue(photoList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }

    /**
     * Check whether or not the photo is already in the database
     *
     * @param photo photo to be checked
     * @return returns TRUE if the photo is already in the database. FALSE if not.
     */
    @Override
    public boolean checkPhotoEntry(Photo photo) {
        final Photo passPhoto = photo;


        final List<Photo> photoList = new ArrayList<Photo>();
        currUserDir.child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    photoList.add(snapshot.getValue(Photo.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for(int i = 0; i < photoList.size(); i++) {
            if (passPhoto == photoList.get(i)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Remove photo entry from Firebase Database
     *
     * @param photo photo to be removed
     * @return returns TRUE if photo is successfully removed. FALSE if the photo is not there.
     */
    @Override
    public boolean removePhotoEntry(Photo photo) {

        final Photo passPhoto = photo;


        final List<Photo> photoList = new ArrayList<Photo>();
        currUserDir.child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    photoList.add(snapshot.getValue(Photo.class));
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        for(int i = 0; i < photoList.size(); i++) {
            if (passPhoto == photoList.get(i)) {
                photoList.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Get handle to requested user's photo list. Used to create Listeners
     *
     * @param email E-mail address of specified user
     * @return returns a reference to specified user's photo list. Returns NULL if user doesn't
     * exist
     */
    @Override
    public DatabaseReference getUserPhotoReference(String email) {

        return  rootDir.child(email).child("photos");
    }

    /**
     * Add new friend using email
     *
     * @param email E-mail address of the new friend. Used to identify the friend
     * @return returns TRUE if new friend entry is created. FALSE if not.
     */
    @Override
    public boolean addNewFriendEntryByName(String email) {
        if (email == null)
            return false;

        final String passName = email;

        final List<Object> oldFriends = new ArrayList<>();
        currUserDir.child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            oldFriends.add(snapshot.getValue());

                        oldFriends.add(passName);
                        currUserDir.child("friends")
                                .setValue(new ArrayList<>(new HashSet<>(oldFriends)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        return true;
    }

    /**
     * Check whether the friend exist in the current user's friend list
     *
     * @param email E-mail address of the friend. Used to identify the friend
     * @return returns TRUE if friend is in the user's friend list. FALSE if not.
     */
    @Override
    public boolean checkFriendEntryByName(String email) {
        if (email == null)
            return false;

        final String passName = email;

        final List<Object> friends = new ArrayList<>();
        currUserDir.child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            friends.add(snapshot.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        for(int i = 0; i < friends.size(); i++){
            if(email == friends.get(i)){return true;}

        }
        return false;
    }

    /**
     * Remove friend from user's friend list
     *
     * @param email E-mail address of the specified friend. Used to identify the friend
     * @return returns TRUE if friend is successfully removed. FALSE if the friend is not there.
     */
    @Override
    public boolean removeFriendEntryByName(String email) {

        if (email == null)
            return false;

        final String passName = email;

        final List<Object> friends = new ArrayList<>();
        currUserDir.child("friends")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            friends.add(snapshot.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        for(int i = 0; i < friends.size(); i++){
            if(email == friends.get(i)){
                friends.remove(i);
                return true;
            }
        }
        return false;
    }


    /**
     * Get handle to specified user's friend list. Used to create Listeners
     *
     * @param email E-mail address of the specified user
     * @return returns a reference to specified user's friend list. Returns NULL if the user doesn't
     * exist
     */
    @Override
    public DatabaseReference getUserFriendReference(String email) {

        return currUserDir.child("friends");
    }
}
