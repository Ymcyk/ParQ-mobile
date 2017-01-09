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
import com.parq.parq.App;
import com.parq.parq.BuyTicketActivity;
import com.parq.parq.VehicleListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Created by piotr on 09.01.17.
 */

public class BuyTicketsAPI {
    private BuyTicketActivity buyTicketActivity;

    public BuyTicketsAPI(BuyTicketActivity buyTicketActivity) {
        this.buyTicketActivity = buyTicketActivity;
    }

    public void postTicket(Ticket ticket) {
        Calendar startCal = ticket.getStart();
        String start = String.format(Locale.ENGLISH,
                "%d-%02d-%02dT%02d:%02d:00Z",
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH)+1,
                startCal.get(Calendar.DAY_OF_MONTH),
                startCal.get(Calendar.HOUR_OF_DAY),
                startCal.get(Calendar.MINUTE));

        Calendar endCal = ticket.getEnd();
        String end = String.format(Locale.ENGLISH,
                "%d-%02d-%02dT%02d:%02d:00Z",
                endCal.get(Calendar.YEAR),
                endCal.get(Calendar.MONTH)+1,
                endCal.get(Calendar.DAY_OF_MONTH),
                endCal.get(Calendar.HOUR_OF_DAY),
                endCal.get(Calendar.MINUTE));
/*
        Map<String, String> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("vehicle", String.valueOf(ticket.getVehicleId()));
        params.put("parking", String.valueOf(ticket.getParkingId()));
*/
        JSONObject postJson = new JSONObject();
        try {
            postJson.put("start", start);
            postJson.put("end", end);
            postJson.put("vehicle", String.valueOf(ticket.getVehicleId()));
            postJson.put("parking", String.valueOf(ticket.getParkingId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest addTicketPost = new JsonObjectRequest(Request.Method.POST, App.getUrl().getTicketListURL(),
                postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("postTicket", "Response success");
                        buyTicketActivity.ticketPostSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                buyTicketActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("postTicket", "Bad token 401");
                                return;
                            } else if(error.networkResponse.statusCode == 403) {
                                buyTicketActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("postTicket", "Bad role 403");
                                return;
                            } else if(error.networkResponse.statusCode == 406){
                                buyTicketActivity.connectionError(App.NOT_ACCEPTABLE);
                                Log.d("postTicket", "Not enough money 406");
                                return;
                            }
                        }

                        error.printStackTrace();
                        Log.d("postTicket", "Connection error");
                        buyTicketActivity.connectionError(App.CONNECTION_ERROR);
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
        addTicketPost.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(buyTicketActivity).add(addTicketPost);
    }

    public void requestVehiclesList() {
        StringRequest vehiclesRequest = new StringRequest(Request.Method.GET, App.getUrl().getVehiclesURL(),
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
                            buyTicketActivity.vehicleListRequestSuccess(vehicleList);
                        } catch (JSONException e) {
                            Log.d("VehicleList", "JSON parse error");
                            e.printStackTrace();
                            buyTicketActivity.connectionError(App.PARSE_ERROR);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                buyTicketActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("VehicleList", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                buyTicketActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("VehicleList", "Bad role 403");
                            }
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        buyTicketActivity.connectionError(App.CONNECTION_ERROR);
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

        Volley.newRequestQueue(buyTicketActivity).add(vehiclesRequest);
    }
}
