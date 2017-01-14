package com.parq.parq;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ListView listView = (ListView) findViewById(R.id.list_options);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position){
            case 0:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, TicketsActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, VehicleListActivity.class));
                break;
            case 3:
                Toast.makeText(this, "Opcja 4", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
