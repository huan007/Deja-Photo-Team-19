package com.android.dejaphoto;

import android.content.Context;

import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by Cyrax on 6/2/2017.
 * Outlining the uage of the adapter from Firebase Storage to Deja Photo. Make it easier for others
 * to use Firebase Storage
 */

public interface FirebaseStorageAdapterInterface {

    /**
     * Upload a photo file to the database
     * @param photoFile file representing the photo
     * @return returns an UploadTask, which can then be used to manage the upload of the photo
     */
    public UploadTask uploadPhotoFile(File photoFile);

    /**
     * Download a photo from the database from a user's space
     * @param email E-mail address of the user. Used to locate the user's directory
     * @param photoFileName name of the requested photo file
     * @return returns TRUE if photo exists and downloaded. FALSE if not.
     */
    public boolean downloadPhotoFromUser(String email, String photoFileName, final Context context);

    /**
     * Remove the specified photo files from current user's storage
     * @param photoFileName name of the photo file to be removed
     * @return returns TRUE if photo exists and removed. FALSE if not.
     */
    public boolean removePhoto(String photoFileName);

    /**
     * Check whether or not the photo exist in the current user's storage
     * @param photoFileName name of the photo file requested
     * @return returns TRUE if photo exists. FALSE if not.
     */
    public boolean checkPhotoExistInCurrentUser(String photoFileName);

    /**
     * Check whether or not the photo exist in the specified user's storage
     * @param photoFileName name of the photo file requested
     * @return returns TRUE if photo exists. FALSE if not.
     */
    public boolean checkPhotoExistInSpecifiedUser(String photoFileName);

    /**
     * Remove all photos of the current User
     * @return return TRUE if photos are deleted. FALSE if not.
     */
    public boolean removeAllPhotos();
}
