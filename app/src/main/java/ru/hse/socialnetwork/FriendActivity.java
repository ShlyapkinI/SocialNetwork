package ru.hse.socialnetwork;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FriendActivity extends ListActivity {

    ListView lvFriend;
    BluetoothAdapter bluetoothAdapter;
    Map<BluetoothDevice,String> bondedDevices = new HashMap<BluetoothDevice, String>();
    public static final int LABEL = 1;

    // Получение Дивайса из MAP
    public BluetoothDevice GetKey(Map<BluetoothDevice,String> map, String value){
        Set<Map.Entry<BluetoothDevice,String>> entrySet=map.entrySet();

        for (Map.Entry<BluetoothDevice,String> pair : entrySet) {
            if (value.equals(pair.getValue())) {
                return pair.getKey();// нашли наше значение и возвращаем  ключ
            }
        }
        return null;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //// выводим список устройст,с которыми мы сопрягались раньше,
        //// не обязательно в этом приложении(которые хранятся в нашем телефоне)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
        // получаем сет из ранее синхронизированных девайсов
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // вычленяем только тех пользователей, которые используют наше приложение
        for (Iterator<BluetoothDevice> it = pairedDevices.iterator(); it.hasNext(); ) {
            BluetoothDevice device = it.next();

            if (device.getName().startsWith("@"))
            {
                bondedDevices.put(device, device.getName().substring(LABEL));
            }
        }
        String[] stockArr=bondedDevices.values().toArray(new String[bondedDevices.size()]);
        // выводим в активити
        setListAdapter(new MyArrayAdapter(this, stockArr));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String name = (String) getListAdapter().getItem(position);

        // Получаем девайс по его имени
        BluetoothDevice pairDevice = GetKey(bondedDevices, name);

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("name", name);
        // передаём девайс в ChatActivity
        intent.putExtra("device", pairDevice);
        startActivity(intent);

    }
}