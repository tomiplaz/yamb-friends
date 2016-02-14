package com.plazonic.tomislav.yambfriends;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

public class MyStatsDialogActivity extends AppCompatActivity {

    private String username;
    private Map<String, TextView> tvMyStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stats_dialog);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        username = settings.getString("username", null);

        tvMyStats = new HashMap<>(13, 1);
        tvMyStats.put("time_registered", (TextView) findViewById(R.id.time_registered_value));
        tvMyStats.put("latitude", (TextView) findViewById(R.id.latitude_value));
        tvMyStats.put("longitude", (TextView) findViewById(R.id.longitude_value));
        tvMyStats.put("games_played", (TextView) findViewById(R.id.games_played_value));
        tvMyStats.put("games_forfeited", (TextView) findViewById(R.id.games_forfeited_value));
        tvMyStats.put("total_time_played", (TextView) findViewById(R.id.total_time_played_value));
        tvMyStats.put("average_game_duration", (TextView) findViewById(R.id.average_game_duration_value));
        tvMyStats.put("an1d5_best", (TextView) findViewById(R.id.an1d5_best_value));
        tvMyStats.put("an1d6_best", (TextView) findViewById(R.id.an1d6_best_value));
        tvMyStats.put("an0d5_best", (TextView) findViewById(R.id.an0d5_best_value));
        tvMyStats.put("an0d6_best", (TextView) findViewById(R.id.an0d6_best_value));
        tvMyStats.put("an1d5_average", (TextView) findViewById(R.id.an1d5_average_value));
        tvMyStats.put("an1d6_average", (TextView) findViewById(R.id.an1d6_average_value));
        tvMyStats.put("an0d5_average", (TextView) findViewById(R.id.an0d5_average_value));
        tvMyStats.put("an0d6_average", (TextView) findViewById(R.id.an0d6_average_value));

        for (String key : tvMyStats.keySet()) {
            getUserValue(key);
        }
    }

    private void getUserValue(final String field) {
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
                    String responseString = bufferedReader.readLine();

                    if (responseString.contains("PHP Error")) {
                        Toast.makeText(getApplicationContext(), R.string.php_error, Toast.LENGTH_SHORT).show();
                    } else {
                        switch (field) {
                            case "total_time_played":
                            case "average_game_duration":
                                int seconds = Integer.parseInt(responseString);
                                tvMyStats.get(field).setText(secondsToFormatedTime(seconds));
                                break;
                            case "time_registered":
                                String date = responseString.split(" ")[0];
                                tvMyStats.get(field).setText(date);
                                break;
                            default:
                                tvMyStats.get(field).setText(responseString);
                                break;
                        }
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

    private String secondsToFormatedTime(int seconds) {
        long s = seconds % 60;
        long min = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", h, min, s);
    }
}
