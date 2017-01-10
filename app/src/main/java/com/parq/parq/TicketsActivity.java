package com.parq.parq;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parq.parq.connection.Parking;
import com.parq.parq.connection.Schedule;
import com.parq.parq.connection.Ticket;
import com.parq.parq.connection.TicketsAPI;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketsActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner parkingSpinner;
    private EditText dateLabel;
    //private TextView fromText;
    //private TextView toText;
    private TextView scheduleDetailText;
    private Button buyTicketButton;
    private ListView ticketList;

    private TicketsAPI api;
    private ArrayAdapter<Parking> parkingAdapter;
    private TwoLineAdapter ticketAdapter;

    private DatePickerDialog dateDialog;
    private SimpleDateFormat dateFormat;

    private Bundle ticketBundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        parkingSpinner = (Spinner) findViewById(R.id.parking_spinner);
        dateLabel = (EditText) findViewById(R.id.date_label);
        //fromText = (TextView) findViewById(R.id.from_text);
        //toText = (TextView) findViewById(R.id.to_text);
        buyTicketButton = (Button) findViewById(R.id.buy_ticket_button);
        scheduleDetailText = (TextView) findViewById(R.id.schedule_detail_text);
        ticketList = (ListView) findViewById(R.id.tickets_list);

        ticketAdapter = new TwoLineAdapter(this);
        ticketList.setAdapter(ticketAdapter);

        buyTicketButton.setOnClickListener(this);
        parkingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        parkingSpinner.setAdapter(parkingAdapter);

        api = new TicketsAPI(this);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        setDateLabel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        api.requestParkings();
        api.requestTickets(1);
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
                    ticketBundle.putInt("year", year);
                    ticketBundle.putInt("month", month);
                    ticketBundle.putInt("day", dayOfMonth);
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

    public void ticketsRequestSuccess(List<Ticket> list){
        this.ticketAdapter.clear();
        this.ticketAdapter.notifyDataSetChanged();

        this.ticketAdapter.setList(list);
        this.ticketAdapter.addAll(list);
        this.ticketAdapter.notifyDataSetChanged();
    }

    public void scheduleRequestSuccess(Schedule schedule) {
        if(schedule == null) {
            Toast.makeText(this, "No schedule for given date", Toast.LENGTH_LONG).show();
            scheduleDetailText.setText("closed");
            buyTicketButton.setEnabled(false);
            //fromText.setText("");
            //toText.setText("");
            return;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(schedule.getStart());

        Calendar end = Calendar.getInstance();
        end.setTime(schedule.getEnd());

        scheduleDetailText.setText(String.format(Locale.ENGLISH, "%02d:%02d - %02d:%02d",
                start.get(Calendar.HOUR_OF_DAY),
                start.get(Calendar.MINUTE),
                end.get(Calendar.HOUR_OF_DAY),
                end.get(Calendar.MINUTE)));

        buyTicketButton.setEnabled(true);
        //fromText.setText(schedule.getStart().toString());
        //toText.setText(schedule.getEnd().toString());
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
            Parking parking = (Parking) parkingSpinner.getSelectedItem();
            ticketBundle.putInt("parkingId", parking.getId());

            Intent intent = new Intent(this, BuyTicketActivity.class);
            intent.putExtras(ticketBundle);

            startActivity(intent);
        } else if(view == dateLabel) {
            dateDialog.show();
        }
    }
}

class TwoLineAdapter extends ArrayAdapter<Ticket> {
    private List<Ticket> list;

    public TwoLineAdapter(Context context){
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1);
    }

    public void setList(List<Ticket> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

        text1.setText(list.get(position).getName());

        Calendar start = list.get(position).getStart();
        Calendar end = list.get(position).getEnd();
        String text = String.format(Locale.ENGLISH, "%02d:%02d-%02d:%02d %04d-%02d-%02d",
                start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE),
                end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE),
                start.get(Calendar.YEAR), start.get(Calendar.MONTH)+1, start.get(Calendar.DAY_OF_MONTH));

        text2.setText(text);

        return view;
    }
}