package com.parq.parq;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.VehicleListAPI;
import com.parq.parq.models.Vehicle;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleListFragment extends ListFragment implements View.OnClickListener, APIResponse {
    private VehicleListAPI api;
    private ArrayAdapter<Vehicle> adapter;
    private FloatingActionButton fab;

    public VehicleListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        Context context = getContext();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        setListAdapter(this.adapter);

        api = new VehicleListAPI(context, this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Twoje pojazdy");
    }

    @Override
    public void onStart() {
        super.onStart();
        api.requestVehicleList();
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api){
            List<Vehicle> list = this.api.getVehicleList();
            this.adapter.clear();
            this.adapter.notifyDataSetChanged();

            this.adapter.addAll(list);
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api){
            switch(abstractAPI.getResponseCode()) {
                case App.HTTP_401:
                    Toast.makeText(getContext(), "Brak uprawnień", Toast.LENGTH_LONG).show();
                    break;
                case App.PARSE_ERROR:
                    Toast.makeText(getContext(), "Błąd parsowania", Toast.LENGTH_LONG).show();
                    break;
                case App.CONNECTION_ERROR:
                default:
                    Toast.makeText(getContext(), "Błąd połączenia", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == fab){
            Fragment fragment = new AddVehicleFragment();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_frame, fragment)
            .addToBackStack("frag");
            ft.commit();
        }
    }
}
