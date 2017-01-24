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
import com.parq.parq.models.Parking;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by piotr on 14.01.17.
 */

public class ParkingListAPI extends AbstractAPI {

    private List<Parking> parkingList;

    public ParkingListAPI(Context context, APIResponse apiResponse){
        super(context, apiResponse);
    }

    public void requestParkings() {
        StringRequest parkingsRequest = new StringRequest(
                Request.Method.GET,
                App.getUrl().getParkingsURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            parkingList = new LinkedList<>();
                            Log.i("Parking length", String.valueOf(array.length()));
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                Parking parking = new Parking();

                                parking.setId(json.getInt("id"));
                                parking.setName(json.getString("name"));
                                parking.setDescription(json.getString("description"));

                                JSONObject open = json.getJSONObject("open");

                                if(open.has("start") && open.has("end")){
                                    DateTime start = DateTime.parse(open.getString("start"));
                                    start = start.withZone(DateTimeZone.getDefault());
                                    parking.setStart(start);
                                    parking.setEnd(DateTime.parse(open.getString("end")));
                                }

                                getParkingList().add(parking);
                            }

                            Log.i("parkingsList", "Response and parse success");
                            responseCode = App.HTTP_2xx;
                            apiResponse.responseSuccess(ParkingListAPI.this);
                        } catch (JSONException e) {
                            Log.d("parkingsList", "JSON parse error");
                            e.printStackTrace();
                            responseCode = App.PARSE_ERROR;
                            apiResponse.responseError(ParkingListAPI.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                responseCode = App.HTTP_401;
                                Log.d("parkingsList", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                responseCode = App.HTTP_403;
                                Log.d("parkingsList", "Bad role 403");
                            }
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        apiResponse.responseError(ParkingListAPI.this);
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

        Volley.newRequestQueue(context).add(parkingsRequest);
    }

    public List<Parking> getParkingList() {
        return parkingList;
    }
}
