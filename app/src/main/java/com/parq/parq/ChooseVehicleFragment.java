package com.parq.parq;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.VehicleListAPI;
import com.parq.parq.models.Vehicle;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseVehicleFragment extends ListFragment implements APIResponse {
    private VehicleListAPI vehicleListAPI;
    private ArrayAdapter<Vehicle> vehicleArrayAdapter;
    private static Vehicle vehicle;

    public ChooseVehicleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_vehicle, container, false);

        vehicleListAPI = new VehicleListAPI(getContext(), this);
        vehicleArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        setListAdapter(vehicleArrayAdapter);

        vehicleListAPI.requestVehicleList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Wybierz pojazd");
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.vehicleListAPI){
            List<Vehicle> list = this.vehicleListAPI.getVehicleList();
            this.vehicleArrayAdapter.clear();
            this.vehicleArrayAdapter.notifyDataSetChanged();

            this.vehicleArrayAdapter.addAll(list);
            this.vehicleArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        switch (abstractAPI.getResponseCode()) {
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        vehicle = (Vehicle) getListAdapter().getItem(position);

        Fragment fragment = new BuyTicketFragment();

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(BuyTicketFragment.class.toString());
        ft.commit();
    }

    public static Vehicle getVehicle() {
        return ChooseVehicleFragment.vehicle;
    }

}


