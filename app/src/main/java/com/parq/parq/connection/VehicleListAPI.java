package com.parq.parq.connection;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.VehicleListActivity;
import com.parq.parq.models.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by piotr on 30.12.16.
 */

public class VehicleListAPI {
    private VehicleListActivity vehicleListActivity;

    public VehicleListAPI(VehicleListActivity vehicleListActivity) {
        this.vehicleListActivity = vehicleListActivity;
    }

    public void requestVehicleList() {
        StringRequest loginRequest = new StringRequest(Request.Method.GET, App.getUrl().getVehiclesURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            LinkedList<Vehicle> vehicleList = new LinkedList<>();

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                Vehicle vehicle = new Vehicle();

                                vehicle.setId(json.getInt("id"));
                                vehicle.setBadge(json.getString("badge"));
                                vehicle.setName(json.getString("name"));
                                vehicle.setPlateCountry(json.getString("plate_country"));
                                vehicle.setPlateNumber(json.getString("plate_number"));

                                vehicleList.add(vehicle);
                            }

                            Log.i("VehicleList", "Response and parse success");
                            vehicleListActivity.vehicleListRequestSuccess(vehicleList);
                        } catch (JSONException e) {
                            Log.d("VehicleList", "JSON parse error");
                            e.printStackTrace();
                            vehicleListActivity.connectionError(App.PARSE_ERROR);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                vehicleListActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("VehicleList", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                vehicleListActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("VehicleList", "Bad role 403");
                            }
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        vehicleListActivity.connectionError(App.CONNECTION_ERROR);
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

        Volley.newRequestQueue(vehicleListActivity).add(loginRequest);
    }

}
