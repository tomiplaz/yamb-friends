package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences settings;
    private TextView tvProfileInfoMain, tvAn0Info, tvDiceInfo;
    private Button btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        tvProfileInfoMain = (TextView) findViewById(R.id.profile_info_main);
        tvAn0Info = (TextView) findViewById(R.id.an0_info);
        tvDiceInfo = (TextView) findViewById(R.id.dice_info);
        btnProfile = (Button) findViewById(R.id.profile_button);

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
            btnProfile.setVisibility(View.VISIBLE);
            tvProfileInfoMain.setText("Signed in as:\n" + settings.getString("username", null));
        } else {
            btnProfile.setVisibility(View.GONE);
            tvProfileInfoMain.setText(R.string.not_signed_in);
        }

        if (settings.getBoolean("settings_an0_column", false)) {
            tvAn0Info.setText(R.string.an0_info_yes);
        } else {
            tvAn0Info.setText(R.string.an0_info_no);
        }

        if (settings.getString("settings_dice_count", "5").equals("5")) {
            tvDiceInfo.setText(R.string.dice_info_5);
        } else {
            tvDiceInfo.setText(R.string.dice_info_6);
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
