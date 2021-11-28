package com.example.AnimalCheck;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView title;
    TextView intensity;
    ItemClickListener listener;
    ImageView imageView;

    public MyViewHolder(View itemView, ItemClickListener listener) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        intensity = itemView.findViewById(R.id.intensity);
        imageView = itemView.findViewById(R.id.ImageView);
        this.listener = listener;
        intensity.setOnClickListener(this);
        title.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    void bindValues(Device device) {
        title.setText(device.getName());
        intensity.setText("Signal Strength: " + device.getIntensity());
        imageView.setImageBitmap(MQTTSubscriber.imageConnected);
        if(!device.isConnected()){
            title.setTextColor(Color.parseColor("#B1B2B3"));
            intensity.setTextColor(Color.parseColor("#B1B2B3"));
            imageView.setImageBitmap(MQTTSubscriber.imageDisconnected);
            intensity.setText("Not connected");
        }
        else if(device.isAlarm()){
            title.setTextColor(Color.parseColor("#E82020"));
            intensity.setTextColor(Color.parseColor("#E82020"));
        }
        else{
            title.setTextColor(Color.parseColor("#FF6200EE"));
            intensity.setTextColor(Color.parseColor("#FF6200EE"));
        }
    }

    @Override
    public void onClick(View view) {
        listener.onItemClick(getAdapterPosition(), view);
    }

    public interface ItemClickListener {
        void onItemClick(int position, View v);
    }

}
