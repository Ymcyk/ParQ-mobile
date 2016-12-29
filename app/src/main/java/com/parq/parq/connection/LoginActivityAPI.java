package com.parq.parq.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.LoginActivity;
import com.parq.parq.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 28.12.16.
 */

public class LoginActivityAPI {
    private LoginActivity loginActivity;
    private static String token;

    public final static int PARSE_ERROR = 0;
    public final static int CONNECTION_ERROR = 1;
    public final static int BAD_ROLE = 2;

    public LoginActivityAPI(LoginActivity loginActivity){
        this.loginActivity = loginActivity;
    }

    static String getToken() {
        return LoginActivityAPI.token;
    }

    public boolean tryLoginWithToken() {
        return false;
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
                            loginActivity.loginSuccess();
                        } catch (JSONException e) {
                            Log.d("Login", "JSON parse error");
                            e.printStackTrace();
                            loginActivity.connectionError(PARSE_ERROR);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse er = error.networkResponse;
                        if(er != null && er.statusCode == 400) {
                            loginActivity.loginFailure();
                            Log.i("Login", "Bad login or password");
                        } else if(er != null && er.statusCode == 403) {
                            loginActivity.connectionError(BAD_ROLE);
                            Log.d("Login", "Bad role");
                        } else {
                            error.printStackTrace();
                            Log.d("Login", "Connection error");
                            loginActivity.connectionError(CONNECTION_ERROR);
                        }
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String data = String.format("username=%s&password=%s&role=driver", username, password);
                return data.getBytes();
            }
        };

        Volley.newRequestQueue(loginActivity).add(loginRequest);
    }
}


