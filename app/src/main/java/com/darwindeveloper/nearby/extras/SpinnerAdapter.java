package com.darwindeveloper.nearby.extras;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.darwindeveloper.nearby.R;

import java.util.ArrayList;


/**
 * Created by DARWIN on 2/1/2017.
 */

public class SpinnerAdapter extends ArrayAdapter<Dispositivo> {
    private Context context;
    private ArrayList<Dispositivo> dispositivos;

    public SpinnerAdapter(Context context, int resource, ArrayList<Dispositivo> dispositivos) {
        super(context, resource, dispositivos);
        this.context = context;
        this.dispositivos = dispositivos;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Dispositivo dispositivo = dispositivos.get(position);
        View item = inflater.inflate(R.layout.item_sppiner_device, parent, false);
        TextView mac = (TextView) item.findViewById(R.id.textView_item_device_mac);
        mac.setText(dispositivo.getMAC());
        TextView sub = (TextView) item.findViewById(R.id.textView_item_device_name);
        sub.setText(dispositivo.getNOMBRE());
        return item;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dispositivo dispositivo = dispositivos.get(position);



        LayoutInflater inflater =  LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.item_sppiner_device, parent, false);
        TextView mac = (TextView) item.findViewById(R.id.textView_item_device_mac);
        mac.setText(dispositivo.getMAC());
        TextView sub = (TextView) item.findViewById(R.id.textView_item_device_name);
        sub.setText(dispositivo.getNOMBRE());
        return item;
    }


}
