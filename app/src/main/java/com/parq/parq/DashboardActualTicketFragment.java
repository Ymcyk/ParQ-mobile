package com.parq.parq;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.TicketListAPI;
import com.parq.parq.models.Ticket;

import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardActualTicketFragment extends ListFragment implements APIResponse {
    private TwoLineTicketAdapter ticketAdapter;
    private TicketListAPI ticketListAPI;
    private TextView ticketStatus;

    public DashboardActualTicketFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_actual_ticket, container, false);
        ticketAdapter = new TwoLineTicketAdapter(getContext());

        ticketListAPI = new TicketListAPI(getContext(), this);
        ticketStatus = (TextView) view.findViewById(R.id.ticket_status);

        setListAdapter(ticketAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ticketListAPI.requestTickets();
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        List<Ticket> list = ticketListAPI.getTicketList();
        if(list.size() == 0){
            ticketStatus.setText("Brak biletów");
        } else {
            ticketStatus.setText("Ważne bilety");

            this.ticketAdapter.clear();
            this.ticketAdapter.notifyDataSetChanged();

            this.ticketAdapter.setList(list);
            this.ticketAdapter.addAll(list);
            this.ticketAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        switch(abstractAPI.getResponseCode()) {
            case App.HTTP_401:
            case App.HTTP_403:
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

class TwoLineTicketAdapter extends ArrayAdapter<Ticket> {
    private List<Ticket> list;

    public TwoLineTicketAdapter(Context context){
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

        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);

        text2.setGravity(Gravity.END | Gravity.RIGHT);

        Ticket ticket = list.get(position);
        text1.setText(ticket.getName());

        String data = String.format(Locale.ENGLISH, "%s %02d:%02d - %02d:%02d", ticket.getParkingName(),
                ticket.getStart().getHourOfDay(), ticket.getStart().getMinuteOfHour(),
                ticket.getEnd().getHourOfDay(), ticket.getEnd().getMinuteOfHour());
        text2.setText(data);

        return view;
    }
}