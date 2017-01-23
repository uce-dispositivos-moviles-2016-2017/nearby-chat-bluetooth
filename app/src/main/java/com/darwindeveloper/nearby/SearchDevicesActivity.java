package com.darwindeveloper.nearby;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.darwindeveloper.nearby.search_devices.Dispositivo;
import com.darwindeveloper.nearby.search_devices.SearchDeviceAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * esta clase se encarga de buscar los dispositivos bluetooth cercanos y delvolver el MAC ADRESS y el NOMBRE del dispositivo bluetooth
 */
public class SearchDevicesActivity extends AppCompatActivity implements SearchDeviceAdapter.OnItemClickListener {


    public static final String SEARCH_DEVICE_MAC = "com.darwindeveloper.SEARCH_DEVICE_MAC";
    public static final String SEARCH_DEVICE_NAME = "com.darwindeveloper.SEARCH_DEVICE_NAME";

    private SearchDeviceAdapter searchDeviceAdapter;
    private ArrayList<Dispositivo> dispositivos_encontrados = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_devices);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        // start listening for refresh local file list in
        registerReceiver(mBroadcastReceiverBluetooth, filter);


        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_search_devices);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchDevicesActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        dispositivos_encontrados.add(new Dispositivo(true, "Dispositivos Encontrados"));
        searchDeviceAdapter = new SearchDeviceAdapter(SearchDevicesActivity.this, dispositivos_encontrados);
        searchDeviceAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(searchDeviceAdapter);

        //iniciazmos la busqueda de dispositivos
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();


        AppCompatButton button_close = (AppCompatButton) findViewById(R.id.btn_close);
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    @Override
    public void onItemClick(Dispositivo dispositivo, int position) {

        try {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(dispositivo.getAdress());
            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(device);
            if (returnValue.booleanValue()) {
                setResult(RESULT_OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }


    // BroadcastReceiverpara buscar dispositivos bluetooth cercanos
    private final BroadcastReceiver mBroadcastReceiverBluetooth = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                    //agregamos el elemento encontrado a la lista
                    dispositivos_encontrados.add(new Dispositivo(device.getName(), device.getAddress(), false));
                    searchDeviceAdapter.notifyItemInserted(dispositivos_encontrados.size() - 1);
                    searchDeviceAdapter.notifyDataSetChanged();

                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                findViewById(R.id.progress_layout).setVisibility(View.GONE);

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mBroadcastReceiverBluetooth);
    }


}
