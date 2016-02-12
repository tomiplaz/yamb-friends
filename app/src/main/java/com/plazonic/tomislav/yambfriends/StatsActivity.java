package com.plazonic.tomislav.yambfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class StatsActivity extends AppCompatActivity implements View.OnClickListener {

    private RestApi restApi;
    private TextView tvRegisteredUsers;
    private Map<String, TextView> tvGamesPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        tvRegisteredUsers = (TextView) findViewById(R.id.registered_users_value);

        tvGamesPlayed = new HashMap<>(3, 1);
        tvGamesPlayed.put("total", (TextView) findViewById(R.id.total_games_played_value));
        tvGamesPlayed.put("registered", (TextView) findViewById(R.id.games_played_registered_value));
        tvGamesPlayed.put("anonymous", (TextView) findViewById(R.id.games_played_anonymous_value));

        findViewById(R.id.an1d5_leaderboard_button).setOnClickListener(this);
        findViewById(R.id.an1d6_leaderboard_button).setOnClickListener(this);
        findViewById(R.id.an0d5_leaderboard_button).setOnClickListener(this);
        findViewById(R.id.an0d6_leaderboard_button).setOnClickListener(this);

        restApi = new RestAdapter.Builder()
                .setEndpoint(RestApi.END_POINT)
                .build()
                .create(RestApi.class);

        getNumberOfUsers();
        for (String key : tvGamesPlayed.keySet()) {
            getNumberOfGamesPlayed(key);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.an1d5_leaderboard_button:
                openLeaderboard("an1d5");
                break;
            case R.id.an1d6_leaderboard_button:
                openLeaderboard("an1d6");
                break;
            case R.id.an0d5_leaderboard_button:
                openLeaderboard("an0d5");
                break;
            case R.id.an0d6_leaderboard_button:
                openLeaderboard("an0d6");
                break;
        }
    }

    private void getNumberOfUsers() {
        restApi.getNumberOfUsers(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    InputStream inputStream = response.getBody().in();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    tvRegisteredUsers.setText(bufferedReader.readLine());
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

    private void getNumberOfGamesPlayed(final String filter) {
        restApi.getNumberOfGamesPlayed(filter, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    InputStream inputStream = response.getBody().in();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    tvGamesPlayed.get(filter).setText(bufferedReader.readLine());
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

    private void openLeaderboard(String type) {
        Intent intent = new Intent().setClass(getBaseContext(), LeaderboardDialogActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
