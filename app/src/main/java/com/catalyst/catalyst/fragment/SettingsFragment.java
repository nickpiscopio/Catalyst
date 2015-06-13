package com.catalyst.catalyst.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.util.CatalystDate;

/**
 * Settings fragment to show the preferences.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        context = getActivity().getApplicationContext();

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

    /**
     * Populates the summary of of a specific node.
     *
     * @param key   The key of the preference to populate.
     */
    private void populateSummary(String key)
    {
        Preference pref = findPreference(key);

        if (pref instanceof MultiSelectListPreference)
        {
            MultiSelectListPreference interval = (MultiSelectListPreference) pref;

            pref.setSummary(new CatalystDate(context).sortDate(interval.getValues()));
        }
    }
}