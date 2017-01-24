package com.parq.parq.models;

import org.joda.time.DateTime;

import java.util.Locale;

/**
 * Created by piotr on 08.01.17.
 */

public class Parking {
    private int id;
    private String name;
    private String description;
    private DateTime start = null;
    private DateTime end = null;

    public Parking() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public boolean isDatesSet() {
        return start != null && end != null;
    }
}
