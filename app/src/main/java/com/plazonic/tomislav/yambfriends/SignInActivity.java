package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient googleApiClient;
    private TextView tvProfileInfoSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        tvProfileInfoSignIn = (TextView) findViewById(R.id.profile_info_sign_in);

        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        findViewById(R.id.google_disconnect_button).setOnClickListener(this);

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        googleApiClient =
                new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (optionalPendingResult.isDone()) {
            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
            handleGoogleSignInResult(googleSignInResult);
        } else {
            updateUI(false, null);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                googleSignIn();
                break;
            case R.id.google_sign_out_button:
                googleSignOut();
                break;
            case R.id.google_disconnect_button:
                googleRevokeAccess();
                break;
            /*case R.id.facebook_sign_in_button:
                break;*/
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(googleSignInResult);
        }
    }

    private void googleSignIn() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false, null);
                    }
                }
        );
    }

    private void googleRevokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false, null);
                    }
                }
        );
    }

    private void handleGoogleSignInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            updateUI(true, googleSignInAccount);
        } else {
            updateUI(false, null);
        }
    }

    private void updateUI(boolean signedIn, GoogleSignInAccount googleSignInAccount) {
        if (signedIn) {
            findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.google_sign_out_disconnect).setVisibility(View.VISIBLE);
            tvProfileInfoSignIn.setText("Signed in as \n" + googleSignInAccount.getDisplayName() + "\n" + googleSignInAccount.getId());
        } else {
            findViewById(R.id.google_sign_out_disconnect).setVisibility(View.GONE);
            findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            tvProfileInfoSignIn.setText(R.string.not_signed_in);
        }
    }

}
