package com.example.AnimalCheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MQTTSubscriber extends AppCompatActivity {
    final String serverUri = "tcp://192.168.1.38";
    final String subscriptionTopic = "animalcheck/ancce/stable1";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "Client18185";
    static boolean init = true;

    TextView tvnumberOfDevices;
    private int devices_connected;
    static List<Device> list_expected;
    public static Bitmap imageConnected, imageDisconnected;
    private PieChart chart;
    Button bDevices;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);

        list_expected = new ArrayList<>();
        listOfDevices();

        bDevices = findViewById(R.id.bDevices);

        chart = findViewById(R.id.chart);
        setUpChart();

        int dev= devices_connected;
        int abs = (list_expected.size()-dev);
        loadPieChartData(abs,dev);

        try {
            InputStream is = getAssets().open("connected.png");
            imageConnected = BitmapFactory.decodeStream(is);
            is = getAssets().open("disconnected.png");
            imageDisconnected = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    subscribeToTopic();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getApplicationContext(), "The Connection was lost.", Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void messageArrived(String topic, MqttMessage message_mqtt) {
                    process_message(message_mqtt.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName("mqtts");
        mqttConnectOptions.setPassword("spswd123".toCharArray());
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    if(init) {
                        subscribeToTopic();
                        init = false;
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Failed to connect to: " + serverUri +
                            ". Cause: " + ((exception.getCause() == null) ?
                            exception.toString() : exception.getCause()), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setUpChart(){

        chart.setHoleRadius(0);
        chart.setUsePercentValues(true);
        chart.setEntryLabelTextSize(12);
        chart.setEntryLabelColor(Color.BLACK);
        chart.getDescription().setEnabled(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData(int absent, int located){
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(located, "Located"));
        entries.add(new PieEntry(absent, "Absent"));

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(Color.parseColor("#FF6200EE"), Color.parseColor("#BBB7C1"));

        PieData data = new PieData(dataset);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);
        chart.invalidate();
    }

    
    public void onDevicesClick(View view) {
        Intent i = new Intent(this, ListOfDevices.class);
        startActivity(i);
    }


    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Subscribed to: " + subscriptionTopic, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Failed to subscribe", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void process_message (String message){

        Log.e("PRUEBA", message);

        if(message.equals("END SCANNING")){

            for(int i=0; i<list_expected.size(); i++){
                if(!list_expected.get(i).isChange()){
                    Device dev = list_expected.get(i);
                    dev.setConnected(false);
                }
                else{
                    list_expected.get(i).setChange(false);
                }
            }
            Log.e("PRUEBA", "End scanning");
            devices_connected = 0;
            for(int i=0; i<list_expected.size(); i++){
                if(list_expected.get(i).isConnected()){
                    devices_connected++;
                }
            }
            tvnumberOfDevices = findViewById(R.id.number_devices);
            Log.e("PRUEBA", String.valueOf(devices_connected));
            tvnumberOfDevices.setText("Number of horses in the stable: "+devices_connected);
            int dev=devices_connected;
            int abs = (list_expected.size()-dev);
            loadPieChartData(abs,dev);
            Log.e("PRUEBA", "Actualiza grafico");
        }

        String[] arrOfStr = message.split(" ");
        for(int i=0; i<list_expected.size(); i++){
            if(list_expected.get(i).getName().equals(arrOfStr[0])){
                list_expected.get(i).setIntensity(arrOfStr[1]);
                list_expected.get(i).setWeight(Double.parseDouble(arrOfStr[2]));
                list_expected.get(i).setTemperature(Double.parseDouble(arrOfStr[3]));
                list_expected.get(i).setAge(Integer.parseInt(arrOfStr[4]));
                list_expected.get(i).setHeartBeat(Integer.parseInt(arrOfStr[5]));
                list_expected.get(i).setConnected(true);
                list_expected.get(i).setChange(true);
                Device dev = list_expected.get(i);
                if(dev.getTemperature() > 38.5 || dev.getTemperature() < 37 ||
                        dev.getHeartBeat() > 40 || dev.getHeartBeat() < 28 ){
                   list_expected.get(i).setAlarm(true);
                   alarmHealth(dev.getName());
                }
                else{
                    list_expected.get(i).setAlarm(false);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void alarmHealth(String name){
        AlertDialog.Builder information = new AlertDialog.Builder(MQTTSubscriber.this, R.style.AlertDialogStyle);
        String infom = "The health of <b>" + name + " </b>may be at risk. You should go to the vet.</b> ";
        information.setMessage(Html.fromHtml(infom,Html.FROM_HTML_MODE_LEGACY));
        information.setCancelable(true).setNegativeButton("Exit", (dialog, which) -> dialog.cancel());
        AlertDialog info = information.create();
        info.setTitle("ALARM!!");
        info.show();
    }

    public static void listOfDevices(){
        Device dev1 = new Device ("Horse1", "Not connected");
        list_expected.add(dev1);
        Device dev2 = new Device ("Horse2", "Not connected");
        list_expected.add(dev2);
        Device dev3 = new Device ("Horse3", "Not connected");
        list_expected.add(dev3);
        Device dev4 = new Device ("Horse4", "Not connected");
        list_expected.add(dev4);
        Device dev5 = new Device ("Horse5", "Not connected");
        list_expected.add(dev5);
    }

}

