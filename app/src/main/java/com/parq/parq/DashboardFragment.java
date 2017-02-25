package com.parq.parq;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parq.parq.connection.APIResponse;
import com.parq.parq.connection.AbstractAPI;
import com.parq.parq.connection.GetProfileAPI;
import com.parq.parq.connection.ParkingListAPI;
import com.parq.parq.connection.TicketListAPI;
import com.parq.parq.models.Parking;
import com.parq.parq.models.Profile;
import com.parq.parq.models.Ticket;

import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements APIResponse {
    private ParkingListAPI parkingApi;
    private GetProfileAPI getProfileAPI;
    private TextView moneyAmount;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.parkingApi = new ParkingListAPI(getContext(), this);
        this.getProfileAPI = new GetProfileAPI(getContext(), this);
        this.moneyAmount = (TextView) view.findViewById(R.id.money_amount);

        this.parkingApi.requestParkings();

        showTicket();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("STAN KONTA", "onReasume()");
        //this.moneyAmount.setText(String.format(Locale.ENGLISH, "%s zł", App.getProfile().getWallet()));
        getProfileAPI.requestProfile();
    }

    private void showParking() {
        Fragment fragment = new DashboardParkingFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.parkings_frame, fragment);
        ft.commit();
    }

    private void showTicket() {
        Fragment fragment = new DashboardActualTicketFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.ticket_frame, fragment);
        ft.commit();
    }

    @Override
    public void responseSuccess(AbstractAPI abstractAPI) {
        if(abstractAPI == this.parkingApi){
            List <Parking> parkingList = this.parkingApi.getParkingList();
            App.setParkingList(parkingList);
            if(parkingList.size() > 0){
                showParking();
            }
        } else if(abstractAPI == this.getProfileAPI) {
            Profile profile = this.getProfileAPI.getProfile();
            App.getProfile().setWallet(profile.getWallet());
            this.moneyAmount.setText(String.format(Locale.ENGLISH, "%s zł", App.getProfile().getWallet()));
        }
    }

    @Override
    public void responseError(AbstractAPI abstractAPI) {
        switch(abstractAPI.getResponseCode()) {
            case App.HTTP_401:
            case App.HTTP_403:
                Toast.makeText(getContext(), "Brak uprawnień", Toast.LENGTH_LONG).show();
                break;
            case App.PARSE_ERROR:
                Toast.makeText(getContext(), "Błąd parsowania", Toast.LENGTH_LONG).show();
                break;
            case App.CONNECTION_ERROR:
            default:
                Toast.makeText(getContext(), "Brak połączenia", Toast.LENGTH_LONG).show();
                break;
        }
    }


}
