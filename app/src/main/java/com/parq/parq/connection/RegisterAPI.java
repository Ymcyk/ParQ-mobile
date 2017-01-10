package com.parq.parq.connection;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.RegisterActivity;
import com.parq.parq.models.Profile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 10.01.17.
 */

public class RegisterAPI {
    private RegisterActivity registerActivity;

    public RegisterAPI(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
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

        JsonObjectRequest addDriverPost = new JsonObjectRequest(Request.Method.POST, App.getUrl().getRegisterURL(),
                driverJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("postDriver", "Response success");
                        registerActivity.registerPostSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                registerActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("postDriver", "Bad token 401");
                                return;
                            } else if(error.networkResponse.statusCode == 403) {
                                registerActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("postDriver", "Bad role 403");
                                return;
                            } else if(error.networkResponse.statusCode == 400) {
                                registerActivity.connectionError(App.USER_EXIST);
                                Log.d("postDriver", "Username exist 400");
                                return;
                            }
                        }

                        error.printStackTrace();
                        Log.d("postDriver", "Connection error");
                        registerActivity.connectionError(App.CONNECTION_ERROR);
                    }
                }
        ) {

        };
        addDriverPost.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(registerActivity).add(addDriverPost);
    }
}
