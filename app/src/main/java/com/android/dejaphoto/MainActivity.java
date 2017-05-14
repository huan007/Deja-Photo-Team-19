package com.android.dejaphoto;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;

import static com.google.android.gms.common.api.GoogleApiClient.*;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    // Create an instance of GoogleAPIClient.
    GoogleApiClient mGoogleApiClient;
    //LocationRequest mLocationRequest;

    public static Location mCurrentLocation;
    public static double latitude;
    public static double longitude;



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
            }
        }
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

    //Trying to figure out how to implement Googles location API interfaces
    @Override
    public void onConnected(Bundle bundle) {
        LatLng latLng;
        // Get last known recent location.
        checkPermission();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Note that this can be NULL if last location isn't already known.
        if (mCurrentLocation != null) {
            // Print current location if not null
            Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
            latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            Log.d("Latitude and Longtitude", "current location: " + latLng.toString());
        }
        //don't call startLocationUpdates if mGoogleApiClient is not connected:
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
      /*  if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
    }

    @Override
    public void onLocationChanged(Location location) {
        checkPermission();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (mCurrentLocation != null) {
            returnLatitude();
            returnLong();

            //latitude = mCurrentLocation.getLatitude();
            //longitude = mCurrentLocation.getLongitude();
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    //Start getting regular location updates with low power interval
    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        checkPermission();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //Thread.dumpStack();

    }

    public static double returnLatitude() {
        return (latitude = mCurrentLocation.getLatitude());
    }

    public static double returnLong() {
        return (longitude = mCurrentLocation.getLongitude());
    }

    //connect to location services on start
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    //disconnect from location services on stop
    protected void onStop() {

        // Disconnecting the client invalidates it.
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    // For getting permission from user to access photos -- Phillip
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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

    public static class PrefsFragment extends PreferenceFragment {

        AlarmManager alarmManager;
        PendingIntent pending;

        @Override
        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final SharedPreferences sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            // update homescreen automatically at a rate specified by the user
            final Handler refresh = new Handler();
            refresh.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // set next photo
                    Intent serviceIntent = new Intent(getContext(), DejaService.class);
                    serviceIntent.putExtra(DejaService.actionFlag, DejaService.refreshAction);
                    Log.d("Refresh Receiver", "Extra string:" + serviceIntent.getStringExtra(DejaService.actionFlag));
                    getContext().startService(serviceIntent);

                    // call handler again
                    refresh.postDelayed(this, sharedPreferences.getInt("interval", 300) * 1000);
                }
            }, sharedPreferences.getInt("interval", 300) * 1000);

            // set button change listener for Interval Setting
            findPreference("interval").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int interval = Integer.valueOf((String) newValue);
                    editor.putInt("interval", Integer.valueOf((String) newValue));
                    editor.apply();
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

                    editor.putBoolean("dejavu", (Boolean) newValue);
                    editor.apply();
                    Log.d("deja value", newValue.toString());
                    return true;
                }
            });

            // set button change listener for Location Setting
            findPreference("location").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putBoolean("location", (Boolean) newValue);
                    editor.apply();
                    Log.d("location", newValue.toString());
                    return true;
                }
            });

            // set button change listener for Day of Week Setting
            findPreference("day").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putBoolean("day", (Boolean) newValue);
                    editor.apply();
                    Log.d("day", newValue.toString());
                    return true;
                }
            });

            // set button change listener for Time of Day Setting
            findPreference("time").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putBoolean("time", (Boolean) newValue);
                    editor.apply();
                    Log.d("time", newValue.toString());
                    return true;
                }
            });
        }

    }

}
