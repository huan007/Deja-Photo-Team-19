package com.android.dejaphoto;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
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

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle saveInstanceState) {
            super.onCreate(saveInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            SwitchPreference dejaVu = (SwitchPreference) findPreference("DejaVu");

            dejaVu.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object object) {
                    //ToDo
                    if (object != null) {
                        Toast.makeText(getContext(), "Deja Vu Mode ON", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Deja Vu Mode OFF", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            SwitchPreference location = (SwitchPreference) findPreference("Location");

            location.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object object) {
                    //ToDo
                    if (object != null) {
                        Toast.makeText(getContext(), "Location ON", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Location OFF", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            SwitchPreference dayWeek = (SwitchPreference) findPreference("DayOfWeek");

            dayWeek.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object object) {
                    //ToDo
                    if (object != null) {
                        Toast.makeText(getContext(), "Day of Week ON", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Day of Week OFF", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            SwitchPreference time = (SwitchPreference) findPreference("Time");

            time.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object object) {
                    //ToDo
                    if (object != null) {
                        Toast.makeText(getContext(), "Time ON", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Time OFF", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            final EditTextPreference interval = (EditTextPreference) findPreference("interval");

            interval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object object) {
                    //ToDo
                    Toast.makeText(getContext(), "Changed Refresh Interval", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
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
}
