package com.android.dejaphoto;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;

import java.io.File;

import static com.google.android.gms.common.api.GoogleApiClient.*;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener {

    // Create an instance of GoogleAPIClient.
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();


     //adding APIs to the client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



        // Start DejaService
        Intent intent = new Intent(MainActivity.this, DejaService.class);
        startService(intent);
        Log.d("MainActivity", "Started Service");


        // Ask user for permission to access photos -- Phillip
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        5);

                Toast toast = Toast.makeText(this.getApplicationContext(), "here", Toast.LENGTH_SHORT);
                toast.show();


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        5);
                Toast.makeText(this.getApplicationContext(), "here", Toast.LENGTH_SHORT).show();
            }
        }


        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");


        GetAllPhotosFromGallery gallery = new GetAllPhotosFromGallery(file, this.getApplicationContext());

        //TextView textView = (TextView) findViewById(R.id.message);
        //textView.setText( file.listFiles()[0].getName() );

        //Bitmap image = gallery.photos.get(1).photo;
        //String date = gallery.photos.get(1).datetime;

    }

    //connect to location services on start
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    //disconnect to location services on stop
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Getting current location as lat

    //Trying to figure out how to implement Googles location API interfaces
    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
    }

            public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final SharedPreferences.Editor editor = getContext().getSharedPreferences("settings", MODE_PRIVATE).edit();

            // set button change listener for Interval Setting
            findPreference("interval").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    editor.putInt("interval", Integer.valueOf((String) newValue));
                    Log.d("interval value", "change to " + newValue);
                    return true;
                }
            });

            // set button change listener for Deja Vu Mode Setting
            findPreference("dejavu").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        // unchecked -> checked
                        findPreference("location").setEnabled(true);
                        findPreference("day").setEnabled(true);
                        findPreference("time").setEnabled(true);
                    } else {
                        // checked -> unchecked
                        findPreference("location").setEnabled(false);
                        findPreference("day").setEnabled(false);
                        findPreference("time").setEnabled(false);
                    }

                    Log.d("deja value", newValue.toString());
                    editor.putBoolean("dejavu", (Boolean) newValue);
                    return true;
                }
            });

            // set button change listener for Location Setting
            findPreference("location").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        // unchecked -> checked
                    } else {
                        // checked -> unchecked
                    }

                    Log.d("location", newValue.toString());
                    editor.putBoolean("location", (Boolean) newValue);
                    return true;
                }
            });

            // set button change listener for Day of Week Setting
            findPreference("day").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        // unchecked -> checked
                    } else {
                        // checked -> unchecked
                    }

                    Log.d("day", newValue.toString());
                    editor.putBoolean("day", (Boolean) newValue);
                    return true;
                }
            });

            // set button change listener for Time of Day Setting
            findPreference("time").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        // unchecked -> checked
                    } else {
                        // checked -> unchecked
                    }

                    Log.d("time", newValue.toString());
                    editor.putBoolean("time", (Boolean) newValue);
                    return true;
                }
            });

            editor.apply();
        }
    }


    // For getting permission from user to access photos -- Phillip
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
