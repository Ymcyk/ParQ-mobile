package com.parq.parq;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.PostTicketAPI;
import com.parq.parq.models.Parking;
import com.parq.parq.models.Ticket;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuyTicketFragment extends Fragment implements View.OnClickListener, APIResponse {
    private Spinner parkingSpinner;
    private EditText minuteLabel;
    private Button acceptButton;
    private PostTicketAPI postTicketAPI;

    private ArrayAdapter<Parking> adapter;

    public BuyTicketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_ticket, container, false);

        parkingSpinner = (Spinner) view.findViewById(R.id.parking_spinner);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        parkingSpinner.setAdapter(adapter);

        minuteLabel = (EditText) view.findViewById(R.id.minutes_label);
        acceptButton = (Button) view.findViewById(R.id.accept_ticket_button);
        acceptButton.setOnClickListener(this);

        postTicketAPI = new PostTicketAPI(getContext(), this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Kup bilet");
    }

    private boolean checkIfOpen() {
        Parking parking = (Parking) parkingSpinner.getSelectedItem();
        if(parking == null){
            return false;
        }
        DateTime now = DateTime.now();
        DateTime end = parking.getEnd();

        if(end != null && now.isBefore(end.getMillis()))
            return true;

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        List<Parking> list = App.getParkingList();
        this.adapter.clear();
        this.adapter.notifyDataSetChanged();

        this.adapter.addAll(list);
        this.adapter.notifyDataSetChanged();

        checkIfOpen();
    }

    @Override
    public void onClick(View view) {
        if(view == this.acceptButton) {
            if(checkIfOpen()) {
                //getFragmentManager().popBackStack();
                Ticket ticket = new Ticket();
                ticket.setMinutes(minuteLabel.getText().toString());
                Parking parking = (Parking) parkingSpinner.getSelectedItem();
                ticket.setParkingId(parking.getId());
                ticket.setVehicleId(ChooseVehicleFragment.getVehicle().getId());
                postTicketAPI.postTicket(ticket);
            } else {
                Toast.makeText(getContext(), "Parking zamknięty", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.postTicketAPI){
            Toast.makeText(getContext(), "Added new ticket", Toast.LENGTH_LONG).show();
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        switch (abstractAPI.getResponseCode()) {
            case App.HTTP_401:
            case App.HTTP_403:
                Toast.makeText(getContext(), "Unauthenticated", Toast.LENGTH_LONG).show();
                break;
            case App.HTTP_406:
                Toast.makeText(getContext(), "Not enough money", Toast.LENGTH_LONG).show();
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
