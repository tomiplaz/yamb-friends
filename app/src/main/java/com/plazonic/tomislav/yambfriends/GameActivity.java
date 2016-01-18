package com.plazonic.tomislav.yambfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;

    private MediaPlayer diceRollSoundPlayer;
    private boolean soundOn;

    private TextView tvRollNo;
    private Chronometer cmTimer;
    private long cmTimerElapsed;
    private Dice dice;
    private Map<String, ImageView> ivDice;
    private Grid grid;
    private ArrayAdapter<String> gvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        if (settings.getString("settings_handedness", "Right-handed").equals("Left-handed")) {
            LinearLayout otherLayout = (LinearLayout) findViewById(R.id.otherLayout);
            List<View> childViews = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                childViews.add(otherLayout.getChildAt(i));
            }
            otherLayout.removeAllViews();
            for (int i = 3; i >= 0; i--) {
                otherLayout.addView(childViews.get(i));
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
        int[] diceIds = {R.id.diceView1, R.id.diceView2, R.id.diceView3, R.id.diceView4, R.id.diceView5, R.id.diceView6};
        ivDice = new HashMap<>(dice.getQuantity(), 1);
        if (dice.getQuantity() < 6) ((ViewGroup) findViewById(R.id.diceView6).getParent()).removeView(findViewById(R.id.diceView6));
        for (int i = 0; i < dice.getQuantity(); i++) {
            ivDice.put("ivDice" + (i + 1), (ImageView) findViewById(diceIds[i]));
            ivDice.get("ivDice" + (i + 1)).setTag(false);
        }

        tvRollNo = (TextView) findViewById(R.id.rollNo);
        tvRollNo.setText(String.format("%d", dice.getRollNumber()));

        cmTimer = (Chronometer) findViewById(R.id.timer);
        cmTimerElapsed = 0;

        grid = new Grid(settings.getBoolean("settings_an0_column", false));
        GridView gvGrid = (GridView) findViewById(R.id.gridView);
        gvGrid.setNumColumns(grid.getNumOfCols(false));
        gvAdapter = new ArrayAdapter<>(this, R.layout.grid_cell, grid.getListCells());
        gvGrid.setAdapter(gvAdapter);
        gvGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridCellClickEvent(position);
            }
        });

        Button btnRoll = (Button) findViewById(R.id.rollBtn);
        btnRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollEvent();
            }
        });

        Button btnUndo = (Button) findViewById(R.id.undoBtn);
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoEvent();
            }
        });
    }

    @Override
    protected void onPause() {
        cmTimerElapsed = SystemClock.elapsedRealtime() - cmTimer.getBase();
        cmTimer.stop();
        sensorManager.unregisterListener(shakeDetector);
        diceRollSoundPlayer.release();
        diceRollSoundPlayer = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        cmTimer.setBase(SystemClock.elapsedRealtime() - cmTimerElapsed);
        cmTimer.start();
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        diceRollSoundPlayer = MediaPlayer.create(this, R.raw.sound_dice_roll);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        cmTimerElapsed = SystemClock.elapsedRealtime() - cmTimer.getBase();
        cmTimer.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(R.string.end_game_dialog_title)
                .setMessage(R.string.end_game_dialog_message)
                .setPositiveButton(R.string.end_game_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.end_game_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
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

    public void gridCellClickEvent(int position) {
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
            } else {
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
                grid.calculateFinalResult();
                Toast.makeText(getApplicationContext(), "Final result: " + grid.getFinalResult(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void diceRolling() {
        if (soundOn && diceRollSoundPlayer != null) diceRollSoundPlayer.start();
        SystemClock.sleep(500);
        for (int i = 0; i < dice.getQuantity(); i++) {
            if (!(boolean) ivDice.get("ivDice" + (i + 1)).getTag()) {
                int newRandom = dice.getRandom();
                dice.setDice(i, newRandom);
                ivDice.get("ivDice" + (i + 1)).setImageResource(getResources().getIdentifier("dice_" + newRandom, "drawable", getPackageName()));
            }
        }
    }

    public void rollEvent() {
        if (!grid.isGameFinished()) {
            if (!grid.getInputDone() && dice.getRollNumber() == 3) {
                Toast.makeText(getApplicationContext(), "Input required!", Toast.LENGTH_SHORT).show();
            }

            if (grid.getInputDone()) {
                for (int i = 0; i < dice.getQuantity(); i++) {
                    ivDice.get("ivDice" + (i + 1)).setTag(false);
                    ivDice.get("ivDice" + (i + 1)).clearColorFilter();
                }
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

            tvRollNo.setText(String.format("%d", dice.getRollNumber()));
        }
    }

    public void undoEvent() {
        if (!grid.isGameFinished()) {
            if (grid.getInputDone()) {
                dice.setRollNumber(dice.getLastRollNumber());
                tvRollNo.setText(String.format("%d", dice.getRollNumber()));
                grid.setModelValue(grid.getLastInputCellName(), -1);
                grid.updateListCells(grid.getLastInputCellName(), "");
                gvAdapter.notifyDataSetChanged();
                grid.updateAvailableCellsNames(dice.getRollNumber());

                if (grid.getLastSumCellsNames().size() > 0) {
                    for (int i = 0; i < grid.getLastSumCellsNames().size(); i++) {
                        grid.setModelValue(grid.getLastSumCellsNames().get(i), -1);
                        grid.updateListCells(grid.getLastSumCellsNames().get(i), "");
                        gvAdapter.notifyDataSetChanged();
                    }
                }

                grid.setInputDone(false);
            } else if ((dice.getRollNumber() == 0 || dice.getRollNumber() == 1) && grid.getAnnouncedCellName() != null) {
                grid.setAnnouncedCellName(null);
                grid.updateAvailableCellsNames(dice.getRollNumber());
            }
        }
    }

}
