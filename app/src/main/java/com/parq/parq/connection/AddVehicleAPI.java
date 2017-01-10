package com.parq.parq.connection;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.AddVehicleActivity;
import com.parq.parq.App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by piotr on 30.12.16.
 */

public class AddVehicleAPI {
    private AddVehicleActivity addVehicleActivity;

    public AddVehicleAPI(AddVehicleActivity addVehicleActivity) {
        this.addVehicleActivity = addVehicleActivity;
    }

    public void postVehicle(Vehicle vehicle) {

        Map<String, String> params = new HashMap<>();
        params.put("name", vehicle.getName());
        params.put("plate_country", vehicle.getPlateCountry());
        params.put("plate_number", vehicle.getPlateNumber());

        JsonObjectRequest addVehiclePost = new JsonObjectRequest(Request.Method.POST, App.getUrl().getVehiclesURL(),
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("AddVehicle", "Response success");
                        addVehicleActivity.addVehiclePostSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                addVehicleActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("addVehicle", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                addVehicleActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("addVehicle", "Bad role 403");
                            }
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        addVehicleActivity.connectionError(App.CONNECTION_ERROR);
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
        addVehiclePost.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(addVehicleActivity).add(addVehiclePost);
    }
}
