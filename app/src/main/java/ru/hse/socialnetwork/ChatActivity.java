package ru.hse.socialnetwork;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

import ru.hse.socialnetwork.service.ServerService;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private ConnectThread client;
    Handler handler;
    MyReceiver myReceiver;
    WorkWithMessages wwm;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String data = arg1.getStringExtra("Data");
            BluetoothDevice bd= arg1.getParcelableExtra("Device");

            if(device.equals(bd)){
                chatArrayAdapter.add(new ChatMessage(!side, data));
            }
        }

    }

    // устройство друга
    private BluetoothDevice device;

    private boolean side = false;

    @Override
    protected void onDestroy() {
        client.cancel();
        super.onDestroy();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        Intent intent = getIntent();
        device = intent.getParcelableExtra("device");

        wwm = new WorkWithMessages(this);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.singlemessage);

        ArrayList<ru.hse.socialnetwork.Message> messages = wwm.loadMessages(device.getAddress().toString());

        for(ru.hse.socialnetwork.Message msg:messages){
            if(!(msg.getTextOfMessage().contentEquals("")))
                chatArrayAdapter.add(new ChatMessage(!msg.getFlag(), msg.getTextOfMessage()));
        }

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ServerService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("Read");
                String start = bundle.getString("Start");
                String stop = bundle.getString("Stop");

                if(data!=null){
                    chatArrayAdapter.add(new ChatMessage(!side, data));
                }
                if(start!=null){
                    buttonSend.setVisibility(View.VISIBLE);
                    chatText.setVisibility(View.VISIBLE);
                }
                if(stop!=null){
                    finish();
                }
            }
        };

        getSupportActionBar().setIcon(R.drawable.bluetooth_ic);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.listView1);

        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
        chatText.setVisibility(View.GONE);
        chatText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        return sendChatMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    sendChatMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        getSupportActionBar().setTitle(" " + device.getName().substring(1));

        client = new ConnectThread(device, handler);

        new Thread(new Runnable() {
            public void run() {
                client.run();
            }
        }).start();
    }

    private boolean sendChatMessage() throws IOException {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        client.write(chatText.getText().toString().getBytes());
        wwm.saveMessage(device.getAddress().toString(), chatText.getText().toString(), new Date(System.currentTimeMillis()), true);
        chatText.setText("");
        return true;
    }

}
