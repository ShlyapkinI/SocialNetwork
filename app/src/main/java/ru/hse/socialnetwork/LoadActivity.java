package ru.hse.socialnetwork;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class LoadActivity extends AppCompatActivity {
    public static final int REQUEST_DISCOVERABLE_CODE = 1;
    BluetoothAdapter bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Подключаем Блютуз Адаптер и получаем стандартные настройки
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        // Включаем Блютуз
        if (!bluetooth.isEnabled()) bluetooth.enable();

        // Устанавливаем стандартное имя если нет метки устройства
        if (bluetooth.getName().contains("@") != true) {
            bluetooth.setName("@"+bluetooth.getName());
        }

        // установка видимости устройства
        ensureDiscoverable();

    }

    private void ensureDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_CODE);
    }

    //Получение результата с окна
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }, 3000);
        } else finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
    }
}
