package com.parq.parq;

import android.content.SharedPreferences;

import com.parq.parq.connection.ParQURLConstructor;

/**
 * Created by piotr on 29.12.16.
 */

public class App {
    private static SharedPreferences sharedPref;
    private static ParQURLConstructor url;
    private static String token;

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
}
