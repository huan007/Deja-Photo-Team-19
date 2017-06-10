package com.android.dejaphoto;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by Cyrax on 6/1/2017.
 * Outlining the usage of the adapter from Firebase Database to DejaPhoto. Make it easier for others
 * to use Firebase Database
 */

public interface FirebaseDatabaseAdapterInterface {

    /**
     * Used to get user's info stored in database every time user log in
     * @param email E-mail address of the user requested
     * @return User object that is stored on the Database. Will return NULL if the user requested
     * does not exist
     */
    public User getUserFromDatabase(String email);

    /**
     * Create a new user entry in the database
     * @param email E-mail address of the new user
     * @param newUser User object that need to be injected into database. Contains list of friends
     *                and list of photos
     * @return boolean, true if user was added, false if user already in database
     */
    public boolean createNewUser(String email, User newUser);

    /**
     * Return a list of photos shared by the specified user
     * @param email E-mail address of the specified user
     * @return List of photos shared by the user
     */
    public List<String> getListOfPhotoFromUser(String email);

    /**
     * Extract information from Photo object and create a new entry in current user's list of photo
     * @param newPhoto new photo that will be added to the databse
     * @return returns TRUE if the photo was successfully added. FALSE if not.
     */
    public boolean addNewPhotoEntry(String newPhoto);

    /**
     * Check whether or not the photo is already in the database
     * @param photo photo to be checked
     * @return returns TRUE if the photo is already in the database. FALSE if not.
     */
    public boolean checkPhotoEntry(String photo);

    /**
     * Remove photo entry from Firebase Database
     * @param photo photo to be removed
     * @return returns TRUE if photo is successfully removed. FALSE if the photo is not there.
     */
    public boolean removePhotoEntry(String photo);

    /**
     * Get handle to requested user's photo list. Used to create Listeners
     * @param email E-mail address of specified user
     * @return returns a reference to specified user's photo list. Returns NULL if user doesn't
     * exist
     */
    public DatabaseReference getUserPhotoReference(String email);

    /**
     * Add new friend using email
     * @param email E-mail address of the new friend. Used to identify the friend
     * @return returns TRUE if new friend entry is created. FALSE if not.
     */
    public boolean addNewFriendEntryByName(String email);

    /**
     * Check whether the friend exist in the current user's friend list
     * @param email E-mail address of the friend. Used to identify the friend
     * @return returns TRUE if friend is in the user's friend list. FALSE if not.
     */
    public boolean checkFriendEntryByName(String email);

    /**
     * Remove friend from user's friend list
     * @param email E-mail address of the specified friend. Used to identify the friend
     * @return returns TRUE if friend is successfully removed. FALSE if the friend is not there.
     */
    public boolean removeFriendEntryByName(String email);

    /**
     * Get handle to specified user's friend list. Used to create Listeners
     * @param email E-mail address of the specified user
     * @return returns a reference to specified user's friend list. Returns NULL if the user doesn't
     * exist
     */
    public DatabaseReference getUserFriendReference(String email);
}
