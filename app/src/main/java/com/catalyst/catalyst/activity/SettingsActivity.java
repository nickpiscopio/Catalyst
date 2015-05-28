package com.catalyst.catalyst.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.catalyst.catalyst.R;


public class SettingsActivity extends AppCompatActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                                                        new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private Context context;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.prefs);

            context = getActivity().getApplicationContext();

//            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
//            MultiSelectListPreference intervalPreference = (MultiSelectListPreference) findPreference(context.getResources().getString(R.string.preference_interval));
//            intervalPreference.setSummary(
//                    sp.getString(context.getResources().getString(R.string.preference_interval),
//                                 ""));

            populateSummary(context.getResources().getString(R.string.preference_interval));
        }

        public void onResume()
        {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                                 .registerOnSharedPreferenceChangeListener(this);
        }

        public void onPause()
        {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                                 .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key)
        {
            populateSummary(context.getResources().getString(R.string.preference_interval));
        }

        private void populateSummary(String key)
        {
            Preference pref = findPreference(key);
            if (pref instanceof MultiSelectListPreference) {
                MultiSelectListPreference etp = (MultiSelectListPreference) pref;

                String valueString = "";

                String[] values = etp.getValues().toArray(new String[etp.getValues().size()]);

                int length = values.length;

                for (int i = 0; i < values.length; i++)
                {
                    valueString += (i != length - 1) ? values[i] + ", " : values[i];
                }

                pref.setSummary(valueString);
            }
        }
    }
}
