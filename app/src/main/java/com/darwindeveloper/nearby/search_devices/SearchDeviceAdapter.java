package com.darwindeveloper.nearby.search_devices;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.darwindeveloper.nearby.R;

import java.util.List;

/**
 * Created by DARWIN on 28/12/2016.
 */

public class SearchDeviceAdapter extends RecyclerView.Adapter<SearchDeviceAdapter.MyViewHolder> {


    private Context mContext;
    private List<Dispositivo> dispositivos;

    private OnItemClickListener onItemClickListener;

    public SearchDeviceAdapter(Context mContext, List<Dispositivo> dispositivos) {
        this.mContext = mContext;
        this.dispositivos = dispositivos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_seacrh_device, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Dispositivo dispositivo = dispositivos.get(position);
       if(dispositivo.isHeader()){
           holder.header.setVisibility(View.VISIBLE);
           holder.text_header.setText(dispositivo.getNombre());
       }else{
           holder.device.setVisibility(View.VISIBLE);
           holder.nombre_dispositivo.setText(dispositivo.getNombre());
           holder.adress.setText(dispositivo.getAdress());
           if (dispositivo.isVinculado()) {
               holder.icon.setImageResource(R.drawable.ic_bluetooth_audio);
           } else {
               holder.icon.setImageResource(R.drawable.ic_bluetooth);
           }

           //le damos la accion al boton connectar
           holder.button_conn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   onItemClickListener.onItemClick(dispositivo,position);
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        return dispositivos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout header,device;
        TextView nombre_dispositivo, adress,text_header;
        ImageView icon;
        AppCompatButton button_conn;


        MyViewHolder(View view) {
            super(view);
            header=(LinearLayout)view.findViewById(R.id.item_header);
            device=(LinearLayout)view.findViewById(R.id.item_device);
            nombre_dispositivo = (TextView) view.findViewById(R.id.txt_nombre_dispositivo);
            adress = (TextView) view.findViewById(R.id.txt_adress);
            text_header = (TextView) view.findViewById(R.id.text_header);
            icon = (ImageView) view.findViewById(R.id.row_icon);
            button_conn=(AppCompatButton)view.findViewById(R.id.btn_conn);


        }
    }


    public interface OnItemClickListener {
        void onItemClick(Dispositivo dispositivo, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
