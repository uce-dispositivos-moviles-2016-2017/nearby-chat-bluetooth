package com.darwindeveloper.nearby.extras;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darwindeveloper.nearby.R;

import java.util.ArrayList;

/**
 * Created by SONY on 15/1/2017.
 */

public class DispositivosVinculadosAdapter extends RecyclerView.Adapter<DispositivosVinculadosAdapter.MyHolder> {

    private ArrayList<Dispositivo> dispositivos;
    private Context context;
    private OnClickUnPairButtonListener onClickUnPairButton;
    private OnCLickRenameListener onCLickRenameListener;

    public DispositivosVinculadosAdapter(ArrayList<Dispositivo> dispositivos, Context context) {
        this.dispositivos = dispositivos;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        final Dispositivo dispositivo = dispositivos.get(position);
        holder.name.setText(dispositivo.getNOMBRE());
        holder.address.setText(dispositivo.getMAC());

        holder.btn_unPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUnPairButton.onItemClickUppair(dispositivo, position);
            }
        });


        holder.btn_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCLickRenameListener.onItemClickrenameListener(dispositivo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dispositivos.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView name, address;
        private AppCompatButton btn_unPair, btn_rename;

        public MyHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.txt_nombre_dispositivo);
            address = (TextView) itemView.findViewById(R.id.txt_adress);
            btn_unPair = (AppCompatButton) itemView.findViewById(R.id.btn_delete);
            btn_rename = (AppCompatButton) itemView.findViewById(R.id.btn_name);
        }
    }


    public interface OnCLickRenameListener {
        void onItemClickrenameListener(Dispositivo dispositivo, int posicion);
    }

    public void setOnCLickRenameListener(OnCLickRenameListener onCLickRenameListener) {
        this.onCLickRenameListener = onCLickRenameListener;
    }

    public interface OnClickUnPairButtonListener {
        void onItemClickUppair(Dispositivo dispositivo, int posicion);
    }

    public void setOnOnClickUnPairButtonListener(OnClickUnPairButtonListener mOnClickUnPairButton) {
        onClickUnPairButton = mOnClickUnPairButton;
    }

}
