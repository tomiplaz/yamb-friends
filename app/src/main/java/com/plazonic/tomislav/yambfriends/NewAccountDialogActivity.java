package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewAccountDialogActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private SharedPreferences settings;
    private EditText etUsername, etPassword;
    private String username, password;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_dialog);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        etUsername = (EditText) findViewById(R.id.new_account_username);
        etPassword = (EditText) findViewById(R.id.new_account_password);

        findViewById(R.id.google_sign_in_button_new_account).setOnClickListener(this);
        findViewById(R.id.new_account_create_button).setOnClickListener(this);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button_new_account:
                createNewAccountViaGoogle();
                break;
            case R.id.new_account_create_button:
                createNewAccountRegularly();
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), R.string.google_api_client_connection_fail, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignInActivity.RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()) {
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                username = googleSignInAccount.getDisplayName();
                password = googleSignInAccount.getId();

                Auth.GoogleSignInApi.revokeAccess(googleApiClient);

                createNewAccount();
            } else {
                Toast.makeText(getApplicationContext(), R.string.google_sign_in_fail, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createNewAccountViaGoogle() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(googleSignInIntent, SignInActivity.RC_GOOGLE_SIGN_IN);
    }

    private void createNewAccountRegularly() {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        createNewAccount();
    }

    private void createNewAccount() {
        RestApi restApi = new RestAdapter.Builder()
                .setEndpoint(RestApi.END_POINT)
                .build()
                .create(RestApi.class);

        restApi.createNewAccount(username, password, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String responseString = bufferedReader.readLine();

                    if (responseString.equals("Account created!")) {
                        settings.edit().putString("username", username).apply();
                    }
                    Toast.makeText(getApplicationContext(),responseString, Toast.LENGTH_SHORT).show();

                    finish();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), R.string.unsuccessful_http_response, Toast.LENGTH_LONG).show();
            }
        });
    }

}
