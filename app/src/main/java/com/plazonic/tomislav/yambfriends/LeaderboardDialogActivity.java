package com.plazonic.tomislav.yambfriends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LeaderboardDialogActivity extends AppCompatActivity {

    private String type;
    private Map<String, TextView> tvLeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_dialog);

        TextView tvLeaderboardTitle = (TextView) findViewById(R.id.leaderboard_title);

        int[] tvLeadersIds = {
                R.id.leaderboard_no1_value,
                R.id.leaderboard_no2_value,
                R.id.leaderboard_no3_value,
                R.id.leaderboard_no4_value,
                R.id.leaderboard_no5_value,
                R.id.leaderboard_no6_value,
                R.id.leaderboard_no7_value,
                R.id.leaderboard_no8_value,
                R.id.leaderboard_no9_value,
                R.id.leaderboard_no10_value
        };
        tvLeaders = new HashMap<>(10, 1);
        for (int i = 0; i < 10; i++) {
            tvLeaders.put("tvLeader" + (i + 1), (TextView) findViewById(tvLeadersIds[i]));
        }

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

        tvLeaderboardTitle.setText(type + " leaderboard");
        getTop10();
    }

    private void getResult(final int number) {
        RestApi restApi = new RestAdapter.Builder()
                .setEndpoint(RestApi.END_POINT)
                .build()
                .create(RestApi.class);

        restApi.getResult(type, number, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    InputStream inputStream = response.getBody().in();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String responseString = bufferedReader.readLine();

                    if (responseString.contains("PHP Error")) {
                        Toast.makeText(getApplicationContext(), R.string.php_error, Toast.LENGTH_SHORT).show();
                    } else if (!responseString.equals("Result not found.")) {
                        tvLeaders.get("tvLeader" + number).setText(responseString);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), R.string.unsuccessful_http_response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTop10() {
        for (int i = 1; i < 11; i++) {
            getResult(i);
        }
    }
}
