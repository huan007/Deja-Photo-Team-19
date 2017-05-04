package com.android.dejaphoto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Switch switchDeja = (Switch) findViewById(R.id.DejaVu);
        switchDeja.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // ToDO Turn Deja Vu Mode On
                    Toast.makeText(getApplicationContext(), "Deja Vu Mode ON", Toast.LENGTH_SHORT).show();
                } else {
                    // ToDo Turn Deja Vu Mode Off
                    Toast.makeText(getApplicationContext(), "Deja Vu Mode OFF", Toast.LENGTH_SHORT).show();
                }
                Log.d("Deja Vu Mode Preference", Boolean.valueOf(isChecked).toString());
            }
        });

        Switch switchLocation = (Switch) findViewById(R.id.Location);
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // ToDO Turn Location Preference On
                    Toast.makeText(getApplicationContext(), "Location ON", Toast.LENGTH_SHORT).show();
                } else {
                    // ToDO Turn Location Preference Off
                    Toast.makeText(getApplicationContext(), "Locatoin OFF", Toast.LENGTH_SHORT).show();
                }
                Log.d("Location Preference", Boolean.valueOf(isChecked).toString());
            }
        });

        Switch switchDay = (Switch) findViewById(R.id.DayOfWeek);
        switchDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // ToDO Turn Day of Week Preference On
                    Toast.makeText(getApplicationContext(), "Day of Week ON", Toast.LENGTH_SHORT).show();
                } else {
                    // ToDO Turn Day of Week Preference Off
                    Toast.makeText(getApplicationContext(), "Day of Week OFF", Toast.LENGTH_SHORT).show();
                }
                Log.d("Day of Week Preference", Boolean.valueOf(isChecked).toString());
            }
        });

        Switch switchTime = (Switch) findViewById(R.id.Time);
        switchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // ToDO Turn Time Preference On
                    Toast.makeText(getApplicationContext(), "Time ON", Toast.LENGTH_SHORT).show();
                } else {
                    // ToDO Turn Time Preference Off
                    Toast.makeText(getApplicationContext(), "Time OFF", Toast.LENGTH_SHORT).show();
                }
                Log.d("Time Preference", Boolean.valueOf(isChecked).toString());
            }
        });

        final Button interval = (Button) findViewById(R.id.intervalButton);
        interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputInterval = (EditText) findViewById(R.id.inputInterval);

                SharedPreferences sharedPreferences = getSharedPreferences("interval", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("newInterval", inputInterval.getText().toString());

                editor.apply();
                changeInterval();
            }
        });

    }

    public void changeInterval() {
        SharedPreferences sharedPreferences = getSharedPreferences("newInterval", MODE_PRIVATE);
        Toast.makeText(getApplicationContext(), "Changed Update Interval", Toast.LENGTH_SHORT).show();
        //ToDO change update interval
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
}
