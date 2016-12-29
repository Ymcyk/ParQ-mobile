package com.parq.parq;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parq.parq.connection.Profile;
import com.parq.parq.connection.ProfileActivityAPI;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView usernameText;
    private TextView emailText;
    private TextView walletText;

    private EditText amountText;
    private Button payButton;

    private ProfileActivityAPI api;

    public final static int PAYPAL_REQUEST_CODE = 42;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    private String paymentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameText = (TextView) findViewById(R.id.username_text);
        emailText = (TextView) findViewById(R.id.email_text);
        walletText = (TextView) findViewById(R.id.wallet_text);

        amountText = (EditText) findViewById(R.id.amount_label);
        payButton = (Button) findViewById(R.id.pay_button);

        payButton.setOnClickListener(this);

        api = new ProfileActivityAPI(this);
    }

    @Override
    public void onClick(View view) {
        getPayment();
    }

    private void getPayment() {
        paymentAmount = amountText.getText().toString();

        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)),
                "PLN", "Ticket Fee", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        api.requestProfile();
    }

    public void profileRequestSuccess(Profile profile) {
        usernameText.setText(profile.getUsername());
        emailText.setText(profile.getEmail());
        walletText.setText(profile.getWallet());
    }

    public void connectionError(int errorCode) {
        switch (errorCode){
            case ProfileActivityAPI.BAD_TOKEN:
                Toast.makeText(this, "Bad token", Toast.LENGTH_LONG).show();
                break;
            case ProfileActivityAPI.CONNECTION_ERROR:
                Toast.makeText(this, "Connection error", Toast.LENGTH_LONG).show();
                break;
            case ProfileActivityAPI.PARSE_ERROR:
                Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, PayPalService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != PAYPAL_REQUEST_CODE)
            return;

        if(resultCode == Activity.RESULT_OK){
            PaymentConfirmation confirm = data.getParcelableExtra(
                    PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if(confirm != null){
                try {
                    String paymentDetils = confirm.toJSONObject().toString(4);
                    Log.i("payment", paymentDetils);
                    startActivity(new Intent(this, ConfirmationActivity.class)
                        .putExtra("PaymentResultJson", paymentDetils)
                        .putExtra("PaymentAmount", paymentAmount));
                } catch(JSONException e) {
                    Log.e("payment", "Error occurred on parsing: ", e);
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            Log.i("payment", "User canceled");
        } else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Log.i("payment", "Invalid payment or PayPalConfiguration");
    }
}
