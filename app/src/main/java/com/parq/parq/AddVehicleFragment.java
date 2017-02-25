package com.parq.parq;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.AddVehicleAPI;
import com.parq.parq.models.Vehicle;


public class AddVehicleFragment extends Fragment implements View.OnClickListener, APIResponse {
    private EditText nameLabel;
    private Spinner plateCountrySpinner;
    private EditText plateNumberLabel;
    private Button addButton;

    private AddVehicleAPI api;

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

    public AddVehicleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_vehicle, container, false);

        setViews(view);
        api = new AddVehicleAPI(getContext(), this);

        return view;
    }

    private void setViews(View view) {
        nameLabel = (EditText) view.findViewById(R.id.name_label);
        plateCountrySpinner = (Spinner) view.findViewById(R.id.plate_country_spinner);
        plateNumberLabel = (EditText) view.findViewById(R.id.plate_number_label);
        addButton = (Button) view.findViewById(R.id.add_button);

        addButton.setOnClickListener(this);
        nameLabel.addTextChangedListener(vehicleTextWatcher);
        plateNumberLabel.addTextChangedListener(vehicleTextWatcher);
    }

    private void checkLabelsForEmptyValues() {
        String name = nameLabel.getText().toString();
        String plateNumber = plateNumberLabel.getText().toString();

        if (name.isEmpty() || plateNumber.isEmpty())
            addButton.setEnabled(false);
        else
            addButton.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view == addButton){
            Vehicle vehicle = new Vehicle();

            vehicle.setName(nameLabel.getText().toString());
            vehicle.setPlateCountry(String.valueOf(plateCountrySpinner.getSelectedItem()));
            vehicle.setPlateNumber(plateNumberLabel.getText().toString());


            api.postVehicle(vehicle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dodaj pojazd");
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api) {
            Toast.makeText(getContext(), "Pojazd dodano", Toast.LENGTH_LONG).show();
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        if(abstractAPI == this.api) {
            switch (abstractAPI.getResponseCode()) {
                case App.HTTP_401:
                case App.HTTP_403:
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
}
