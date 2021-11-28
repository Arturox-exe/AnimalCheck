package com.example.AnimalCheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListOfDevices extends AppCompatActivity implements MyViewHolder.ItemClickListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        MyAdapter recyclerViewAdapter = new MyAdapter( MQTTSubscriber.list_expected, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemClick(int position, View v) {
        Device device = MQTTSubscriber.list_expected.get(position);
        if (device.isConnected()) {
            showInfo(device);
        }
        else{
            showNoConnected();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showInfo(Device device){
        AlertDialog.Builder information = new AlertDialog.Builder(ListOfDevices.this);
        String infom = "<b>NAME: </b> " + device.getName() + "<br><br><b>SIGNAL STRENGTH: </b> " + device.getIntensity()
                +"<br><br><b>AGE: </b> " + device.getAge() + "<br><br><b>WEIGHT: </b>" + device.getWeight()
                + "<br><br><b>HEART BEAT: </b>" + device.getHeartBeat() + "<br><br><b>TEMPERATURE: </b>" + device.getTemperature();
        information.setMessage(Html.fromHtml(infom,Html.FROM_HTML_MODE_LEGACY));
        information.setCancelable(false).setNegativeButton("Exit", (dialog, which) -> dialog.cancel());
        information.setNeutralButton("Need Veterinarian",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(getApplicationContext(), MQTTPublisher.class);
                i.putExtra("name", device.getName());
                i.putExtra("beat", String.valueOf(device.getHeartBeat()));
                i.putExtra("temperature", String.valueOf(device.getTemperature()));
                startActivity(i);
            }
        });
        AlertDialog info = information.create();
        info.setTitle("Information of the horse");
        info.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showNoConnected(){
        AlertDialog.Builder information = new AlertDialog.Builder(ListOfDevices.this);
        String infom = "This device is <b>not connected</b>";
        information.setMessage(Html.fromHtml(infom,Html.FROM_HTML_MODE_LEGACY));
        information.setCancelable(true).setNegativeButton("OK", (dialog, which) -> dialog.cancel());
        AlertDialog info = information.create();
        info.setTitle("INFORMATION");
        info.show();
    }
}
