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

    Dice(int quantity) {
        this.quantity = quantity;
        this.rollNumber = 0;
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

    public List<Integer> getRandoms() {
        Random rand = new Random();
        List<Integer> randoms = new ArrayList<>(this.quantity);

        for (int i = 0; i < this.quantity; i++) {
            randoms.add(i, rand.nextInt(6) + 1);
        }

        return randoms;
    }
}

