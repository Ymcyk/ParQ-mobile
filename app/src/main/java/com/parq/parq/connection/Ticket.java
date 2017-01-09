package com.parq.parq.connection;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by piotr on 09.01.17.
 */

public class Ticket {
    private Calendar start;
    private Calendar end;
    private int parkingId;
    private int vehicleId;

    public Ticket(int year, int month, int day) {
        start = Calendar.getInstance();
        end = Calendar.getInstance();

        start.set(year, month, day);
        end.set(year, month, day);
    }


    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public int getParkingId() {
        return parkingId;
    }

    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "start: %d-%02d-%02d %02d:%02d end: %d-%02d-%02d %02d:%02d",
                start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH),
                start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE),
                end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH),
                end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE));
    }
}
