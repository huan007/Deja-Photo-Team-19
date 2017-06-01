package com.android.dejaphoto;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    SignInButton signInButton;
    Button signOutButton;
    TextView statusTextView;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    //Reference pointing to user's designated space on database where user can access their friends
    private DatabaseReference userRef;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get singleton Auth object
        mAuth= FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        statusTextView = (TextView) findViewById(R.id.status_textview);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void signIn(){
        Intent sighInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(sighInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            statusTextView.setText("Attempting to login...");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.d(TAG, "StatusCode: " + statusCode);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult;" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            //Authenticate with firebase
            firebaseAuthWithGoogle(acct);
            getUserDatabaseReference(acct);
            statusTextView.setText("Hello, " + acct.getDisplayName());

            //Change Activity
            Intent intent = new Intent (this, InteractiveActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("UID", acct);
            startActivity(intent);
            finish();
        }else{

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            //Authentication success
                            Log.d(TAG, "firebaseAuthWithGoogle:Success");
                        }

                        else
                        {
                            //Failed to authenticate
                            Log.d(TAG, "firebaseAuthWithGoogle:Failed");
                        }
                    }
                });
    }

    public void getUserDatabaseReference(GoogleSignInAccount acct)
    {
        //Try to get reference. If return null then that mean new-user, then we create a new reference
        userRef = FirebaseDatabase.getInstance().getReference().child(acct.getId());

        //Handle new-user
        if (userRef == null)
        {
            userRef = createUserDatabaseReference(acct);
        }

        //Populate User Object
        final User[] dejaUser = new User[1];

        ValueEventListener dejaUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dejaUser[0] = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Attempted to retrieve User's data");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userRef.addListenerForSingleValueEvent(dejaUserListener);

        if (userRef != null)
            Log.d(TAG, "Connected to User Database!");

        if (dejaUser[0] == null) {
            Log.d(TAG, "Failed to retrieve User's Information");
            //Make new user object in database
            LinkedList<Object> friendList = new LinkedList<>();
            HashMap<String, Object> photoList = new HashMap();
            friendList.add(acct.getId());
            photoList.put("Empty Photo", "Null");
            userRef.setValue(new User(friendList, photoList));
            //userRef.child("friends").child("0").removeValue();
            //userRef.child("photos").child("Photo 1").removeValue();
            Log.d(TAG, "Created new User in Database!");
        }
    }

    private DatabaseReference createUserDatabaseReference(GoogleSignInAccount acct) {
        DatabaseReference rootRef  = FirebaseDatabase.getInstance().getReference();
        rootRef.child(acct.getId()).setValue(new User(new LinkedList<Object>(), new HashMap<String, Object>()));
        Log.d(TAG, "Created new User in Database!");
        return rootRef.child(acct.getId());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.d(TAG, "onconnectionFailed:" + connectionResult);

    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                statusTextView.setText("Signed out");
            }
        });
    }
}
