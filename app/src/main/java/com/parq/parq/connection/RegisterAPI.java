package com.parq.parq.connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.models.Profile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 10.01.17.
 */

public class RegisterAPI extends AbstractAPI {

    public RegisterAPI(Context context, APIResponse apiResponse) {
        super(context, apiResponse);
    }

    public void postDriver(Profile profile) {

        JSONObject userJson = new JSONObject();
        JSONObject driverJson = new JSONObject();
        try {
            userJson.put("username", profile.getUsername());
            userJson.put("email", profile.getEmail());
            userJson.put("password", profile.getPassword());

            driverJson.put("user", userJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest driverPost = new JsonObjectRequest(Request.Method.POST, App.getUrl().getRegisterURL(),
                driverJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("postDriver", "Response success");
                        responseCode = App.HTTP_2xx;
                        apiResponse.responseSuccess(RegisterAPI.this);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                responseCode = App.HTTP_401;
                                Log.d("postDriver", "Bad token 401");
                                return;
                            } else if(error.networkResponse.statusCode == 403) {
                                responseCode = App.HTTP_403;
                                Log.d("postDriver", "Bad role 403");
                                return;
                            } else if(error.networkResponse.statusCode == 400) {
                                responseCode = App.HTTP_400;
                                Log.d("postDriver", "Username exist 400");
                                return;
                            }
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("postDriver", "Connection error");
                        apiResponse.responseError(RegisterAPI.this);
                    }
                }
        ) {

        };
        driverPost.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(context).add(driverPost);
    }
}
