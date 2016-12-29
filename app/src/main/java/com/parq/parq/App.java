package com.parq.parq;

import android.content.SharedPreferences;

import com.parq.parq.connection.ParQURLConstructor;

/**
 * Created by piotr on 29.12.16.
 */

public class App {
    private static SharedPreferences sharedPref;
    private static ParQURLConstructor url;


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
