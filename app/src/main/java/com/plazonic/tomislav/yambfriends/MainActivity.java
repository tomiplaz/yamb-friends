package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences settings;
    private TextView tvProfileInfoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        tvProfileInfoMain = (TextView) findViewById(R.id.profile_info_main);

        findViewById(R.id.play_button).setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.profile_button).setOnClickListener(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settings.getString("username", null) != null) {
            findViewById(R.id.profile_button).setVisibility(View.VISIBLE);
            tvProfileInfoMain.setText("Signed in as:\n" + settings.getString("username", null));
        } else {
            findViewById(R.id.profile_button).setVisibility(View.GONE);
            tvProfileInfoMain.setText(R.string.not_signed_in);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_button:
                startGame();
                break;
            case R.id.settings_button:
                goToSettings();
                break;
            case R.id.sign_in_button:
                goToSignIn();
                break;
            case R.id.profile_button:
                goToProfile();
                break;
        }
    }

    private void startGame() {
        startActivity(new Intent().setClass(getBaseContext(), GameActivity.class));
    }

    private void goToSettings() {
        startActivity(new Intent().setClass(getBaseContext(), SettingsActivity.class));
    }

    private void goToSignIn() {
        startActivity(new Intent().setClass(getBaseContext(), SignInActivity.class));
    }

    private void goToProfile() {
        startActivity(new Intent().setClass(getBaseContext(), ProfileActivity.class));
    }

}

/*
 * group dice under a group layout
 * group other views under another group layout
 * selected dice: new drawable or something else?
 * toast for announcements
 *
 * http://developer.android.com/guide/components/processes-and-threads.html
 * disable gvGrid when game is finished
 * don't waste cpu time for an0 if an0 not included
 * available cells related actions to functions
 *
 */
