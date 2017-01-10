package com.parq.parq.models;

import com.parq.parq.models.Charge;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by piotr on 08.01.17.
 */

public class Schedule {
    private Date start;
    private Date end;
    private List<Charge> charges;

    public Schedule() {}

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public List<Charge> getCharges() {
        return charges;
    }

    public void setCharges(List<Charge> charges) {
        this.charges = charges;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "start: %s end: %s", start.toString(), end.toString());
    }
}
