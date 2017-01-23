package com.darwindeveloper.nearby;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.nearby.extras.Dispositivo;
import com.darwindeveloper.nearby.extras.DispositivosVinculadosAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MiDispositivoActivity extends AppCompatActivity implements DispositivosVinculadosAdapter.OnClickUnPairButtonListener, DispositivosVinculadosAdapter.OnCLickRenameListener {

    private TextView name, adress;
    private BluetoothAdapter mBluetoothAdapter;

    private RecyclerView recyclerView;
    private DispositivosVinculadosAdapter adapter;
    private ArrayList<Dispositivo> lista_dispositivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_dispositivo);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        name = (TextView) findViewById(R.id.textView_device_name);
        adress = (TextView) findViewById(R.id.textView_device_address);
        adress.setText(mBluetoothAdapter.getAddress());


        lista_dispositivos = getDevices();
        recyclerView = (RecyclerView) findViewById(R.id.dispositivos_vinculados);
        adapter = new DispositivosVinculadosAdapter(lista_dispositivos, MiDispositivoActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MiDispositivoActivity.this));
        adapter.setOnCLickRenameListener(this);
        adapter.setOnOnClickUnPairButtonListener(this);
        recyclerView.setAdapter(adapter);

    }


    /**
     * @return lista de dispositivos vinculados al telefono
     */
    private ArrayList<Dispositivo> getDevices() {


        ArrayList<Dispositivo> dispositivos_vinculados = new ArrayList<>();

        //una coleccion de todos loas dispositivos vinculados al telefono android
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // si hay dispositivos vinculados
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Dispositivo tmp = new Dispositivo(device.getAddress(), device.getName());
                dispositivos_vinculados.add(tmp);
            }
        }

        return dispositivos_vinculados;
    }


    public void setName(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MiDispositivoActivity.this);
        alertDialog.setTitle("Nuevo nombre Bluettoth");

        final EditText input = new EditText(MiDispositivoActivity.this);
        input.setText(name.getText().toString());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String new_name = input.getText().toString();
                        if (new_name.trim().length() > 3) {
                            name.setText(new_name);
                            mBluetoothAdapter.setName(new_name);
                        } else {
                            Toast.makeText(MiDispositivoActivity.this, "Nombre no valido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onItemClickUppair(final Dispositivo dispositivo, final int posicion) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MiDispositivoActivity.this);
        builder.setTitle("¿Desvincular Dispositivo?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Method method = null;
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(dispositivo.getMAC());

                try {
                    method = device.getClass().getMethod("removeBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                    method.invoke(device);
                    lista_dispositivos.remove(posicion);
                    adapter.notifyItemRemoved(posicion);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MiDispositivoActivity.this, "Disp. Desvinculado", Toast.LENGTH_SHORT).show();


                } catch (NoSuchMethodException e) {
                    Toast.makeText(MiDispositivoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MiDispositivoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();

    }

    @Override
    public void onItemClickrenameListener(final Dispositivo dispositivo, final int posicion) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MiDispositivoActivity.this);
        alertDialog.setTitle("Nuevo nombre Bluettoth");

        final EditText input = new EditText(MiDispositivoActivity.this);
        input.setText(dispositivo.getNOMBRE());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String new_name = input.getText().toString();
                        if (new_name.trim().length() > 3) {
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(dispositivo.getMAC());
                            Method method = null;
                            try {
                                method = device.getClass().getMethod("setAlias", String.class);
                                method.invoke(device,new_name);
                                lista_dispositivos.get(posicion).setNOMBRE(new_name);
                                adapter.notifyItemChanged(posicion);
                                adapter.notifyDataSetChanged();

                            } catch (NoSuchMethodException e) {
                                Toast.makeText(MiDispositivoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            } catch (InvocationTargetException e) {
                                Toast.makeText(MiDispositivoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(MiDispositivoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(MiDispositivoActivity.this, "Nombre no valido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}
