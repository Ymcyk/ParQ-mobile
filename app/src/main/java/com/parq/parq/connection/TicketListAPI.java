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
import com.parq.parq.models.Ticket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by piotr on 08.01.17.
 */

public class TicketListAPI extends AbstractAPI {

    private List<Ticket> ticketList;

    public TicketListAPI(Context context, APIResponse apiResponse){
        super(context, apiResponse);
    }

    public void requestTickets(int parkingId) {
        StringRequest ticketsRequest = new StringRequest(
                Request.Method.GET,
                App.getUrl().getTicketByParkingURL(String.valueOf(parkingId)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //2017-01-10T12:30:00Z
                            JSONArray array = new JSONArray(response);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd'T'H:mm:ss'Z'", Locale.GERMAN);
                            ticketList = new LinkedList<>();
                            Log.i("Ticket length", String.valueOf(array.length()));

                            for(int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                Ticket ticket = new Ticket();

                                try {
                                    Date start = sdf.parse(json.getString("start"));
                                    Date end = sdf.parse(json.getString("end"));

                                    Calendar startCal = Calendar.getInstance();
                                    startCal.setTime(start);
                                    ticket.setStart(startCal);

                                    Calendar endCal = Calendar.getInstance();
                                    endCal.setTime(end);
                                    ticket.setEnd(endCal);

                                    //Log.i("start hour", String.valueOf(startCal.get(Calendar.HOUR_OF_DAY)));
                                    //Log.i("end hour", String.valueOf(endCal.get(Calendar.HOUR_OF_DAY)));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    responseCode = App.PARSE_ERROR;
                                    apiResponse.responseError(TicketListAPI.this);
                                }

                                JSONObject veh = json.getJSONObject("vehicle");
                                ticket.setName(veh.getString("name"));

                                getTicketList().add(ticket);
                            }

                            Log.i("ticketList", "Size: " + ticketList.size());
                            responseCode = App.HTTP_2xx;
                            apiResponse.responseSuccess(TicketListAPI.this);
                        } catch (JSONException e) {
                            Log.d("ticketList", "JSON parse error");
                            e.printStackTrace();
                            responseCode = App.PARSE_ERROR;
                            apiResponse.responseError(TicketListAPI.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                responseCode = App.HTTP_401;
                                Log.d("ticketList", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                responseCode = App.HTTP_403;
                                Log.d("ticketList", "Bad role 403");
                            }
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("ticketList", "Connection error");
                        apiResponse.responseError(TicketListAPI.this);
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

        Volley.newRequestQueue(context).add(ticketsRequest);
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }
}
