package ru.hse.socialnetwork;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DeviceActivity extends ListActivity {

    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver bReciever;
    public static final int LABEL = 1;


   // ключ мас-адресс, а значение Имя блютуза
    Map<BluetoothDevice,String> discoveredDevices = new HashMap<BluetoothDevice, String>();


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
                        discoveredDevices.put(device, device.getName().substring(LABEL));
                    }
                }
                String[] stockArr=discoveredDevices.values().toArray(new String[discoveredDevices.size()]);
                setListAdapter(new MyArrayAdapter(DeviceActivity.this, stockArr));
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bReciever, filter);
    }

    // Получаем девайс по имени
    public BluetoothDevice GetKey(Map<BluetoothDevice,String> map, String value){
        Set<Map.Entry<BluetoothDevice,String>> entrySet=map.entrySet();

        for (Map.Entry<BluetoothDevice,String> pair : entrySet) {
            if (value.equals(pair.getValue())) {
                return pair.getKey();// нашли наше значение и возвращаем  ключ
            }
        }
        return null;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String name = (String) getListAdapter().getItem(position);

        // Получаем девайс по имени
        BluetoothDevice pairDevice = GetKey(discoveredDevices, name);

        // Если девайс пейренный
        if (pairDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("device", pairDevice);
            startActivity(intent);
        } else {
            // иначе осуществляем пейринг
            pairingDevice(pairDevice);
        }
    }

    // пейринг устройства
    private void pairingDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
