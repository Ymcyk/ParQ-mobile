package com.parq.parq.connection;

/**
 * Created by piotr on 30.12.16.
 */

public class Vehicle {
    private int id;
    private String name;
    private String badge;
    private String plateCountry;
    private String plateNumber;

    public Vehicle() {}

    public Vehicle(int id, String badge, String name, String plateCountry, String plateNumber) {
        this.setPlateNumber(plateNumber);
        this.setId(id);
        this.setBadge(badge);
        this.setName(name);
        this.setPlateCountry(plateCountry);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlateCountry() {
        return plateCountry;
    }

    public void setPlateCountry(String plateCountry) {
        this.plateCountry = plateCountry;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, plateNumber);
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}
