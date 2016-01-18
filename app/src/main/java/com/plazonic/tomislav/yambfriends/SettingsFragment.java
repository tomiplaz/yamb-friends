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
                case "settings_an0_column":
                    changedPreference.setSummary(settings.getBoolean(key, false) ? "Include" : "Don't include");
                    break;
                case "settings_dice_count":
                    changedPreference.setSummary("Use " + settings.getString(key, "5") + " dice");
                    break;
                case "settings_handedness":
                    changedPreference.setSummary(settings.getString(key, "Right-handed"));
                    break;
                case "settings_shake_roll":
                    changedPreference.setSummary(settings.getBoolean(key, false) ? "Enabled" : "Disabled");
                    break;
                case "settings_shake_sensitivity":
                    changedPreference.setSummary(settings.getString(key, "Medium"));
                    break;
                case "settings_sound":
                    changedPreference.setSummary(settings.getBoolean(key, true) ? "On" : "Off");
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        // Set preferences' summaries onCreate.
        SharedPreferences settings = getPreferenceScreen().getSharedPreferences();
        String key = "settings_an0_column";
        findPreference(key).setSummary(settings.getBoolean(key, false) ? "Include" : "Don't include");
        key = "settings_dice_count";
        findPreference(key).setSummary("Use " + settings.getString(key, "5") + " dice");
        key = "settings_handedness";
        findPreference(key).setSummary(settings.getString(key, "Right-handed"));
        key = "settings_shake_roll";
        findPreference(key).setSummary(settings.getBoolean(key, false) ? "Enabled" : "Disabled");
        key = "settings_shake_sensitivity";
        findPreference(key).setSummary(settings.getString(key, "Medium"));
        key = "settings_sound";
        findPreference(key).setSummary(settings.getBoolean(key, true) ? "On" : "Off");
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        super.onResume();
    }

}
