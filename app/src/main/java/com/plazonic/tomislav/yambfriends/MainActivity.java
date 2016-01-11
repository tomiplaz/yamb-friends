package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private GoogleApiClient googleApiClient;
    private TextView tvProfileInfoMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvProfileInfoMain = (TextView) findViewById(R.id.profile_info_main);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        googleApiClient =
                new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (optionalPendingResult.isDone()) {
            Log.d(TAG, "Successful cached sign in");
            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
            if (googleSignInResult.isSuccess()) {
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                tvProfileInfoMain.setText("Signed in as \n" + googleSignInAccount.getDisplayName() + "\n" + googleSignInAccount.getId());
            } else {
                tvProfileInfoMain.setText(R.string.not_signed_in);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    public void startGame(View v) {
        startActivity(new Intent().setClass(getBaseContext(), GameActivity.class));
    }

    public void goToSettings(View v) {
        startActivity(new Intent().setClass(getBaseContext(), SettingsActivity.class));
    }

    public void goToSignIn(View v) {
        startActivity(new Intent().setClass(getBaseContext(), SignInActivity.class));
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
 * enable ide's vertical line and fix formatting
 *
 */
