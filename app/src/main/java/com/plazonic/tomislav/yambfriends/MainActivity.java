package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startGame(View v) {
        startActivity(new Intent().setClass(getBaseContext(), GameActivity.class));
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
 *
 *
 *
 *
 *
 *
 */
