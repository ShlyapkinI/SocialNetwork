package ru.hse.socialnetwork;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DeviceActivity extends ListActivity {

    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver bReciever;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        выводим список устройст,с которыми мы сопрягались раньше,
//         не обязательно в этом приложении(которые хранятся в нашем телефоне)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//        Object[] v = pairedDevices.toArray(new Object[pairedDevices.size()]);
//        String[] stringArray = new String[v.length];
//        for (int i=0; i < v.length; i++) {
//            stringArray[i] = v[i].toString();
//        }
        //здесь мы устанавиваем ресивер,он нужен,чтобы мы добавляли в список устройств те,которые онлайн
        // и с которыми мы не сопрягались
        //код в ресивере срабатывает как только он нашёл новое устройство поблизости с работающим блютузом
        final String[] newDevice = new String[2];
        final List<String> listOfDevice= new ArrayList<>();
        bReciever = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String [] massDevice;
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Create a new device item
                    newDevice[0] =device.getAddress();//это сделано,чтобы когда-нибудь их проверить,правда ли они совпадают
                    //с именами или нет
                    newDevice[1]=device.getName();
                    Toast.makeText(getApplicationContext(), device.getName(),Toast.LENGTH_LONG).show();
                }
//                выведет мас адресса устройств,которые имеют вкл блютуз поблизости
//                listOfDevice.add(newDevice[0]);
//                следующая строка выводит имена устройств с найденными вокруг блютузами
                listOfDevice.add(newDevice[1]);
//                Toast.makeText(getApplicationContext(),"lalala"+newDevice[0],Toast.LENGTH_LONG).show();
                massDevice= new String[listOfDevice.size()];
                for(int i = 0; i < listOfDevice.size(); i++){
                    massDevice[i] = listOfDevice.get(i);
                }
                setListAdapter(new MyArrayAdapter(DeviceActivity.this, massDevice));
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //регистрируем наш ресивер
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
