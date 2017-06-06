package com.android.dejaphoto;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.android.dejaphoto.R.drawable.apple;
import static com.android.dejaphoto.R.drawable.ic_chevron_left_black_48dp;

public class InteractiveActivity extends AppCompatActivity {
    //Account object of the current user
    GoogleSignInAccount acct;
    DatabaseReference userRef;
    int numOfMessages = 0;
    final String databaseURL = "https://deja-demo.firebaseio.com/";
    FirebaseStorageAdapterInterface storage;
    FirebaseDatabaseAdapterInterface database;
    File testFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive);
        acct = getIntent().getParcelableExtra("UID");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:45709257200:android:41cabffdd2db9746")
                .setDatabaseUrl(databaseURL)
                .build();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storage = FirebaseStorageAdapter.getInstance();
        this.database = FirebaseDatabaseAdapter.getInstance();

        if (acct != null) {
            Toast.makeText(this, "UID is correctly passed", Toast.LENGTH_SHORT).show();
            userRef = database.getReferenceFromUrl(databaseURL);
            if (userRef != null)
                Toast.makeText(this, "Successfully get database!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Failed to connect to database", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this, "Unable to get UID", Toast.LENGTH_SHORT).show();

    }


    //On clicks
    public void testCheckFile(View view)
    {
        boolean result = storage.checkPhotoExistInCurrentUser("Nothing.jpg");
        if (result == true)
            Toast.makeText(this, "File exist!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "File DOESN'T exist!", Toast.LENGTH_LONG).show();
    }

    public void testUploadFile(View view)
    {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        File dejaPhotoAlbumFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
        if (!dejaPhotoAlbumFile.exists()) {
            Log.d("Interactive", "Path doesn't exist");
        }
        //Try to get a test file
        for (File file : dejaPhotoAlbumFile.listFiles())
        {
            if (file.isFile()){
                testFile = file;
                break;
            }
        }

        //Path to file
        //Uri uri = Uri.parse("android.resource://"+ getPackageName()+ "/raw/apple.jpg");
        //File file = new File(uri.getPath());
        boolean isAFile = testFile.isFile();
        UploadTask task = storage.uploadPhotoFile(testFile);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(InteractiveActivity.this, "Finished Uploading!", Toast.LENGTH_SHORT).show();
                TextView view = (TextView) findViewById(R.id.textView);
                @SuppressWarnings("VisibleForTests") Uri download = taskSnapshot.getDownloadUrl();
                view.setText(download.toString());
            }
        });
    }

    //getUserFromDatabase
    public void testGetUserFromDatabase(View view)
    {

        String currUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        User result = database.getUserFromDatabase(currUser);

        if (result != null)
            Toast.makeText(this, "User exists!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "User DOESN'T exist!", Toast.LENGTH_LONG).show();

    }

    //CreateNewUser
    public void testCreateUser(View view){

        Map<String, Object> friendList = new HashMap<>();
        HashMap<String, Object> photoList = new HashMap();

        friendList.put(acct.getId(), 0);

        photoList.put("Empty Photo", "Null");

        User newUser = new User(friendList, photoList);
        boolean checkResult = database.createNewUser("Perkins", newUser);

        if (checkResult == false)
            Toast.makeText(this, "User already exists in database!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "New User Created!", Toast.LENGTH_LONG).show();


    }

    //testing getListOfPhotoFromUser
    public void testGetListOfPhotoFromUser(View view){


        List<String> photoList;


        photoList = database.getListOfPhotoFromUser("vmperkin@ucsd_edu");

        if (photoList.size() == 0)
            Toast.makeText(this, "User photoList size is 1", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "User photoList exists!", Toast.LENGTH_LONG).show();


    }

    //testing addNewPhotoEntry
    public void testAddNewPhotoEntry(View view){

        boolean check = database.addNewPhotoEntry("PictureOfDog");

        if (check == true)
            Toast.makeText(this, "Photo added to database!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Photo NOT added to database!", Toast.LENGTH_LONG).show();


    }

    //testing check photo entry
    public void testCheckPhotoEntry(View view){


        boolean check = database.checkPhotoEntry("PictureOfDog");

        if (check != true)
            Toast.makeText(this, "Picture of Dog found", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Picture of Dog not found", Toast.LENGTH_LONG).show();


    }

}
