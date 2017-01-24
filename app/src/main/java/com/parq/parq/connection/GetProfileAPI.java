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
import com.parq.parq.models.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 29.12.16.
 */

public class GetProfileAPI extends AbstractAPI {

    private Profile profile = new Profile();

    public GetProfileAPI(Context context, APIResponse apiResponse){
        super(context, apiResponse);
    }

    public void requestProfile() {
        StringRequest profileRequest = new StringRequest(Request.Method.GET, App.getUrl().getCurrentURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject userJSON = jsonResponse.getJSONObject("user");

                            profile.setUsername(userJSON.getString("username"));
                            profile.setEmail(userJSON.getString("email"));
                            profile.setWallet(jsonResponse.getString("wallet"));

                            Log.i("Profile", "Response and parse success");
                            responseCode = App.HTTP_2xx;
                            apiResponse.responseSuccess(GetProfileAPI.this);
                        } catch (JSONException e) {
                            Log.d("Profile", "JSON parse error");
                            e.printStackTrace();
                            responseCode = App.PARSE_ERROR;
                            apiResponse.responseError(GetProfileAPI.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse nr = error.networkResponse;
                        if(nr != null && error.networkResponse.statusCode == 401){
                            responseCode = App.HTTP_401;
                            apiResponse.responseError(GetProfileAPI.this);
                            Log.i("Profile", "Bad token 401");
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("Login", "Connection error");
                        apiResponse.responseError(GetProfileAPI.this);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", String.format("Token %s", App.getToken()));
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(profileRequest);
    }

    public Profile getProfile() {
        return profile;
    }
}
