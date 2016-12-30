package com.parq.parq.connection;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.ProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 29.12.16.
 */

public class ProfileAPI {
        private ProfileActivity profileActivity;

        public ProfileAPI(ProfileActivity profileActivity){
            this.profileActivity = profileActivity;
        }

        public void requestProfile() {
            StringRequest loginRequest = new StringRequest(Request.Method.GET, App.getUrl().getCurrentURL(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONObject userJSON = jsonResponse.getJSONObject("user");

                                Profile profile = new Profile();
                                profile.setUsername(userJSON.getString("username"));
                                profile.setEmail(userJSON.getString("email"));
                                profile.setWallet(jsonResponse.getString("wallet"));

                                Log.i("Profile", "Response and parse success");
                                profileActivity.profileRequestSuccess(profile);
                            } catch (JSONException e) {
                                Log.d("Profile", "JSON parse error");
                                e.printStackTrace();
                                profileActivity.connectionError(App.PARSE_ERROR);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if(error.networkResponse != null){
                                if(error.networkResponse.statusCode == 401) {
                                    profileActivity.connectionError(App.UNAUTHENTICATED);
                                    Log.i("Profile", "Bad token 401");
                                }
                            }

                            error.printStackTrace();
                            Log.d("Login", "Connection error");
                            profileActivity.connectionError(App.CONNECTION_ERROR);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", String.format("Token %s", LoginAPI.getToken()));
                    return headers;
                }
            };

            Volley.newRequestQueue(profileActivity).add(loginRequest);
        }
}
