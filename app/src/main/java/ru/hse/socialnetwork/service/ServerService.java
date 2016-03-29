package ru.hse.socialnetwork.service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import ru.hse.socialnetwork.AcceptThread;
import ru.hse.socialnetwork.ChatActivity;
import ru.hse.socialnetwork.ChatMessage;
import ru.hse.socialnetwork.R;
import ru.hse.socialnetwork.WorkWithMessages;

public class ServerService extends Service {

    Context context = this;
    Handler handler;
    private static final int notificationID = 12;
    private AcceptThread server;
    public final static String MY_ACTION = "MY_ACTION";
    WorkWithMessages wwm;

    public int onStartCommand(Intent intent, int flags, int startId) {

        wwm = new WorkWithMessages(context);

        // создаём Handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("Read");
                String uri = bundle.getString("IMAGE_URI");
                BluetoothDevice device = bundle.getParcelable("Device");

                if(data!=null){
                    if(data.contentEquals(ChatActivity.start_write)){
                        Toast toast = Toast.makeText(getApplicationContext(), "Interlocutor " + device.getName().substring(1) + " Write message in SocialNetwork" , Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }else{
                        wwm.saveMessage(device.getAddress().toString(), data, new Date(System.currentTimeMillis()),false);
                    }
                }

                Intent intent = new Intent();
                intent.setAction(MY_ACTION);
                intent.putExtra("Data", data);
                intent.putExtra("Image", uri);
                intent.putExtra("Device", device);
                sendBroadcast(intent);


                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent notificationIntent = new Intent(context, ChatActivity.class);
                notificationIntent.putExtra("device", device);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                long[] pattern = {500, 500, 500};

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
                notificationBuilder.setSmallIcon(R.drawable.bluetooth_ic)
                        .setContentTitle(context.getString(R.string.TitleNotify) + " " + device.getName().substring(1))
                        .setContentText(data)
                        .setTicker(context.getString(R.string.TickerNotify)).setWhen(System.currentTimeMillis())
                        .setContentIntent(contentIntent)
                        .setSound(alarmSound)
                        .setAutoCancel(true);
                        //.setVibrate(pattern);
                nm.notify(notificationID, notificationBuilder.build());
            }
        };

        someTask();

        return super.onStartCommand(intent, flags, startId);
    }

    void someTask() {
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void run() {

                server = new AcceptThread(handler, getApplicationContext());
                server.run();

            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
