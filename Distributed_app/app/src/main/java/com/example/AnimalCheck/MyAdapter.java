package com.example.AnimalCheck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

    final List<Device> devices;

    private final MyViewHolder.ItemClickListener myClickListener;

    public MyAdapter( List<Device> listOfDevices, MyViewHolder.ItemClickListener clickListener) {
        super();
        devices = listOfDevices;
        this.myClickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_card, parent, false);
        return new MyViewHolder(v, myClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bindValues(devices.get(position));
    }


    @Override
    public int getItemCount() {
        return devices.size();
    }

}
