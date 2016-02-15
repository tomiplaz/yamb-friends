package com.plazonic.tomislav.yambfriends;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GameActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String username;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    private MediaPlayer diceRollSoundPlayer;
    private boolean soundOn;

    private Grid grid;
    private GridView gvGrid;
    private ArrayAdapter<String> gvAdapter;
    private Dice dice;
    private Map<String, ImageView> ivDice;
    private boolean diceClickable;
    private TextView tvRollNo;
    private Chronometer cmTimer;
    private long cmTimerElapsed;
    private Button btnRoll, btnUndo;

    private GoogleApiClient googleApiClient;
    private double lastLatitude = 0, lastLongitude = 0;

    private ProgressDialog progressDialog;
    private RestApi restApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        username = settings.getString("username", null);

        if (settings.getString("settings_handedness", "Right-handed").equals("Left-handed")) {
            LinearLayout llOtherLayout = (LinearLayout) findViewById(R.id.other_layout);
            List<View> childViews = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                childViews.add(llOtherLayout.getChildAt(i));
            }
            llOtherLayout.removeAllViews();
            for (int i = 3; i >= 0; i--) {
                llOtherLayout.addView(childViews.get(i));
            }
        }

        if (settings.getBoolean("settings_shake_roll", false)) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetector = new ShakeDetector(settings.getString("settings_shake_sensitivity", "Medium"));
            shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
                @Override
                public void onShake() {
                    rollEvent();
                }
            });
        }

        soundOn = settings.getBoolean("settings_sound", true);

        dice = new Dice(Integer.parseInt(settings.getString("settings_dice_count", "5")));
        int[] ivDiceIds = {R.id.dice_view_1, R.id.dice_view_2, R.id.dice_view_3, R.id.dice_view_4, R.id.dice_view_5, R.id.dice_view_6};
        ivDice = new HashMap<>(dice.getQuantity(), 1);
        if (dice.getQuantity() < 6) ((ViewGroup) findViewById(R.id.dice_view_6).getParent()).removeView(findViewById(R.id.dice_view_6));
        for (int i = 0; i < dice.getQuantity(); i++) {
            ivDice.put("ivDice" + (i + 1), (ImageView) findViewById(ivDiceIds[i]));
            ivDice.get("ivDice" + (i + 1)).setTag(false);
        }

        tvRollNo = (TextView) findViewById(R.id.roll_number);
        tvRollNo.setText(String.format("%d", dice.getRollNumber()));

        cmTimer = (Chronometer) findViewById(R.id.timer);
        cmTimerElapsed = 0;

        btnRoll = (Button) findViewById(R.id.roll_button);
        btnUndo = (Button) findViewById(R.id.undo_button);

        grid = new Grid(settings.getBoolean("settings_an0_column", false));
        gvGrid = (GridView) findViewById(R.id.gridview);
        gvGrid.setNumColumns(grid.getNumOfCols(false));
        gvAdapter = new ArrayAdapter<>(this, R.layout.grid_cell, grid.getListCells());
        gvGrid.setAdapter(gvAdapter);
        gvGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridCellClickEvent(position);
            }
        });

        LinearLayout llGridView = (LinearLayout) findViewById(R.id.gridview_layout);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        llGridView.getLayoutParams().width = (int) Math.ceil(53 * metrics.density) * grid.getNumOfCols(false);

        btnRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollEvent();
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoEvent();
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        restApi = new RestAdapter.Builder()
                .setEndpoint(RestApi.END_POINT)
                .build()
                .create(RestApi.class);
    }

    @Override
    protected void onPause() {
        cmTimer.stop();
        cmTimerElapsed = SystemClock.elapsedRealtime() - cmTimer.getBase();
        if (sensorManager != null) sensorManager.unregisterListener(shakeDetector);
        if (diceRollSoundPlayer != null) diceRollSoundPlayer.release();
        diceRollSoundPlayer = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        cmTimer.setBase(SystemClock.elapsedRealtime() - cmTimerElapsed);
        cmTimer.start();
        if (sensorManager != null) sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (diceRollSoundPlayer == null) diceRollSoundPlayer = MediaPlayer.create(this, R.raw.sound_dice_roll);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        cmTimerElapsed = SystemClock.elapsedRealtime() - cmTimer.getBase();
        cmTimer.stop();
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), R.string.google_api_client_connection_fail, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest locationRequest = LocationRequest.create();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLatitude = location.getLatitude();
                lastLongitude = location.getLongitude();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Toast.makeText(getApplicationContext(), R.string.google_api_client_connection_interrupted, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (!grid.isGameFinished()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setTitle(R.string.end_game_dialog_title)
                    .setMessage(R.string.end_game_dialog_message)
                    .setPositiveButton(R.string.end_game_dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gameForfeited();
                        }
                    })
                    .setNegativeButton(R.string.end_game_dialog_negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    public void diceClick(View v) {
        ImageView iv = (ImageView) v;
        if ((boolean) iv.getTag()) {
            iv.clearColorFilter();
            iv.setTag(false);
        } else {
            iv.setColorFilter(R.color.diceGray);
            iv.setTag(true);
        }
    }

    private void gridCellClickEvent(int position) {
        String clickedCellName = grid.positionToCellName(position);
        int nCol = grid.getNumOfCols(false);
        if (position / nCol == 0 || position % nCol == 0) {
            String text = getResources().getString(getResources().getIdentifier("_" + clickedCellName, "string", getPackageName()));
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        } else if (grid.getAvailableCellsNames().contains(clickedCellName)) {
            if (((dice.getRollNumber() == 0 && grid.getCellColName(clickedCellName).equals("an0")) ||
                    (dice.getRollNumber() == 1 && grid.getCellColName(clickedCellName).equals("an1"))) &&
                    grid.getAnnouncedCellName() == null) {
                grid.setAnnouncedCellName(clickedCellName);
                grid.updateAvailableCellsNames(dice.getRollNumber());

                // For An0 column announcement.
                if (dice.getRollNumber() == 0) {
                    grid.setInputDone(false);
                    diceSetClickable(false);
                }

                Toast.makeText(getApplicationContext(), "Announced: " + clickedCellName.replace("_", "-"), Toast.LENGTH_SHORT).show();
            } else if (dice.getRollNumber() != 0) {
                unclickDice();
                diceSetClickable(false);

                int result = dice.calculateInput(grid.getCellRowName(clickedCellName));
                grid.setModelValue(clickedCellName, result);
                grid.updateListCells(clickedCellName, Integer.toString(result));
                gvAdapter.notifyDataSetChanged();

                grid.setLastInputCellName(clickedCellName);
                dice.setLastRollNumber(dice.getRollNumber());
                grid.setInputDone(true);
                dice.setRollNumber(0);
                tvRollNo.setText(String.format("%d", dice.getRollNumber()));
                grid.clearAvailableCellsNames();
                grid.setAnnouncedCellName(null);
            }

            grid.checkCompletedSections();
            if (!grid.getLastSumCellsNames().isEmpty()) {
                List<String> lastSumCellsNames = grid.getLastSumCellsNames();

                // Update view with lastSumCellsNames.
                for (int i = 0; i < lastSumCellsNames.size(); i++) {
                    grid.updateListCells(lastSumCellsNames.get(i), Integer.toString(grid.getModelValue(lastSumCellsNames.get(i))));
                    gvAdapter.notifyDataSetChanged();
                }
            }

            if (grid.isGameFinished()) {
                disableUI();
                cmTimer.stop();
                grid.calculateFinalResult();

                if (googleApiClient.isConnected()) {
                    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if (lastLocation != null) {
                        lastLatitude = lastLocation.getLatitude();
                        lastLongitude = lastLocation.getLongitude();
                    }
                }

                insertGame();
            }
        }
    }

    private void diceRolling() {
        if (soundOn && diceRollSoundPlayer != null) diceRollSoundPlayer.start();
        for (int i = 0; i < dice.getQuantity(); i++) {
            if (!(boolean) ivDice.get("ivDice" + (i + 1)).getTag()) {
                int newRandom = dice.getRandom();
                dice.setDice(i, newRandom);
                ivDice.get("ivDice" + (i + 1)).setImageResource(getResources().getIdentifier("dice_" + newRandom, "drawable", getPackageName()));
            }
        }
    }

    private void rollEvent() {
        if (!grid.isGameFinished()) {
            if (!diceClickable) diceSetClickable(true);

            if (!grid.getInputDone() && dice.getRollNumber() == 3) {
                Toast.makeText(getApplicationContext(), "Input required!", Toast.LENGTH_SHORT).show();
            }

            if (grid.getInputDone()) {
                unclickDice();
                grid.setInputDone(false);
                grid.setAnnouncedCellName(null);
            }

            if (dice.getRollNumber() < 3 && !grid.getInputDone()) {
                if (grid.isAnnouncementRequired(dice.getRollNumber())) {
                    Toast.makeText(getApplicationContext(), "Announcement required!", Toast.LENGTH_SHORT).show();
                } else {
                    dice.incrementRollNumber();
                    tvRollNo.setText(String.format("%d", dice.getRollNumber()));

                    diceRolling();

                    if (grid.getAnnouncedCellName() == null) grid.updateAvailableCellsNames(dice.getRollNumber());
                }
            }

            if (dice.getRollNumber() == 3) {
                unclickDice();
                diceSetClickable(false);
            }

            tvRollNo.setText(String.format("%d", dice.getRollNumber()));
        }
    }

    private void undoEvent() {
        if (!grid.isGameFinished()) {
            if (grid.getInputDone()) {
                diceSetClickable(true);
                dice.setRollNumber(dice.getLastRollNumber());
                tvRollNo.setText(String.format("%d", dice.getRollNumber()));

                grid.setModelValue(grid.getLastInputCellName(), -1);
                grid.updateListCells(grid.getLastInputCellName(), "");
                gvAdapter.notifyDataSetChanged();

                if (grid.getLastInputCellName().contains("an1") || grid.getLastInputCellName().contains("an0")) {
                    grid.setAnnouncedCellName(grid.getLastInputCellName());
                }
                grid.updateAvailableCellsNames(dice.getRollNumber());

                if (grid.getLastSumCellsNames().size() > 0) {
                    for (int i = 0; i < grid.getLastSumCellsNames().size(); i++) {
                        grid.setModelValue(grid.getLastSumCellsNames().get(i), -1);
                        grid.updateListCells(grid.getLastSumCellsNames().get(i), "");
                        gvAdapter.notifyDataSetChanged();
                    }
                }

                grid.setInputDone(false);
            } else if (grid.getAnnouncedCellName() != null) {
                if (dice.getRollNumber() == 0 || (dice.getRollNumber() == 1 && grid.getAnnouncedCellName().contains("an1"))) {
                    grid.setAnnouncedCellName(null);
                    grid.updateAvailableCellsNames(dice.getRollNumber());
                }
            }
        }
    }

    private void disableUI() {
        gvGrid.setClickable(false);
        diceSetClickable(false);
        btnRoll.setClickable(false);
        btnUndo.setClickable(false);
    }

    private void diceSetClickable(boolean clickable) {
        diceClickable = clickable;
        for (int i = 0; i < dice.getQuantity(); i++) {
            ivDice.get("ivDice" + (i + 1)).setClickable(clickable);
        }
    }

    private void unclickDice() {
        for (int i = 0; i < dice.getQuantity(); i++) {
            ivDice.get("ivDice" + (i + 1)).setTag(false);
            ivDice.get("ivDice" + (i + 1)).clearColorFilter();
        }
    }

    private String getGameType(int diceQuantity, int numberOfColumns) {
        String gameType = "";

        gameType += (numberOfColumns == 4 ? "an1" : "an0");
        gameType += "d" + diceQuantity;

        return gameType;
    }

    private void insertGame() {
        String type = getGameType(dice.getQuantity(), grid.getNumOfCols(true));
        String game = grid.getGameString();
        int result = grid.getFinalResult();
        int duration = (int) ((SystemClock.elapsedRealtime() - cmTimer.getBase()) / 1000);
        float latitude = (float) lastLatitude;
        float longitude = (float) lastLongitude;

        Toast.makeText(getApplicationContext(), "Final result: " + result, Toast.LENGTH_LONG).show();

        showProgressDialog();
        restApi.insertGame(username, type, game, result, duration, latitude, longitude, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                hideProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.unsuccessful_http_response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gameForfeited() {
        restApi.gameForfeited(username, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), R.string.unsuccessful_http_response, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Saving...");
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
