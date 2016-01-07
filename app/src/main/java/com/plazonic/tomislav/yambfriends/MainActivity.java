package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    public void startGame(View v) {
        startActivity(new Intent().setClass(getBaseContext(), GameActivity.class));
    }

    public void goToSettings(View v) {
        startActivity(new Intent().setClass(getBaseContext(), SettingsActivity.class));
    }
}

/*
 * use tagging of gridview cells for available cells
 *
 * group dice under a group layout
 * group other views under another group layout
 * selected dice: new drawable or something else?
 * cell onclick: select cell; new cell onclick: do input
 * handle backbutton click while in game
 * stop timer onexit
 *
 * http://developer.android.com/guide/components/processes-and-threads.html
 *
 * available cells related actions to functions
 *
 *
 *
 */
