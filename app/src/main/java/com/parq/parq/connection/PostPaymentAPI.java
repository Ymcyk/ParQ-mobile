package com.parq.parq.connection;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.parq.parq.App;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 09.01.17.
 */

public class PostPaymentAPI extends AbstractAPI {

    public PostPaymentAPI(Context context, APIResponse apiResponse) {
        super(context, apiResponse);
    }

    public void postPayment(String paymentId, double money) {

        Map<String, String> params = new HashMap<>();
        params.put("transaction_id", paymentId);
        params.put("money", String.valueOf(money));

        JsonObjectRequest paymentPost = new JsonObjectRequest(Request.Method.POST, App.getUrl().getPaymentsURL(),
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("postPayment", "Response success");
                        responseCode = App.HTTP_2xx;
                        apiResponse.responseSuccess(PostPaymentAPI.this);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse er = error.networkResponse;
                        if(er != null && er.statusCode == 401) {
                            responseCode = App.HTTP_401;
                            Log.i("Login", "Bad login or password");
                        } else if(er != null && er.statusCode == 403) {
                            responseCode = App.HTTP_403;
                            Log.d("Login", "Bad role");
                        } else {
                            error.printStackTrace();
                            Log.d("Login", "Connection error");
                            responseCode = App.CONNECTION_ERROR;
                        }

                        apiResponse.responseError(PostPaymentAPI.this);
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
        paymentPost.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        Volley.newRequestQueue(context).add(paymentPost);
    }
}
