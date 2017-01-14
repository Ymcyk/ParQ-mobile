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
import com.parq.parq.models.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by piotr on 09.01.17.
 */

public class PostTicketAPI extends AbstractAPI {

    public PostTicketAPI(Context context, APIResponse apiResponse) {
        super(context, apiResponse);
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

        JSONObject postJson = new JSONObject();
        try {
            postJson.put("start", start);
            postJson.put("end", end);
            postJson.put("vehicle", String.valueOf(ticket.getVehicleId()));
            postJson.put("parking", String.valueOf(ticket.getParkingId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postTicket = new JsonObjectRequest(Request.Method.POST, App.getUrl().getTicketListURL(),
                postJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("postTicket", "Response success");
                        responseCode = App.HTTP_2xx;
                        apiResponse.responseSuccess(PostTicketAPI.this);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                responseCode = App.HTTP_401;
                                Log.d("postTicket", "Bad token 401");
                                return;
                            } else if(error.networkResponse.statusCode == 403) {
                                responseCode = App.HTTP_403;
                                Log.d("postTicket", "Bad role 403");
                                return;
                            } else if(error.networkResponse.statusCode == 406){
                                responseCode = App.HTTP_406;
                                Log.d("postTicket", "Not enough money 406");
                                return;
                            }
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("postTicket", "Connection error");
                        apiResponse.responseError(PostTicketAPI.this);
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

        postTicket.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(context).add(postTicket);
    }
}
