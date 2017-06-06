package com.android.dejaphoto;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by Cyrax on 6/2/2017.
 */

class FirebaseStorageAdapter implements FirebaseStorageAdapterInterface {
    private static FirebaseStorageAdapter ourInstance = null;

    //Used to take care of Storage references
    private static String currUserEmail;
    private static StorageReference currUserDir;
    private static StorageReference rootDir;

    //Tag used for debug
    private String debug_tag = "AdapterStorage";

    static FirebaseStorageAdapter getInstance() {

        //Using lazy instantiation
        if (ourInstance == null)
        {
            ourInstance = new FirebaseStorageAdapter();

            //Used to take care of Storage references
            currUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            currUserDir = FirebaseStorage.getInstance().getReference().child(currUserEmail);
            rootDir = FirebaseStorage.getInstance().getReference();
        }
        return ourInstance;
    }

    private FirebaseStorageAdapter() {
    }

    /**
     * Upload a photo file to the database
     *
     * @param photoFile file representing the photo
     * @return returns an UploadTask, which can then be used to manage the upload of the photo
     */
    @Override
    public UploadTask uploadPhotoFile(File photoFile) {
        //Start an upload task
        Uri fileUri = Uri.fromFile(photoFile);
        StorageReference targetReference = currUserDir.child(fileUri.getLastPathSegment());
        UploadTask task = targetReference.putFile(fileUri);

        //Added for debugging purposes
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                int errorCode = ((StorageException) exception).getErrorCode();
                String errorMessage = exception.getMessage();
                Log.d(debug_tag, "Error Message: " + errorMessage);
            }
        });
        return task;
    }

    /**
     * Download a photo from the database from a user's space
     *
     * @param email         E-mail address of the user. Used to locate the user's directory
     * @param photoFileName name of the requested photo file
     * @return returns TRUE if photo exists and downloaded. FALSE if not.
     */
    @Override
    public boolean downloadPhotoFromUser(String email, String photoFileName) {
        return false;
    }

    /**
     * Remove the specified photo files from current user's storage
     *
     * @param photoFileName name of the photo file to be removed
     * @return returns TRUE if photo exists and removed. FALSE if not.
     */
    @Override
    public boolean removePhoto(String photoFileName) {
        return false;
    }

    /**
     * Check whether or not the photo exist in the current user's storage
     *
     * @param photoFileName name of the photo file requested
     * @return returns TRUE if photo exists. FALSE if not.
     */
    @Override
    public boolean checkPhotoExistInCurrentUser(String photoFileName) {
        StorageReference fileReference = currUserDir.child(photoFileName);
        final boolean[] isExist = {false};

        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                isExist[0] = true;
                Log.d(debug_tag, uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isExist[0] = false;
            }
        });
        return isExist[0];
    }

    /**
     * Check whether or not the photo exist in the specified user's storage
     *
     * @param photoFileName name of the photo file requested
     * @return returns TRUE if photo exists. FALSE if not.
     */
    @Override
    public boolean checkPhotoExistInSpecifiedUser(String photoFileName) {
        return false;
    }

    /**
     * Remove all photos of the current User
     *
     * @return return TRUE if photos are deleted. FALSE if not.
     */
    @Override
    public boolean removeAllPhotos() {
        final boolean result[] = new boolean[1];

        currUserDir.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(debug_tag, "Successfully deleted directory");
                result[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(debug_tag, "Failed to delete directory");
                result[0] = false;
            }
        });
        return result[0];
    }
}
