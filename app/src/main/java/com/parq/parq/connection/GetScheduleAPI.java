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
import com.parq.parq.models.Charge;
import com.parq.parq.models.Schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Created by piotr on 14.01.17.
 */

public class GetScheduleAPI extends AbstractAPI {

    private Schedule schedule = null;

    public GetScheduleAPI(Context context, APIResponse apiResponse) {
        super(context, apiResponse);
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
                                apiResponse.responseSuccess(GetScheduleAPI.this);
                                return;
                            }

                            JSONObject scheduleJson = new JSONObject(response);

                            String startString = scheduleJson.getString("start");
                            String endString = scheduleJson.getString("end");

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd'T'hh:mm:ss'Z'", Locale.ENGLISH);
/*
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                            TimeZone tz = TimeZone.getTimeZone("Europe/Warsaw");
                            df.setTimeZone(tz);
                            String asEW = df.format(new Date());
                            System.out.println(asEW);
                            TimeZone utc = TimeZone.getTimeZone("UTC");
                            df.setTimeZone(utc);
                            String nowAsISO = df.format(new Date());
                            System.out.println(nowAsISO);
*/
                            schedule = new Schedule();

                            try {
                                Date startDate = sdf.parse(startString);
                                Date endDate = sdf.parse(endString);

                                schedule.setStart(startDate);
                                schedule.setEnd(endDate);
                            } catch (ParseException e) {
                                Log.d("requestSchedule", "Date parsing failed");
                                responseCode = App.PARSE_ERROR;
                                apiResponse.responseError(GetScheduleAPI.this);
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
                            responseCode = App.HTTP_2xx;
                            apiResponse.responseSuccess(GetScheduleAPI.this);
                        } catch (JSONException e) {
                            Log.d("schedule", "JSON parse error");
                            e.printStackTrace();
                            responseCode = App.PARSE_ERROR;
                            apiResponse.responseError(GetScheduleAPI.this);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                responseCode = App.HTTP_401;
                                Log.d("schedule", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                responseCode = App.HTTP_403;
                                Log.d("schedule", "Bad role 403");
                            }
                        } else {
                            responseCode = App.CONNECTION_ERROR;
                        }

                        error.printStackTrace();
                        Log.d("schedule", "Connection error");
                        apiResponse.responseError(GetScheduleAPI.this);
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

        Volley.newRequestQueue(context).add(schedulesRequest);
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
