package com.catalyst.catalyst.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.util.DateUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Settings fragment to show the preferences.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private Context context;

    private CheckBoxPreference notification;

    private MultiSelectListPreference interval;

    private Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        context = getActivity().getApplicationContext();

        res = context.getResources();

        notification = (CheckBoxPreference)findPreference(res.getString(R.string.preference_notification));

        interval = (MultiSelectListPreference)findPreference(res.getString(R.string.preference_interval));

        populateSummary(res.getString(R.string.preference_interval));
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
        Preference pref = findPreference(key);

        if (pref instanceof CheckBoxPreference)
        {
            if (interval.getValues().size() == 0)
            {
                interval.setValues(
                        new HashSet<>(Arrays.asList(res.getStringArray(R.array.interval))));
            }
        }
        else if (pref instanceof MultiSelectListPreference)
        {
            populateSummary(res.getString(R.string.preference_interval));
        }
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
            Set<String> days = interval.getValues();

            if (days.size() > 0)
            {
                pref.setSummary(new DateUtil(context).sortDate(interval.getValues()));
            }
            else
            {
                notification.setChecked(false);
            }
        }
    }
}