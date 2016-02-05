package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_GOOGLE_SIGN_IN = 7;

    private SharedPreferences settings;
    private TextView tvProfileInfoSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.sign_in_dialog_button).setOnClickListener(this);
        findViewById(R.id.new_account_dialog_button).setOnClickListener(this);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        tvProfileInfoSignIn = (TextView) findViewById(R.id.profile_info_sign_in);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(settings.getString("username", null) != null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_account_dialog_button:
                startNewAccountActivityDialog();
                break;
            case R.id.sign_in_dialog_button:
                startSignInActivityDialog();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            tvProfileInfoSignIn.setText("Signed in as:\n" + settings.getString("username", null));
            findViewById(R.id.sign_in_dialog_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            tvProfileInfoSignIn.setText(R.string.not_signed_in);
            findViewById(R.id.sign_in_dialog_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    private void signOut() {
        settings.edit().putString("username", null).apply();
        updateUI(false);
    }

    private void startSignInActivityDialog() {
        startActivity(new Intent().setClass(getBaseContext(), SignInDialogActivity.class));
    }

    private void startNewAccountActivityDialog() {
        startActivity(new Intent().setClass(getBaseContext(), NewAccountDialogActivity.class));
    }

}
