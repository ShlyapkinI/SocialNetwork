package ru.hse.socialnetwork;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ru.hse.socialnetwork.service.ServerService;

public class LoadActivity extends AppCompatActivity {
    public static final int REQUEST_DISCOVERABLE_CODE = 1;
    BluetoothAdapter bluetooth;
    Context context = this;

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
        if (!bluetooth.getName().contains("@")) bluetooth.setName("@" + bluetooth.getName());

        // установка видимости устройства
        ensureDiscoverable();

        // запускаем сервис
        startService(new Intent(this, ServerService.class));

    }
    // если устройство не видимо, то делаем его видимым при старте
    private void ensureDiscoverable() {
        //if (bluetooth.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
            startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_CODE);
        //}
        //else {
        //    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //    startActivity(intent);
        //}
    }

    //Получение результата с окна
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        } else finish();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
    }
}
