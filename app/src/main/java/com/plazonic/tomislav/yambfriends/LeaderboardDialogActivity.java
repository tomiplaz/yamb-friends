package com.plazonic.tomislav.yambfriends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LeaderboardDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_dialog);

        String type;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                type = "an1d5";
            } else {
                type = extras.getString("type", "an1d5");
            }
        } else {
            type = (String) savedInstanceState.getSerializable("type");
            if (type == null) type = "an1d5";
        }

        ((TextView) findViewById(R.id.leaderboard_title)).setText(type + " leaderboard");
    }
}
