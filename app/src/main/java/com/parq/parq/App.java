package com.parq.parq;

import android.content.SharedPreferences;

import com.parq.parq.connection.ParQURLConstructor;
import com.parq.parq.models.Parking;
import com.parq.parq.models.Profile;

import java.util.List;

/**
 * Created by piotr on 29.12.16.
 */

public class App {
    private static SharedPreferences sharedPref;
    private static ParQURLConstructor url;
    private static String token;
    private static Profile profile;
    private static List<Parking> parkingList;

    public static List<Parking> getParkingList() {
        return parkingList;
    }

    public static void setParkingList(List<Parking> parkingList) {
        App.parkingList = parkingList;
    }

    public final static int HTTP_400 = 0;
    public final static int HTTP_401 = 1;
    public final static int HTTP_403 = 2;
    public final static int HTTP_406 = 3;
    public final static int HTTP_2xx = 4;

    public final static int PARSE_ERROR = 5;
    public final static int CONNECTION_ERROR = 6;

    public static SharedPreferences getSharedPref() {
        return sharedPref;
    }

    public static void setSharedPref(SharedPreferences sharedPref) {
        App.sharedPref = sharedPref;
    }

    public static ParQURLConstructor getUrl() {
        return url;
    }

    public static void setUrl(ParQURLConstructor url) {
        App.url = url;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        App.token = token;
    }

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile profile) {
        App.profile = profile;
    }
}
