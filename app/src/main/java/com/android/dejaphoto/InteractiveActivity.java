package com.android.dejaphoto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InteractiveActivity extends AppCompatActivity {
    //Account object of the current user
    GoogleSignInAccount acct;
    DatabaseReference userRef;
    int numOfMessages = 0;
    final String databaseURL = "https://deja-demo.firebaseio.com/";

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
    public void send(View view)
    {
        //Get unique database location for the user
        EditText newMessage = (EditText) findViewById(R.id.editText);
        DatabaseReference nextMessageDatabase = FirebaseDatabase.getInstance().getReference()
                .child(acct.getId())
                .child("Messages")
                .child(String.valueOf(numOfMessages));
        nextMessageDatabase.setValue(newMessage.getText());

        //Update the number of message
        DatabaseReference numOfMessageDatabase = FirebaseDatabase.getInstance().getReference()
                .child(acct.getId())
                .child("numOfMessage");
        numOfMessages++;
        numOfMessageDatabase.setValue(numOfMessages);
    }
}
