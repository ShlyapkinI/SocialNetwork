package ru.hse.socialnetwork;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class LoadActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Подключаем Блютуз Адаптер и получаем стандартные настройки
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        // Хотим получить статус устройства
        String status;

        // Если Блютуз включен
        if (bluetooth.isEnabled()) {
            ensureDiscoverable();
            // Получаем MAC-адрес моего устройства
            String mydeviceaddress = bluetooth.getAddress();
            // Получаем текущее имя моего устройства
            String mydevicename = bluetooth.getName();
            // Заполняем статус
            status = mydevicename + " : " + mydeviceaddress;
        } else {
            // Включаем Блютуз
            bluetooth.enable();
            ensureDiscoverable();

            status = "Bluetooth is not Enabled.";
        }

        // Устанавливаем стандартное имя если нет метки устройства
        if (bluetooth.getName().contains("@") != true) {
            bluetooth.setName("@noname");
        }

        // Расскоментировать для проверки статуса

        //Toast toast = Toast.makeText(getApplicationContext(),status, Toast.LENGTH_LONG);
        //toast.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {//для красоты
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }, 3000);

    }

    // установка видимости устройства
    private void ensureDiscoverable() {
        if (bluetooth.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
            startActivity(discoverableIntent);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        LoadActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
