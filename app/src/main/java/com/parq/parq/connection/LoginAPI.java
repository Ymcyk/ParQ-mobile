package com.parq.parq.connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 28.12.16.
 */

public class LoginAPI extends AbstractAPI {

    private String token = "";

    public LoginAPI(Context context, APIResponse apiResponse){
        super(context, apiResponse);
    }

    public void login(final String username, final String password) {
        StringRequest loginRequest = new StringRequest(Request.Method.POST, App.getUrl().getLoginURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            token = jsonResponse.getString("token");
                            Log.i("Token received",token);
                            responseCode = App.HTTP_2xx;
                            apiResponse.responseSuccess(LoginAPI.this);
                        } catch (JSONException e) {
                            Log.d("Login", "JSON parse error");
                            e.printStackTrace();
                            responseCode = App.PARSE_ERROR;
                            apiResponse.responseError(LoginAPI.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse er = error.networkResponse;
                        if(er != null && er.statusCode == 400) {
                            responseCode = App.HTTP_400;
                            Log.i("Login", "Bad login or password");
                        } else if(er != null && er.statusCode == 403) {
                            responseCode = App.HTTP_403;
                            Log.d("Login", "Bad role");
                        } else {
                            error.printStackTrace();
                            responseCode = App.CONNECTION_ERROR;
                            Log.d("Login", "Connection error");
                        }
                        apiResponse.responseError(LoginAPI.this);
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String data = String.format("username=%s&password=%s&role=driver", username, password);
                return data.getBytes();
            }
        };

        Volley.newRequestQueue(context).add(loginRequest);
    }

    public String getToken() {
        return token;
    }
}


