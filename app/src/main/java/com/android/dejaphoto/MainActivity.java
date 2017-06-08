package com.android.dejaphoto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.gms.common.api.GoogleApiClient.*;


public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    // Create an instance of GoogleAPIClient.
    GoogleApiClient mGoogleApiClient;

    //Location static variables to access from other classes
    public static Location mCurrentLocation;
    public static double latitude;
    public static double longitude;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();


        //Beginning a location update request, setting interval to every 10 seconds
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);


        //adding location APIs to the client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Ask user for permission to access photos -- Phillip
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response. After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CAMERA},
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
        // Handling action bar item clicks
        int id = item.getItemId();

        //no inspection Simplifiable If Statement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Implementing google's location API interfaces
    @Override
    public void onConnected(Bundle bundle) {
        LatLng latLng;

        //don't call startLocationUpdates if mGoogleApiClient is not connected:
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        // Get last known, most recent location.
        checkPermission();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // Note that this can be NULL if last location isn't already known.
        if (mCurrentLocation != null) {
            // log current location if not null
            Log.d("DEBUG", "current location: " + mCurrentLocation.toString());
            latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
            Log.d("Latitude and Longtitude", "current location: " + latLng.toString());
        }
    }

    //Handle connection being suspended. Toast message to user
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    //Handle connection failing. Toast message to user
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();

    }

    //Called on location change - update static lat/long variables
    @Override
    public void onLocationChanged(Location location) {
        //getting last known location again
        checkPermission();
        System.out.println("location changed");
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //updating lat/long variables
        if (mCurrentLocation != null) {
            latitude = returnLatitude();
            longitude = returnLong();

        }
    }

    //Ask user for permission to access their location
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    //Start getting regular location updates with low power interval
    protected void startLocationUpdates() {
        checkPermission();
    }

    //getter method for current latitude
    public static double returnLatitude() {
        System.out.println(mCurrentLocation.getLatitude());
        return ((mCurrentLocation != null) ? (latitude = mCurrentLocation.getLatitude()) : 999);
    }

    //getter method for current longitude
    public static double returnLong() {
        System.out.println(mCurrentLocation.getLongitude());
        return ((mCurrentLocation != null) ? (longitude = mCurrentLocation.getLongitude()) : 999);
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

    // For getting permission from user to access photos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Start DejaService
                    Intent intent = new Intent(MainActivity.this, DejaService.class);
                    startService(intent);
                    Log.d("MainActivity", "Started Service");
                }

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    android.location.LocationListener locationListener = new android.location.LocationListener() {
                        // Check if location has changed
                        @Override
                        public void onLocationChanged(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    };

                    // Get updated Location
                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    String locationProvider = LocationManager.GPS_PROVIDER;
                    locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                }
            }
        }
    }

    //Alarm class for updating app GUI
    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case DejaCamera.REQUEST_IMAGE_CAPTURE:
                    DejaCamera.exitCamera();
                    break;
            }
        }

        @Override
        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            // Listener for user adding new friends
            final EditTextPreference friendsPref = (EditTextPreference) findPreference("add_friend");
            friendsPref.setDefaultValue("");
            friendsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    friendsPref.setDefaultValue("");
                    friendsPref.setText("");

                    final String friendEmail = FirebaseService.validateName((String) newValue);
                    final String userEmail = getContext().getSharedPreferences("user", MODE_PRIVATE).getString("email", null);

                    final Object newNewValue = newValue;
                    final Context context = getContext();
                    FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Check that email entered is of proper format
                            // http://howtodoinjava.com/regex/java-regex-validate-email-address/
                            String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                            if (!((String) newNewValue).matches(emailRegex)) {
                                Toast.makeText(context, "Re-enter proper email address format", Toast.LENGTH_LONG).show();
                            }
                            // Check that email entered is not the email of current user
                            else if (userEmail.equals(friendEmail)) {
                                Toast.makeText(context, "Cannot add yourself as a friend", Toast.LENGTH_LONG).show();
                            } else if (!dataSnapshot.hasChild(friendEmail)) {
                                Toast.makeText(context, "Friend is not DejaPhoto user", Toast.LENGTH_LONG).show();
                            } else {

                                // Add email of new friend
                                Log.d("New Friend Receiver", "Email is : " + newNewValue);

                                Intent serviceIntent = new Intent(context, FirebaseService.class);
                                serviceIntent.putExtra(FirebaseService.ACTION, FirebaseService.ADD_FRIEND);
                                serviceIntent.putExtra(FirebaseService.FRIEND, friendEmail);
                                context.startService(serviceIntent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    return true;
                }
            });

            final SharedPreferences sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            // update home-screen automatically at a rate specified by the user
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

            // set button click listener for camera
            final PreferenceFragment fragment = this;
            findPreference("camera").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("camera", "starting camera");
                    DejaCamera.startCamera(fragment, getContext());
                    return true;
                }
            });

            // set button click listener for album_main
            findPreference("album_main").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("album_main", "entering album");
                    return true;
                }
            });

            // set button click listener for album_copied
            findPreference("album_copied").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("album_copied", "entering album");
                    return true;
                }
            });

            // set button click listener for album_friends
            findPreference("album_friends").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("album_friends", "entering album");
                    return true;
                }
            });

            // set button click listener for photo_picker
            findPreference("photo_picker").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("photo_picker", "entering picker");
                    return true;
                }
            });

            // set button change listener for Interval Setting
            findPreference("interval").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
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
            findPreference("location").setEnabled(((SwitchPreference) findPreference("dejavu")).isChecked());
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
            findPreference("day").setEnabled(((SwitchPreference) findPreference("dejavu")).isChecked());
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
            findPreference("time").setEnabled(((SwitchPreference) findPreference("dejavu")).isChecked());
            findPreference("time").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putBoolean("time", (Boolean) newValue);
                    editor.apply();
                    Log.d("time", newValue.toString());
                    return true;
                }
            });

            // set button change listener for Show Your Photo Setting
            findPreference("own").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d("own", newValue.toString());
                    return true;
                }
            });

            // set button change listener for Show Friends Photo Setting
            findPreference("friends").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d("own", newValue.toString());
                    return true;
                }
            });

            // set button change listener for Share You Photo Setting
            findPreference("share").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Log.d("share", newValue.toString());
                    Intent serviceIntent = new Intent(getContext(), FirebaseService.class);
                    serviceIntent.putExtra(FirebaseService.ACTION, FirebaseService.REMOVE_PHOTOS);
                    getContext().startService(serviceIntent);
                    return true;
                }
            });
        }
    }
}
