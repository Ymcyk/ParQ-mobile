package com.parq.parq.connection;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;
import com.parq.parq.TicketsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import static com.parq.parq.R.string.parking;

/**
 * Created by piotr on 08.01.17.
 */

public class TicketsAPI {
    private TicketsActivity ticketsActivity;

    public TicketsAPI(TicketsActivity ticketsActivity){
        this.ticketsActivity = ticketsActivity;
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
                            LinkedList<Ticket> ticketsList = new LinkedList<>();
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
                                }

                                JSONObject veh = json.getJSONObject("vehicle");
                                ticket.setName(veh.getString("name"));

                                ticketsList.add(ticket);
                            }

                            Log.i("ticketList", "Size: " + ticketsList.size());
                            ticketsActivity.ticketsRequestSuccess(ticketsList);
                        } catch (JSONException e) {
                            Log.d("ticketList", "JSON parse error");
                            e.printStackTrace();
                            ticketsActivity.connectionError(App.PARSE_ERROR);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                ticketsActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("ticketList", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                ticketsActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("ticketList", "Bad role 403");
                            }
                        }

                        error.printStackTrace();
                        Log.d("ticketList", "Connection error");
                        ticketsActivity.connectionError(App.CONNECTION_ERROR);
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

        Volley.newRequestQueue(ticketsActivity).add(ticketsRequest);
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

                            LinkedList<Parking> parkingsList = new LinkedList<>();
                            Log.i("Parking length", String.valueOf(array.length()));
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                Parking parking = new Parking();

                                parking.setId(json.getInt("id"));
                                parking.setName(json.getString("name"));
                                parking.setDescription(json.getString("description"));

                                parkingsList.add(parking);
                            }

                            Log.i("parkingsList", "Response and parse success");
                            ticketsActivity.parkingsRequestSuccess(parkingsList);
                        } catch (JSONException e) {
                            Log.d("parkingsList", "JSON parse error");
                            e.printStackTrace();
                            ticketsActivity.connectionError(App.PARSE_ERROR);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                ticketsActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("parkingsList", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                ticketsActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("parkingsList", "Bad role 403");
                            }
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        ticketsActivity.connectionError(App.CONNECTION_ERROR);
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

        Volley.newRequestQueue(ticketsActivity).add(parkingsRequest);
    }

    public void requestSchedules(int year, int month, int day, int parkingId) {
        StringRequest schedulesRequest = new StringRequest(
                Request.Method.GET,
                App.getUrl().getSchedulesByDateAndParkingURL(year, month, day, parkingId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(response.isEmpty()) {
                                ticketsActivity.scheduleRequestSuccess(null);
                                return;
                            }

                            JSONObject scheduleJson = new JSONObject(response);

                            String startString = scheduleJson.getString("start");
                            String endString = scheduleJson.getString("end");

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss'Z'", Locale.ENGLISH);

                            Schedule schedule = new Schedule();

                            try {
                                Date startDate = sdf.parse(startString);
                                Date endDate = sdf.parse(endString);

                                schedule.setStart(startDate);
                                schedule.setEnd(endDate);
                            } catch (ParseException e) {
                                Log.d("requestSchedule", "Date parsing failed");
                                ticketsActivity.connectionError(App.PARSE_ERROR);
                                e.printStackTrace();
                            }

                            JSONArray chargesArray = scheduleJson.getJSONArray("charges");

                            LinkedList<Charge> chargesList = new LinkedList<>();

                            for(int i = 0; i < chargesArray.length(); i++) {
                                JSONObject json = chargesArray.getJSONObject(i);
                                Charge charge = new Charge();

                                charge.setCost(json.getDouble("cost"));
                                charge.setMinutes(json.getInt("minutes"));
                                charge.setDuration(json.getInt("duration"));
                                charge.setMinuteBilling(json.getBoolean("minute_billing"));

                                Log.i("New charge", charge.toString());
                                chargesList.add(charge);
                            }

                            schedule.setCharges(chargesList);

                            Log.i("schedule", "Response and parse success");
                            Log.i("New schedule", schedule.toString());
                            ticketsActivity.scheduleRequestSuccess(schedule);
                        } catch (JSONException e) {
                            Log.d("schedule", "JSON parse error");
                            e.printStackTrace();
                            ticketsActivity.connectionError(App.PARSE_ERROR);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                ticketsActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("schedule", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                ticketsActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("schedule", "Bad role 403");
                            }
                        }

                        error.printStackTrace();
                        Log.d("schedule", "Connection error");
                        ticketsActivity.connectionError(App.CONNECTION_ERROR);
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

        Volley.newRequestQueue(ticketsActivity).add(schedulesRequest);
    }
}
