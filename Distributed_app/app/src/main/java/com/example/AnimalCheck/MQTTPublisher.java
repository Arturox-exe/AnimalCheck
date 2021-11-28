package com.example.AnimalCheck;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

public class MQTTPublisher extends AppCompatActivity {

    final String serverUri = "tcp://192.168.1.38";
    final String publishTopic = "animalcheck/ancce/stable1/vet";
    final String subscriptionTopicVet = "animalcheck/ancce/stable1/vet/check";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "Client1";
    String name, beat, temperature;
    EditText eName, eBeat, eTemperature, eMessage;
    static boolean init_pub = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);

        name = getIntent().getStringExtra("name");
        beat = getIntent().getStringExtra("beat");
        temperature = getIntent().getStringExtra("temperature");

        eMessage = findViewById(R.id.reason);
        eName = findViewById(R.id.name);
        eBeat = findViewById(R.id.beat);
        eTemperature = findViewById(R.id.temp);

        eName.setText(name);
        eName.setFocusable(false);
        eBeat.setText(beat);
        eBeat.setFocusable(false);
        eTemperature.setText(temperature);
        eTemperature.setFocusable(false);

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
                Log.e("PRUEBA", message_mqtt.toString());
                VetRequest(message_mqtt.toString());
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
                    if(init_pub) {
                        subscribeToTopic();
                        init_pub = false;
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

    public void subscribeToTopic() {
        try {

            mqttAndroidClient.subscribe(subscriptionTopicVet, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Subscribed to: " + subscriptionTopicVet, Toast.LENGTH_SHORT).show();
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

    public void onPublishMessage(View view) {

        MqttMessage message = new MqttMessage();

        String publish = name;
        eMessage.setText("");
        message.setPayload(publish.getBytes());
        message.setRetained(false);
        message.setQos(1);
        try {
            mqttAndroidClient.publish(publishTopic, message);
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mqttAndroidClient.isConnected()) {
            Toast.makeText(getApplicationContext(), "Client not connected!", Toast.LENGTH_SHORT).show();
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eMessage.getWindowToken(), 0);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void VetRequest (String message){
        String[] arrOfStr = message.split(" ");
        String horse = arrOfStr[0];
        String mess = arrOfStr[1];
        AlertDialog.Builder information = new AlertDialog.Builder(MQTTPublisher.this);
        String confirm = "<b>"+horse+"</b> located, your vet appointment has been <b>confirmed</b>";
        String fail = "<b>"+horse+"</b> not located,<b> no appointment</b> with the vet has been arranged";
        String infom = "";
        if(mess.equals("Fail")){
            infom = fail;
        }
        if(mess.equals("Success")){
            infom = confirm;
        }
        information.setMessage(Html.fromHtml(infom,Html.FROM_HTML_MODE_LEGACY));
        information.setCancelable(true).setNegativeButton("Exit", (dialog, which) -> dialog.cancel());
        AlertDialog info = information.create();
        info.setTitle("INFORMATION");
        info.show();
    }

}
