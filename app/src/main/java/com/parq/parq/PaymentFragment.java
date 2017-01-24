package com.parq.parq;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.GetProfileAPI;
import com.parq.parq.connection.PostPaymentAPI;
import com.parq.parq.models.Profile;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment implements APIResponse, View.OnClickListener {
    private EditText amountText;
    private Button payButton;

    private GetProfileAPI api;
    private PostPaymentAPI postPaymentApi;

    public final static int PAYPAL_REQUEST_CODE = 42;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    private String paymentAmount;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        postPaymentApi = new PostPaymentAPI(getContext(), this);
        api = new GetProfileAPI(getContext(), this);

        amountText = (EditText) view.findViewById(R.id.amount_label);
        payButton = (Button) view.findViewById(R.id.pay_button);

        payButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    sendPayment(paymentDetils);
                    //startActivity(new Intent(this, ConfirmationActivity.class)
                    //    .putExtra("PaymentResultJson", paymentDetils)
                    //    .putExtra("PaymentAmount", paymentAmount));
                } catch(JSONException e) {
                    Log.e("payment", "Error occurred on parsing: ", e);
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            Log.i("payment", "User canceled");
        } else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Log.i("payment", "Invalid payment or PayPalConfiguration");
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Doładowanie");
    }

    private void sendPayment(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONObject jsonResponse = json.getJSONObject("response");

            String id = jsonResponse.getString("id");

            postPaymentApi.postPayment(id, Double.valueOf(paymentAmount));
        } catch (JSONException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(this.api == abstractAPI) {
            Profile profile = this.api.getProfile();
            App.getProfile().setWallet(profile.getWallet());
            getFragmentManager().popBackStack();
        } else if(this.postPaymentApi == abstractAPI) {
            Toast.makeText(getContext(), "Payment success", Toast.LENGTH_LONG).show();
            api.requestProfile();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        if(this.api == abstractAPI || this.postPaymentApi == abstractAPI){
            switch (this.api.getResponseCode()) {
                case App.HTTP_401:
                    Toast.makeText(getContext(), "Unauthenticated", Toast.LENGTH_LONG).show();
                    break;
                case App.PARSE_ERROR:
                    Toast.makeText(getContext(), "Parse error", Toast.LENGTH_LONG).show();
                    break;
                case App.CONNECTION_ERROR:
                default:
                    Toast.makeText(getContext(), "Connection error", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == payButton)
            getPayment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().stopService(new Intent(getContext(), PayPalService.class));
    }

    private void getPayment() {
        paymentAmount = amountText.getText().toString();

        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)),
                "PLN", "Ticket Fee", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }
}
