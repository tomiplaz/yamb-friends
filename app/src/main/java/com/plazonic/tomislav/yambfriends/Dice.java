package com.plazonic.tomislav.yambfriends;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tomo on 11/30/2015.
 */
public class Dice {
    private int quantity;
    private int rollNumber;
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

    public void incrementRollNumber() {
        this.rollNumber++;
    }

    public int getRandom() {
        return new Random().nextInt(6) + 1;
    }

    public void setDice(int index, int number) {
        this.dice.set(index, number);
    }
}

