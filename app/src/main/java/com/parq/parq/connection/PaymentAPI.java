package com.parq.parq.connection;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.AddVehicleActivity;
import com.parq.parq.App;
import com.parq.parq.ProfileActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 09.01.17.
 */

public class PaymentAPI {
    private ProfileActivity profileActivity;

    public PaymentAPI(ProfileActivity profileActivity) {
        this.profileActivity = profileActivity;
    }

    public void postPayment(String paymentId, double money) {

        Map<String, String> params = new HashMap<>();
        params.put("transaction_id", paymentId);
        params.put("money", String.valueOf(money));

        JsonObjectRequest addPaymentPost = new JsonObjectRequest(Request.Method.POST, App.getUrl().getPaymentsURL(),
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("postPayment", "Response success");
                        profileActivity.paymentSendSuccess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null){
                            if(error.networkResponse.statusCode == 401) {
                                profileActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("postPayment", "Bad token 401");
                            } else if(error.networkResponse.statusCode == 403) {
                                profileActivity.connectionError(App.UNAUTHENTICATED);
                                Log.d("postPayment", "Bad role 403");
                            }
                        }

                        error.printStackTrace();
                        Log.d("VehicleList", "Connection error");
                        profileActivity.connectionError(App.CONNECTION_ERROR);
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
        addPaymentPost.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(profileActivity).add(addPaymentPost);
    }
}
