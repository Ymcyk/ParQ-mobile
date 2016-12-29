package com.parq.parq;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ConfirmationActivity extends AppCompatActivity {
    private TextView paymentAmountText;
    private TextView paymentStatusText;
    private TextView paymentIdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        Intent intent = getIntent();

        paymentAmountText = (TextView) findViewById(R.id.payment_amount_text);
        paymentStatusText = (TextView) findViewById(R.id.payment_status_text);
        paymentIdText = (TextView) findViewById(R.id.payment_id_text);

        try {
            JSONObject json = new JSONObject(intent.getStringExtra("PaymentResultJson"));
            showDetails(json.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDetails(JSONObject details, String paymentAmount) throws JSONException {
        paymentIdText.setText(details.getString("id"));
        paymentStatusText.setText(details.getString("state"));
        paymentAmountText.setText(paymentAmount+" PLN");
    }
}
