package com.android.dejaphoto;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
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
                if(isChecked) {
                    // TO DO
                    Toast.makeText( getApplicationContext(), "Deja Vu Mode ON", Toast.LENGTH_SHORT).show();
                } else {
                    // TO DO
                    Toast.makeText( getApplicationContext(), "Deja Vu Mode OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Switch switchLocation = (Switch) findViewById(R.id.Location);
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText( getApplicationContext(), "Location ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText( getApplicationContext(), "Locatoin OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Switch switchDay = (Switch) findViewById(R.id.DayOfWeek);
        switchDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                   Toast.makeText( getApplicationContext(), "Day of Week ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText( getApplicationContext(), "Day of Week OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Switch switchTime = (Switch) findViewById(R.id.Time);
        switchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText( getApplicationContext(), "Time ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText( getApplicationContext(), "Time OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
