package com.android.dejaphoto;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Cyrax on 6/2/2017.
 */

class FirebaseDatabaseAdapter implements FirebaseDatabaseAdapterInterface {
    private static FirebaseDatabaseAdapter ourInstance = new FirebaseDatabaseAdapter();

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
        return null;
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
    public User createNewUser(String email, User newUser) {
        return null;
    }

    /**
     * Return a list of photos shared by the specified user
     *
     * @param email E-mail address of the specified user
     * @return List of photos shared by the user
     */
    @Override
    public List<Photo> getListOfPhotoFromUser(String email) {
        return null;
    }

    /**
     * Extract information from Photo object and create a new entry in current user's list of photo
     *
     * @param newPhoto new photo that will be added to the databse
     * @return returns TRUE if the photo was successfully added. FALSE if not.
     */
    @Override
    public boolean addNewPhotoEntry(Photo newPhoto) {
        return false;
    }

    /**
     * Check whether or not the photo is already in the database
     *
     * @param photo photo to be checked
     * @return returns TRUE if the photo is already in the database. FALSE if not.
     */
    @Override
    public boolean checkPhotoEntry(Photo photo) {
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
        return null;
    }

    /**
     * Add new friend using email
     *
     * @param email E-mail address of the new friend. Used to identify the friend
     * @return returns TRUE if new friend entry is created. FALSE if not.
     */
    @Override
    public boolean addNewFriendEntryByEmail(String email) {
        return false;
    }

    /**
     * Check whether the friend exist in the current user's friend list
     *
     * @param email E-mail address of the friend. Used to identify the friend
     * @return returns TRUE if friend is in the user's friend list. FALSE if not.
     */
    @Override
    public boolean checkFriendEntryByEmail(String email) {
        return false;
    }

    /**
     * Remove friend from user's friend list
     *
     * @param email E-mail address of the specified friend. Used to identify the friend
     * @return returns TRUE if friend is successfully removed. FALSE if the friend is not there.
     */
    @Override
    public boolean removeFreindEntryByEmail(String email) {
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
    public boolean getUserFriendReference(String email) {
        return false;
    }
}
