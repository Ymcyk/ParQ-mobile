package com.parq.parq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parq.parq.connection.AddVehicleAPI;
import com.parq.parq.connection.Vehicle;
import com.parq.parq.connection.VehicleListAPI;

public class AddVehicleActivity extends AppCompatActivity {
    private EditText nameLabel;
    private Spinner plateCountrySpinner;
    private EditText plateNumberLabel;
    private Button addButton;

    private AddVehicleAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        nameLabel = (EditText) findViewById(R.id.name_label);
        plateCountrySpinner = (Spinner) findViewById(R.id.plate_country_spinner);
        plateNumberLabel = (EditText) findViewById(R.id.plate_number_label);
        addButton = (Button) findViewById(R.id.add_button);

        nameLabel.addTextChangedListener(vehicleTextWatcher);
        plateNumberLabel.addTextChangedListener(vehicleTextWatcher);

        api = new AddVehicleAPI(this);
    }

    public void addVehicleOnClick(View view) {
        Vehicle vehicle = new Vehicle();

        vehicle.setName(nameLabel.getText().toString());
        vehicle.setPlateCountry(String.valueOf(plateCountrySpinner.getSelectedItem()));
        vehicle.setPlateNumber(plateNumberLabel.getText().toString());

        api.postVehicle(vehicle);
    }

    public void addVehiclePostSuccess(){
        Log.i("AddVehicleActivity", "Before finish");
        finish();
    }

    public void addVehiclePostFailure(){

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

    private TextWatcher vehicleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkLabelsForEmptyValues();
        }
    };

    private void checkLabelsForEmptyValues() {
        String name = nameLabel.getText().toString();
        String plateNumber = plateNumberLabel.getText().toString();

        if (name.isEmpty() || plateNumber.isEmpty())
            addButton.setEnabled(false);
        else
            addButton.setEnabled(true);
    }
}
