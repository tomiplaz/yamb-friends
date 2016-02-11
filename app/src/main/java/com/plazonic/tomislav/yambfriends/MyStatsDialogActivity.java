package com.plazonic.tomislav.yambfriends;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyStatsDialogActivity extends AppCompatActivity {

    private SharedPreferences settings;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stats_dialog);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        username = settings.getString("username", null);
    }

    private void getUserValue(String field) {
        RestApi restApi = new RestAdapter.Builder()
                .setEndpoint(RestApi.END_POINT)
                .build()
                .create(RestApi.class);

        restApi.getUserValue(username, field, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    InputStream inputStream = response.getBody().in();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    tv.setText(bufferedReader.readLine());
                } catch (IOException e) {
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
