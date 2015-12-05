package com.plazonic.tomislav.yambfriends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final Dice dice = new Dice(5);
        final int[] diceIds = {R.id.diceView1, R.id.diceView2, R.id.diceView3, R.id.diceView4, R.id.diceView5, R.id.diceView6};
        final Map<String, ImageView> ivDice = new HashMap<>(dice.getQuantity(), 1);
        if (dice.getQuantity() < 6) ((ViewGroup) findViewById(R.id.diceView6).getParent()).removeView(findViewById(R.id.diceView6));
        for (int i = 0; i < dice.getQuantity(); i++) {
            ivDice.put("dice_" + (i + 1), (ImageView) findViewById(diceIds[i]));
        }

        final Grid grid = new Grid(true); // change constant to input value
        GridView gvGrid = (GridView) findViewById(R.id.gridView);
        gvGrid.setNumColumns(grid.getNumOfCols(false));

        ListAdapter gvAdapter = new ArrayAdapter<>(this, R.layout.grid_cell, grid.getListCells());
        gvGrid.setAdapter(gvAdapter);
        gvGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cellName = grid.positionToCellName(position);
                int nCol = grid.getNumOfCols(false);
                if (position / nCol == 0 || position % nCol == 0) {
                    String text = getResources().getString(getResources().getIdentifier("_" + cellName, "string", getPackageName()));
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                } else if (grid.getAvailableCells().contains(grid.positionToCellName(position))) {
                    //if ()
                    /*
                    treat position as an id or as a cellName?
                    rethink variable names
                    if (isAvailable(position)) {
                        if (roll == 1 && col == 'ann' && !announced[0]) {
                            make all cells unavailable
                            make position available
                            announced = [true, position]
                        }
                    } else {
                        calculatedInput = calculateInput(rowString or position);
                        save calculatedInput to model
                        display calculatedInput to view
                        lastInput = [modelPosition, position]
                        inputDone = true
                        make all cells unavailable
                    }
                    checkEnd()
                    if (checkFin()) {
                        finalResult = sum of all sub-sums
                        save game to database
                        inform user of final result and game finish
                    }
                     */
                    // if (value == "") this.handleClickedInput(cellName);

                    // updateModel
                    grid.updateModelValue(grid.positionToCellName(position), position);
                    TextView tv = (TextView) view;
                    tv.setText(String.format("%d", position));
                }
            }
        });

        Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> randoms = dice.getRandoms();
                for (int i = 0; i < dice.getQuantity(); i++) {
                    ivDice.get("dice_" + (i + 1)).setImageResource(getResources().getIdentifier("dice_" + randoms.get(i), "drawable", getPackageName()));
                }
            }
        });
    }
}