package com.parq.parq.connection;

import android.content.Context;
import android.net.Uri.Builder;

import com.parq.parq.R;

import java.util.Locale;

/**
 * Created by piotr on 27.12.16.
 */

public class ParQURLConstructor {
    private String authority;
    private Context context;

    public ParQURLConstructor(String authority, Context context) {
        this.authority = authority;
        this.context = context;
    }

    private Builder getBase() {
        return new Builder()
                .scheme("http")
                .encodedAuthority(authority)
                .appendEncodedPath("api/");
    }

    public String getLoginURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_login));
        return builder.build().toString();
    }

    public String getTicketListURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_tickets));
        return builder.build().toString();
    }

    public String getTicketByParkingURL(String parking) {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_tickets))
                .appendQueryParameter("parking", parking);
        return builder.build().toString();
    }

    public String getTicketByBadgeURL(String badge) {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_tickets))
                .appendQueryParameter("badge", badge);
        return builder.build().toString();
    }

    public String getSchedulesByDateAndParkingURL(int year, int month, int day, int parkingId) {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_schedules))
                .appendQueryParameter("date", String.format(Locale.ENGLISH, "%d-%d-%d", year, month, day))
                .appendQueryParameter("parking", String.valueOf(parkingId));
        return builder.build().toString();
    }

    public String getParkingsURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_parkings));
        return builder.build().toString();
    }

    public String getCurrentURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_current));
        return builder.build().toString();
    }

    public String getVehiclesURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_vehicles));
        return builder.build().toString();
    }

    public String getPaymentsURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_payments));
        return builder.build().toString();
    }

    public String getRegisterURL() {
        Builder builder = getBase()
                .appendEncodedPath(context.getString(R.string.url_register));
        return builder.build().toString();
    }
}
