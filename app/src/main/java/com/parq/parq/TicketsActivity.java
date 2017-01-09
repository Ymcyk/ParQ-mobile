package com.parq.parq;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parq.parq.connection.Parking;
import com.parq.parq.connection.Schedule;
import com.parq.parq.connection.TicketsAPI;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TicketsActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner parkingSpinner;
    private EditText dateLabel;
    private TextView fromText;
    private TextView toText;
    private Button buyTicketButton;

    private TicketsAPI api;
    private ArrayAdapter<Parking> parkingAdapter;

    private DatePickerDialog dateDialog;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        parkingSpinner = (Spinner) findViewById(R.id.parking_spinner);
        dateLabel = (EditText) findViewById(R.id.date_label);
        fromText = (TextView) findViewById(R.id.from_text);
        toText = (TextView) findViewById(R.id.to_text);
        buyTicketButton = (Button) findViewById(R.id.buy_ticket_button);

        parkingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        parkingSpinner.setAdapter(parkingAdapter);

        api = new TicketsAPI(this);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        setDateLabel();

        api.requestParkings();
    }

    private void setDateLabel() {
        dateLabel.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();

        dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                dateLabel.setText(dateFormat.format(newDate.getTime()));
                
                if(datePicker.isShown()){
                    Parking parking = (Parking) parkingSpinner.getSelectedItem();
                    api.requestSchedules(year, month+1, dayOfMonth, parking.getId());
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    public void parkingsRequestSuccess(List<Parking> list){
        this.parkingAdapter.clear();
        this.parkingAdapter.notifyDataSetChanged();

        this.parkingAdapter.addAll(list);
        this.parkingAdapter.notifyDataSetChanged();
    }

    public void scheduleRequestSuccess(Schedule schedule) {
        if(schedule == null) {
            Toast.makeText(this, "No schedule for given date", Toast.LENGTH_LONG).show();
            fromText.setText("");
            toText.setText("");
            return;
        }
        fromText.setText(schedule.getStart().toString());
        toText.setText(schedule.getEnd().toString());
    }

    public void connectionError(int errorCode) {
        switch (errorCode){
            case App.UNAUTHENTICATED:
                Toast.makeText(this, "Unauthenticated", Toast.LENGTH_LONG).show();
                break;
            case App.CONNECTION_ERROR:
                Toast.makeText(this, "Connection error", Toast.LENGTH_LONG).show();
                break;
            case App.PARSE_ERROR:
                Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if(view == buyTicketButton){

        } else if(view == dateLabel) {
            dateDialog.show();
        }
    }
}
