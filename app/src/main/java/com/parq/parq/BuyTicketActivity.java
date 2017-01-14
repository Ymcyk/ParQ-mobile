package com.parq.parq;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.PostTicketAPI;
import com.parq.parq.connection.VehicleListAPI;
import com.parq.parq.models.Ticket;
import com.parq.parq.models.Vehicle;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BuyTicketActivity extends AppCompatActivity implements View.OnClickListener, APIResponse {
    private Button fromLabel;
    private Button toLabel;
    private Spinner vehiclesSpinner;
    private Button buyTicket;

    private ArrayAdapter<Vehicle> adapter;
    private TimePickerDialog fromDialog;
    private TimePickerDialog toDialog;

    private PostTicketAPI postTicketApi;
    private VehicleListAPI vehicleListAPI;

    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        Bundle mBundle = getIntent().getExtras();

        ticket = new Ticket(
                mBundle.getInt("year"),
                mBundle.getInt("month"),
                mBundle.getInt("day")
        );
        ticket.setParkingId(mBundle.getInt("parkingId"));

        postTicketApi = new PostTicketAPI(getApplicationContext(), this);
        vehicleListAPI = new VehicleListAPI(getApplicationContext(), this);

        setViews();

        setTimeLabels(ticket);
    }

    private void setViews() {
        fromLabel = (Button) findViewById(R.id.from_label);
        toLabel = (Button) findViewById(R.id.to_label);
        buyTicket = (Button) findViewById(R.id.accept_ticket_button);
        buyTicket.setOnClickListener(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        vehiclesSpinner = (Spinner) findViewById(R.id.vehicles_spinner);
        vehiclesSpinner.setAdapter(adapter);
    }

    private void setTimeLabels(final Ticket ticket) {
        fromLabel.setOnClickListener(this);
        toLabel.setOnClickListener(this);

        fromLabel.setText(String.format(Locale.ENGLISH, "%02d:%02d",
                ticket.getStart().get(Calendar.HOUR_OF_DAY),
                ticket.getStart().get(Calendar.MINUTE)));
        toLabel.setText(String.format(Locale.ENGLISH, "%02d:%02d",
                ticket.getEnd().get(Calendar.HOUR_OF_DAY),
                ticket.getEnd().get(Calendar.MINUTE)));

        Calendar newCalendar = Calendar.getInstance();
        fromDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.i("fromDialog", String.format(Locale.ENGLISH, "onTimeSet: %d:%d", hour, minute));
                fromLabel.setText(String.format(Locale.ENGLISH, "%02d:%02d", hour, minute));

                ticket.getStart().set(Calendar.HOUR_OF_DAY, hour);
                ticket.getStart().set(Calendar.MINUTE, minute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        toDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                toLabel.setText(String.format(Locale.ENGLISH, "%02d:%02d", hour, minute));

                ticket.getEnd().set(Calendar.HOUR_OF_DAY, hour);
                ticket.getEnd().set(Calendar.MINUTE, minute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        vehicleListAPI.requestVehicleList();
    }

    @Override
    public void onClick(View view) {
        if(view == fromLabel){
            fromDialog.show();
        } else if(view == toLabel){
            toDialog.show();
        } else if(view == buyTicket) {
            Vehicle vehicle = (Vehicle) vehiclesSpinner.getSelectedItem();
            ticket.setVehicleId(vehicle.getId());
            Log.i("Ticket", ticket.toString());
            postTicketApi.postTicket(ticket);
        }
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.vehicleListAPI) {
            List<Vehicle> list = this.vehicleListAPI.getVehicleList();
            this.adapter.clear();
            this.adapter.notifyDataSetChanged();

            this.adapter.addAll(list);
            this.adapter.notifyDataSetChanged();
        } else if(abstractAPI == this.postTicketApi) {
            Toast.makeText(this, "Added new ticket", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        switch(abstractAPI.getResponseCode()) {
            case App.HTTP_401:
            case App.HTTP_403:
                Toast.makeText(this, "Unauthenticated", Toast.LENGTH_LONG).show();
                break;
            case App.HTTP_406:
                Toast.makeText(this, "Not enough money", Toast.LENGTH_LONG).show();
                break;
            case App.PARSE_ERROR:
                Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
                break;
            case App.CONNECTION_ERROR:
            default:
                Toast.makeText(this, "Connection error", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
