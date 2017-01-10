package com.parq.parq;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.parq.parq.models.Vehicle;
import com.parq.parq.connection.VehicleListAPI;

import java.util.List;

public class VehicleListActivity extends ListActivity {
    private VehicleListAPI api;
    private ArrayAdapter<Vehicle> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(this.adapter);

        api = new VehicleListAPI(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        api.requestVehicleList();
    }

    public void addVehicleOnClick(View view){
        Intent intent = new Intent(this, AddVehicleActivity.class);
        startActivity(intent);
    }

    public void vehicleListRequestSuccess(List<Vehicle> list){
        this.adapter.clear();
        this.adapter.notifyDataSetChanged();

        this.adapter.addAll(list);
        this.adapter.notifyDataSetChanged();
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
}
