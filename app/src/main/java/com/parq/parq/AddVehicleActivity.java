package com.parq.parq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AddVehicleAPI;
import com.parq.parq.models.Vehicle;

public class AddVehicleActivity extends AppCompatActivity implements APIResponse {
    private EditText nameLabel;
    private Spinner plateCountrySpinner;
    private EditText plateNumberLabel;
    private Button addButton;

    private AddVehicleAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        setViews();

        api = new AddVehicleAPI(getApplicationContext(), this);
    }

    private void setViews() {
        nameLabel = (EditText) findViewById(R.id.name_label);
        plateCountrySpinner = (Spinner) findViewById(R.id.plate_country_spinner);
        plateNumberLabel = (EditText) findViewById(R.id.plate_number_label);
        addButton = (Button) findViewById(R.id.add_button);

        nameLabel.addTextChangedListener(vehicleTextWatcher);
        plateNumberLabel.addTextChangedListener(vehicleTextWatcher);
    }

    public void addVehicleOnClick(View view) {
        Vehicle vehicle = new Vehicle();

        vehicle.setName(nameLabel.getText().toString());
        vehicle.setPlateCountry(String.valueOf(plateCountrySpinner.getSelectedItem()));
        vehicle.setPlateNumber(plateNumberLabel.getText().toString());

        api.postVehicle(vehicle);
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

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api) {
            Toast.makeText(this, "Vehicle added", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api) {
            switch (abstractAPI.getResponseCode()) {
                case App.HTTP_401:
                case App.HTTP_403:
                    Toast.makeText(this, "Unauthenticated", Toast.LENGTH_LONG).show();
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
}
