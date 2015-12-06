package com.plazonic.tomislav.yambfriends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tomo on 10/31/2015.
 */
public class Grid {
    // use JavaDoc extensively
    private final List<String> ROW_NAMES = Arrays.asList("1", "2", "3", "4", "5", "6", "eq1", "max", "min", "eq2", "str", "ful", "pok", "ymb", "eq3");
    private final List<String> COL_NAMES = Arrays.asList("dwn", "any", "up", "an1", "an0");
    private Map<String, Integer> gameModel;
    private List<String> listCells;
    private int numOfCols;
    private String announcedCellName = null;
    private boolean inputDone = false;

    Grid(boolean preRollAnnouncement) {
        // Set number of columns to 5 if preRollAnnouncement column is included
        this.numOfCols = preRollAnnouncement ? 5 : 4;

        // Initialize model (map each cellName to -1, e.g. "1_dwn": -1)
        this.gameModel = new HashMap<>(15 * this.numOfCols, 1);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < this.numOfCols; j++) {
                this.gameModel.put(this.ROW_NAMES.get(i) + "_" + this.COL_NAMES.get(j), -1);
            }
        }

        // Initialize List for GridView (add each cellValue to List in order)
        listCells = new ArrayList<>(16 * (this.numOfCols + 1));
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < this.numOfCols + 1; j++) {
                if (j == 0) listCells.add(i == 0 ? "X" : this.ROW_NAMES.get(i - 1));
                else listCells.add(i == 0 ? this.COL_NAMES.get(j - 1) : "");
            }
        }
    }

    public int getNumOfCols(boolean playable) {
        return playable ? numOfCols : numOfCols + 1;
    }

    public List getAvailableCells() {
        List<String> availableCells = new ArrayList<>((this.numOfCols - 2) * 12 + 2);
        String currentCellName;
        String cellNameToAdd = null;

        if (this.announcedCellName != null) {
            availableCells.add(announcedCellName);
        } else {
            for (int i = 0; i < this.numOfCols; i++) {
                rowIterator: for (int j = 0; j < 15; j++) {
                    if (this.ROW_NAMES.get(j).contains("eq")) continue;
                    currentCellName = this.ROW_NAMES.get(j) + "_" + this.COL_NAMES.get(i);
                    switch (this.COL_NAMES.get(i)) {
                        case "dwn":
                            if (this.gameModel.get(currentCellName).equals(-1)) {
                                availableCells.add(currentCellName);
                                break rowIterator;
                            } else break;
                        case "up":
                            if (this.gameModel.get(currentCellName).equals(-1)) cellNameToAdd = currentCellName;
                            if (j == 13 && cellNameToAdd != null) availableCells.add(cellNameToAdd);
                            break;
                        default:
                            if (this.gameModel.get(currentCellName).equals(-1)) availableCells.add(currentCellName);
                    }
                }
            }
        }

        return availableCells;
    }

    public boolean onlyLeftAn1() {
        return this.getAvailableCells().contains("^(dwn|any|up)");
    }

    public boolean isGameFinished() {
        // ...
        return false;
    }

    public String getAnnouncedCellName() {
        return this.announcedCellName;
    }

    public void setAnnouncedCellName(String cellName) {
        this.announcedCellName = cellName;
    }

    public boolean getInputDone() {
        return this.inputDone;
    }

    public void setInputDone(boolean value) {
        this.inputDone = value;
    }

    public String positionToCellName(int position) {
        return this.ROW_NAMES.get(position / (this.numOfCols + 1) - 1) + "_" + this.COL_NAMES.get(position % (this.numOfCols + 1) - 1);
    }

    public int cellNameToPosition(String cellName) {
        String[] cellNameSplit = cellName.split("_");
        return (this.numOfCols + 1) * (this.ROW_NAMES.indexOf(cellNameSplit[0]) + 1) + (this.COL_NAMES.indexOf(cellNameSplit[1]) + 1);
    }

    public void updateModelValue(String cellName, Integer value) {
        this.gameModel.put(cellName, value);
    }

    public List<String> getListCells() {
        return listCells;
    }
}
