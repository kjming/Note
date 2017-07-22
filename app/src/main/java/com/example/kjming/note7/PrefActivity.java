package com.example.kjming.note7;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by Kjming on 5/1/2017.
 */

public class PrefActivity extends PreferenceActivity {
    private SharedPreferences mSharedPreferences;
    private Preference mDefaultColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mypreference);
        mDefaultColor = (Preference)findPreference("DEFAULT_COLOR");
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int color = mSharedPreferences.getInt("DEFAULT_COLOR",-1);
        if (color != -1) {
            mDefaultColor.setSummary(getString(R.string.default_color_summary)+": "+ItemActivity.getColors(color));
        }

    }


}
