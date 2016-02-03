package com.plazonic.tomislav.yambfriends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import retrofit.RestAdapter;

public class NewAccountActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        etUsername = (EditText) findViewById(R.id.new_account_username);
        etPassword = (EditText) findViewById(R.id.new_account_password);
    }

    public void createNewAccount(View v) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://ugodnomjesto.net84.net/yambfriends")
                .build();

        restAdapter.create();
    }
}
