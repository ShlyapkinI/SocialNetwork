package ru.hse.socialnetwork;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

public class DeviceActivity extends ListActivity {

    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver bReciever;
    public static final int LABEL = 1;


   // ключ мас-адресс, а значение Имя блютуза
    Map<String,String> discoveredDevices = new HashMap<String, String>();

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();

        bReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!discoveredDevices.containsKey(device.getAddress()) && device.getName().startsWith("@"))
                    {
                        discoveredDevices.put(device.getAddress(), device.getName().substring(LABEL));
                    }
                }
                String[] stockArr=discoveredDevices.values().toArray(new String[discoveredDevices.size()]);
                setListAdapter(new MyArrayAdapter(DeviceActivity.this, stockArr));
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bReciever, filter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String name = (String) getListAdapter().getItem(position);

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);

    }
}
