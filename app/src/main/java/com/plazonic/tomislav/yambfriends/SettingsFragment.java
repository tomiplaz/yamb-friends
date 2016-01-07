package com.plazonic.tomislav.yambfriends;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences settings, String key) {
            Preference changedPreference = findPreference(key);

            switch (key) {
                case "settings_preRollAnnouncementColumn":
                    changedPreference.setSummary(settings.getBoolean(key, false) ? "Include" : "Don't include");
                    break;
                case "settings_diceCount":
                    changedPreference.setSummary("Use " + settings.getString(key, "5") + " dice");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        // Set preferences' summaries onCreate
        SharedPreferences settings = getPreferenceScreen().getSharedPreferences();
        String key = "settings_preRollAnnouncementColumn";
        findPreference(key).setSummary(settings.getBoolean(key, false) ? "Include" : "Don't include");
        key = "settings_diceCount";
        findPreference(key).setSummary("Use " + settings.getString(key, "5") + " dice");
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

}
