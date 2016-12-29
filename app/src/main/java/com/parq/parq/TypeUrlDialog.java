package com.parq.parq;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by piotr on 27.12.16.
 */

public class TypeUrlDialog extends DialogFragment implements View.OnClickListener {
    private EditText typeURLLabel;
    private Button acceptButton;
    private SharedPreferences sharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_type_url, container, false);

        getSharedPref();
        getDialog().setTitle(getString(R.string.type_url));

        typeURLLabel = (EditText) view.findViewById(R.id.type_url_label);
        String url = getURLFromSharedPref();
        if(url != null)
            typeURLLabel.setText(url);

        acceptButton = (Button) view.findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        saveURLToSharedPref();
        dismiss();
    }

    private void getSharedPref() {
        Context context = getContext();
        sharedPref = context.getSharedPreferences(getString(R.string.sharedpref_file_key),
                Context.MODE_PRIVATE);
    }

    private void saveURLToSharedPref() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.sharedpref_url_slug),
                typeURLLabel.getText().toString());
        editor.apply();
    }

    private String getURLFromSharedPref() {
        return sharedPref.getString(getString(R.string.sharedpref_url_slug), null);
    }
}
