package com.android.dejaphoto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

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

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences.Editor editor = getContext().getSharedPreferences("settings", MODE_PRIVATE).edit();

            // set button change listener for Interval Setting
            findPreference("interval").setOnPreferenceChangeListener((preference, newValue) -> {
                editor.putInt("interval", Integer.valueOf((String) newValue));
                Log.d("interval value", "change to " + newValue);
                return true;
            });

            // set button change listener for Deja Vu Mode Setting
            findPreference("dejavu").setOnPreferenceChangeListener((preference, newValue) -> {
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
            });

            // set button change listener for Location Setting
            findPreference("location").setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    // unchecked -> checked
                } else {
                    // checked -> unchecked
                }

                Log.d("deja value", newValue.toString());
                editor.putBoolean("location", (Boolean) newValue);
                return true;
            });

            // set button change listener for Day of Week Setting
            findPreference("day").setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    // unchecked -> checked
                } else {
                    // checked -> unchecked
                }

                Log.d("deja value", newValue.toString());
                editor.putBoolean("day", (Boolean) newValue);
                return true;
            });

            // set button change listener for Time of Day Setting
            findPreference("time").setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    // unchecked -> checked
                } else {
                    // checked -> unchecked
                }

                Log.d("deja value", newValue.toString());
                editor.putBoolean("time", (Boolean) newValue);
                return true;
            });

            editor.apply();
        }
    }
}
