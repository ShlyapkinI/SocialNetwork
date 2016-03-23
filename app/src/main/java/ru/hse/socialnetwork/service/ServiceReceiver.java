package ru.hse.socialnetwork.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Илья on 23.03.2016.
 */
public class ServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, ServerService.class);
        context.startService(myIntent);

    }
}