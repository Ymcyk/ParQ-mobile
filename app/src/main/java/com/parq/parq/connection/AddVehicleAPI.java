package com.parq.parq.connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.models.Vehicle;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 30.12.16.
 */

public class AddVehicleAPI extends AbstractAPI {

    public AddVehicleAPI(Context context, APIResponse apiResponse) {
        super(context, apiResponse);
    }

    public void postVehicle(Vehicle vehicle) {

        Map<String, String> params = new HashMap<>();
        params.put("name", vehicle.getName());
        params.put("plate_country", vehicle.getPlateCountry());
        params.put("plate_number", vehicle.getPlateNumber());

        JsonObjectRequest postVehicleRequest = new JsonObjectRequest(Request.Method.POST, App.getUrl().getVehiclesURL(),
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("AddVehicle", "Response success");
                        responseCode = App.HTTP_2xx;
                        apiResponse.responseSuccess(AddVehicleAPI.this);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                responseCode = App.HTTP_401;
                                Log.d("addVehicle", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                responseCode = App.HTTP_403;
                                Log.d("addVehicle", "Bad role 403");
                            }
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        apiResponse.responseError(AddVehicleAPI.this);
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
        postVehicleRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(context).add(postVehicleRequest);
    }
}
