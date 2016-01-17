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
    private List<String> availableCellsNames;
    private String announcedCellName = null;
    private boolean inputDone = false;
    private String lastInputCellName = null;
    private List<String> lastSumCellsNames = new ArrayList<>(2);
    private int finalResult = -1;

    Grid(boolean an0Column) {
        // Set number of columns to 5 if an0Column is included
        this.numOfCols = an0Column ? 5 : 4;

        // Initialize model (map each cellName to -1, e.g. "1_dwn": -1)
        this.gameModel = new HashMap<>(15 * this.numOfCols, 1);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < this.numOfCols; j++) {
                this.gameModel.put(this.ROW_NAMES.get(i) + "_" + this.COL_NAMES.get(j), -1);
            }
        }

        // Initialize List for available cells
        this.availableCellsNames = new ArrayList<>((this.numOfCols - 2) * 12 + 2);
        this.updateAvailableCellsNames(-1);

        // Initialize List for GridView (add each cellValue to List in order)
        this.listCells = new ArrayList<>(16 * (this.numOfCols + 1));
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < this.numOfCols + 1; j++) {
                if (j == 0) this.listCells.add(i == 0 ? "x" : this.ROW_NAMES.get(i - 1));
                else this.listCells.add(i == 0 ? this.COL_NAMES.get(j - 1) : "");
            }
        }
    }

    public int getNumOfCols(boolean playable) {
        return playable ? numOfCols : numOfCols + 1;
    }

    public List<String> getListCells() {
        return this.listCells;
    }

    public void updateListCells(String cellName, String value) {
        this.listCells.set(this.cellNameToPosition(cellName), value);
    }

    public List<String> getAvailableCellsNames() {
        return this.availableCellsNames;
    }

    public void clearAvailableCellsNames() {
        this.availableCellsNames.clear();

        // So user can make an announcement for An0 column
        if (this.numOfCols == 5) {
            String currentCellName;
            for (int i = 0; i < 15; i++) {
                if (this.ROW_NAMES.get(i).contains("eq")) continue;
                currentCellName = this.ROW_NAMES.get(i) + "_an0";
                if (this.gameModel.get(currentCellName).equals(-1)) this.availableCellsNames.add(currentCellName);
            }
        }
    }

    public void updateAvailableCellsNames(int rollNumber) {
        this.availableCellsNames.clear();

        if (this.announcedCellName != null) {
            this.availableCellsNames.add(announcedCellName);
        } else {
            String currentCellName;
            String cellNameToAdd = null;

            for (int i = 0; i < this.numOfCols; i++) {
                rowIterator: for (int j = 0; j < 15; j++) {
                    if (this.ROW_NAMES.get(j).contains("eq")) continue;
                    currentCellName = this.ROW_NAMES.get(j) + "_" + this.COL_NAMES.get(i);
                    switch (this.COL_NAMES.get(i)) {
                        case "dwn":
                            if (this.gameModel.get(currentCellName).equals(-1)) {
                                this.availableCellsNames.add(currentCellName);
                                break rowIterator;
                            } else break;
                        case "any":
                            if (this.gameModel.get(currentCellName).equals(-1)) this.availableCellsNames.add(currentCellName);
                            break;
                        case "up":
                            if (this.gameModel.get(currentCellName).equals(-1)) cellNameToAdd = currentCellName;
                            if (j == 13 && cellNameToAdd != null) this.availableCellsNames.add(cellNameToAdd);
                            break;
                        case "an1":
                            if (rollNumber != -1 && rollNumber != 1) break rowIterator;
                            if (this.gameModel.get(currentCellName).equals(-1)) this.availableCellsNames.add(currentCellName);
                            break;
                        case "an0":
                            if (rollNumber != -1 && rollNumber != 0) break rowIterator;
                            if (this.gameModel.get(currentCellName).equals(-1)) this.availableCellsNames.add(currentCellName);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public List<String> getColumnsLeft() {
        List<String> columnsLeft = new ArrayList<>(this.numOfCols);
        boolean addColumn;
        String currentCellName;

        for (int i = 0; i < this.numOfCols; i++) {
            addColumn = true;
            rowIterator: for (int j = 0; j < 15; j++) {
                if (this.ROW_NAMES.get(j).contains("eq")) continue;
                currentCellName = this.ROW_NAMES.get(j) + "_" + this.COL_NAMES.get(i);
                if (this.gameModel.get(currentCellName).equals(-1)) {
                    addColumn = false;
                    break rowIterator;
                }
            }
            if (addColumn) columnsLeft.add(this.COL_NAMES.get(i));
        }

        return columnsLeft;
    }

    public boolean isAnnouncementRequired(int rollNumber) {
        if (this.announcedCellName == null) {
            List<String> columnsLeft = this.getColumnsLeft();
            if (((this.onlyAn0AndAn1Left(columnsLeft) || this.onlyAn1Left(columnsLeft)) && rollNumber == 1) ||
                this.onlyAn0Left(columnsLeft) && rollNumber == 0) return true;
        }
        return false;
    }

    public boolean onlyAn0Left(List<String> columnsLeft) {
        if (this.numOfCols == 4) return false;
        return columnsLeft.size() == 1 && columnsLeft.contains("an0");
    }

    public boolean onlyAn1Left(List<String> columnsLeft) {
        return columnsLeft.size() == 1 && columnsLeft.contains("an1");
    }

    public boolean onlyAn0AndAn1Left(List<String> columnsLeft) {
        if (this.numOfCols == 4) return false;
        return columnsLeft.size() == 2 && columnsLeft.contains("an0") && columnsLeft.contains("an1");
    }

    public void checkCompletedSections() {
        this.lastSumCellsNames.clear();
        boolean isCompleted;
        int sum;

        for (int i = 0; i < this.numOfCols; i++) {
            if (this.gameModel.get("eq1_" + this.COL_NAMES.get(i)) == -1) {
                isCompleted = true;
                sum = 0;
                for (String rowName : new String[]{"1", "2", "3", "4", "5", "6"}) {
                    if (this.gameModel.get(rowName + "_" + this.COL_NAMES.get(i)) == -1) {
                        isCompleted = false;
                        break;
                    } else {
                        sum += this.gameModel.get(rowName + "_" + this.COL_NAMES.get(i));
                    }
                }
                if (isCompleted) {
                    if (sum > 59) sum += 30;
                    this.gameModel.put("eq1_" + this.COL_NAMES.get(i), sum);
                    this.lastSumCellsNames.add("eq1_" + this.COL_NAMES.get(i));
                }
            }

            if (this.gameModel.get("eq2_" + this.COL_NAMES.get(i)) == -1) {
                isCompleted = true;
                for (String rowName : new String[]{"1", "max", "min"}) {
                    if (this.gameModel.get(rowName + "_" + this.COL_NAMES.get(i)) == -1) {
                        isCompleted = false;
                        break;
                    }
                }
                if (isCompleted) {
                    sum = this.gameModel.get("max_" + this.COL_NAMES.get(i));
                    sum -= this.gameModel.get("min_" + this.COL_NAMES.get(i));
                    sum *= this.gameModel.get("1_" + this.COL_NAMES.get(i));
                    if (sum < 0) sum = 0;
                    this.gameModel.put("eq2_" + this.COL_NAMES.get(i), sum);
                    this.lastSumCellsNames.add("eq2_" + this.COL_NAMES.get(i));
                }
            }

            if (this.gameModel.get("eq3_" + this.COL_NAMES.get(i)) == -1) {
                isCompleted = true;
                sum = 0;
                for (String rowName : new String[]{"str", "ful", "pok", "ymb"}) {
                    if (this.gameModel.get(rowName + "_" + this.COL_NAMES.get(i)) == -1) {
                        isCompleted = false;
                        break;
                    } else {
                        sum += this.gameModel.get(rowName + "_" + this.COL_NAMES.get(i));
                    }
                }
                if (isCompleted) {
                    this.gameModel.put("eq3_" + this.COL_NAMES.get(i), sum);
                    this.lastSumCellsNames.add("eq3_" + this.COL_NAMES.get(i));
                }
            }
        }
    }

    public boolean isGameFinished() {
        for (int i = 0; i < this.numOfCols; i++) {
            for (String rowName : new String[]{"eq1", "eq2", "eq3"}) {
                if (this.gameModel.get(rowName + "_" + this.COL_NAMES.get(i)) == -1) return false;
            }
        }
        return true;
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

    public String getLastInputCellName() {
        return this.lastInputCellName;
    }

    public void setLastInputCellName(String cellName) {
        this.lastInputCellName = cellName;
    }

    public List<String> getLastSumCellsNames() {
        return this.lastSumCellsNames;
    }

    public String positionToCellName(int position) {
        int nCol = this.getNumOfCols(false);
        String rowName = (position / nCol == 0 ? "x" : this.ROW_NAMES.get(position / nCol - 1));
        String colName = (position % nCol == 0 ? "x":  this.COL_NAMES.get(position % nCol - 1));
        return rowName + "_" + colName;
    }

    public int cellNameToPosition(String cellName) {
        String[] cellNameSplit = cellName.split("_");
        return (this.numOfCols + 1) * (this.ROW_NAMES.indexOf(cellNameSplit[0]) + 1) + (this.COL_NAMES.indexOf(cellNameSplit[1]) + 1);
    }

    public String getCellRowName(String cellName) {
        return cellName.split("_")[0];
    }

    public String getCellColName(String cellName) {
        return cellName.split("_")[1];
    }

    public void setModelValue(String cellName, Integer value) {
        this.gameModel.put(cellName, value);
    }

    public Integer getModelValue(String cellName) {
        return this.gameModel.get(cellName);
    }

    public void setFinalResult(int finalResult) {
        this.finalResult = finalResult;
    }

    public int getFinalResult() {
        return this.finalResult;
    }

    public void calculateFinalResult() {
        int finalResult = 0;

        for (int i = 0; i < this.numOfCols; i++) {
            for (String rowName : new String[]{"eq1", "eq2", "eq3"}) {
                finalResult += this.gameModel.get(rowName + "_" + this.COL_NAMES.get(i));
            }
        }

        this.finalResult = finalResult;
    }

}
