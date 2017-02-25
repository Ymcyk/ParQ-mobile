package com.parq.parq;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parq.parq.models.Parking;
import com.parq.parq.models.Ticket;
import com.parq.parq.models.Vehicle;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardParkingFragment extends ListFragment {
    private TwoLineParkingAdapter adapter;

    public DashboardParkingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_parking, container, false);
        adapter = new TwoLineParkingAdapter(getContext());
        setListAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ParQ");
    }

    @Override
    public void onStart() {
        super.onStart();

        List<Parking> list = App.getParkingList();
        this.adapter.clear();
        this.adapter.notifyDataSetChanged();

        this.adapter.setList(list);
        this.adapter.addAll(list);
        this.adapter.notifyDataSetChanged();
    }
}

class TwoLineParkingAdapter extends ArrayAdapter<Parking> {
    private List<Parking> list;

    public TwoLineParkingAdapter(Context context){
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1);
    }

    public void setList(List<Parking> list) {
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

        Parking parking = list.get(position);
        text1.setText(parking.getName());
        Log.i("TwoLineParkingAdapter", parking.getName());

        String date;
        if(parking.isDatesSet()){
            date = String.format(Locale.ENGLISH, "%02d:%02d - %02d:%02d",
                    parking.getStart().getHourOfDay(), parking.getStart().getMinuteOfHour(),
                    parking.getEnd().getHourOfDay(), parking.getEnd().getMinuteOfHour());
        } else {
            date = "nieczynne";
        }

        Log.i("TwoLineParkingAdapter", date);
        text2.setText(date);

        return view;
    }
}