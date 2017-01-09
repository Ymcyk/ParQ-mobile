package com.parq.parq;

import android.content.SharedPreferences;

import com.parq.parq.connection.ParQURLConstructor;

/**
 * Created by piotr on 29.12.16.
 */

public class App {
    private static SharedPreferences sharedPref;
    private static ParQURLConstructor url;

    public final static int PARSE_ERROR = 0;
    public final static int CONNECTION_ERROR = 1;
    public final static int UNAUTHENTICATED = 2;
    public final static int NOT_ACCEPTABLE = 3;

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
}
