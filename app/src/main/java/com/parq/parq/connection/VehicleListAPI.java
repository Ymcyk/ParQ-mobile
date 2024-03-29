package com.parq.parq.connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.models.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by piotr on 30.12.16.
 */

public class VehicleListAPI extends AbstractAPI {

    private List<Vehicle> vehicleList;

    public VehicleListAPI(Context context, APIResponse apiResponse) {
        super(context, apiResponse);
    }

    public void requestVehicleList() {
        StringRequest vehicleListRequest = new StringRequest(Request.Method.GET, App.getUrl().getVehiclesURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            vehicleList = new LinkedList<>();

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                Vehicle vehicle = new Vehicle();

                                vehicle.setId(json.getInt("id"));
                                vehicle.setBadge(json.getString("badge"));
                                vehicle.setName(json.getString("name"));
                                vehicle.setPlateCountry(json.getString("plate_country"));
                                vehicle.setPlateNumber(json.getString("plate_number"));

                                getVehicleList().add(vehicle);
                            }

                            Log.i("VehicleList", "Response and parse success");
                            responseCode = App.HTTP_2xx;
                            apiResponse.responseSuccess(VehicleListAPI.this);
                        } catch (JSONException e) {
                            Log.d("VehicleList", "JSON parse error");
                            e.printStackTrace();
                            responseCode = App.PARSE_ERROR;
                            apiResponse.responseError(VehicleListAPI.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                responseCode = App.HTTP_401;
                                Log.d("VehicleList", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                responseCode = App.HTTP_403;
                                Log.d("VehicleList", "Bad role 403");
                            }
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        apiResponse.responseError(VehicleListAPI.this);
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

        Volley.newRequestQueue(context).add(vehicleListRequest);
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }
}
