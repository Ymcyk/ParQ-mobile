package com.parq.parq.models;

import java.util.Locale;

/**
 * Created by piotr on 08.01.17.
 */

public class Charge {
    private double cost;
    private int minutes;
    private int duration;
    private boolean minuteBilling;

    public Charge() {}

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isMinuteBilling() {
        return minuteBilling;
    }

    public void setMinuteBilling(boolean minuteBilling) {
        this.minuteBilling = minuteBilling;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%d %f/%d", duration, cost, minutes);
    }
}

