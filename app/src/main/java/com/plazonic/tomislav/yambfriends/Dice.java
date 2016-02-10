package com.plazonic.tomislav.yambfriends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dice {

    private int quantity;
    private int rollNumber;
    private int lastRollNumber;
    private List<Integer> dice;

    Dice(int quantity) {
        this.quantity = quantity;
        this.rollNumber = 0;
        this.dice = new ArrayList<>(quantity);
        for (int i = 0; i < quantity; i++) {
            this.dice.add(0);
        }
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getRollNumber() {
        return this.rollNumber;
    }

    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }

    public int getLastRollNumber() {
        return this.lastRollNumber;
    }

    public void setLastRollNumber(int rollNumber) {
        this.lastRollNumber = rollNumber;
    }

    public void incrementRollNumber() {
        this.rollNumber++;
    }

    public int getRandom() {
        return new Random().nextInt(6) + 1;
    }

    public void setDice(int index, int number) {
        this.dice.set(index, number);
    }

    public int calculateInput(String rowName) {
        int result = 0;
        List<Integer> auxDice = new ArrayList<>(this.dice);
        int[] values = {1, 2, 3, 4, 5, 6};

        switch (rowName) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
                for (int i = 0; i < this.quantity; i++) {
                    if (this.dice.get(i) == Integer.parseInt(rowName) && result < 5 * Integer.parseInt(rowName)) result += this.dice.get(i);
                }
                break;
            case "max":
            case "min":
                Collections.sort(auxDice);
                if (rowName.equals("max")) Collections.reverse(auxDice);
                for (int i = 0; i < 5; i++) {
                    result += auxDice.get(i);
                }
                break;
            case "str":
                if (this.dice.containsAll(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5))) || this.dice.containsAll(new ArrayList<>(Arrays.asList(2, 3, 4, 5, 6)))) {
                    switch (this.rollNumber) {
                        case 1:
                            result = 66;
                            break;
                        case 2:
                            result = 56;
                            break;
                        case 3:
                            result = 46;
                            break;
                        default:
                            break;
                    }
                }
                break;
            case "ful":
                for (int x : values) {
                    if (Collections.frequency(this.dice, x) >= 3) {
                        for (int y : values) {
                            if (y == x) continue;
                            if (Collections.frequency(this.dice, y) >= 2) {
                                result = 3 * x + 2 * y + 30;
                            }
                        }
                    }
                }
                break;
            case "pok":
                for (int x : values) {
                    if (Collections.frequency(this.dice, x) >= 4) {
                        result = 4 * x + 40;
                    }
                }
                break;
            case "ymb":
                for (int x : values) {
                    if (Collections.frequency(this.dice, x) >= 5) {
                        result = 5 * x + 50;
                    }
                }
                break;
            default:
                break;
        }

        return result;
    }

}

