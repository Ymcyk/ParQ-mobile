package com.parq.parq.connection;

import android.content.Context;

/**
 * Created by piotr on 14.01.17.
 */

public abstract class AbstractAPI {
    protected int responseCode;
    protected Context context;
    protected APIResponse apiResponse;

    public AbstractAPI(Context context, APIResponse apiResponse){
        this.context = context;
        this.apiResponse = apiResponse;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
