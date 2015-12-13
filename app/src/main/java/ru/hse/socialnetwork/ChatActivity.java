package ru.hse.socialnetwork;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private AcceptThread server;
    private ConnectThread client;
    private String type;
    Handler handler;

    // устройство друга
    private BluetoothDevice device;

    Intent intent;
    private boolean side = false;

    @Override
    protected void onPause() {
        super.onPause();
        if(type.contains("server"))
            server.cancel();
        else
            if(type.contains("client"))
                client.cancel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.chat);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("Key");
                chatArrayAdapter.add(new ChatMessage(!side, data));
            }
        };

        Intent intent = getIntent();

        type = intent.getStringExtra("type");

        getSupportActionBar().setIcon(R.drawable.bluetooth_ic);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.singlemessage);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.chatText);
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

        if(type.contains("client")) {

            Log.d(TAG,"client");
            String name = intent.getStringExtra("name");
            device = intent.getParcelableExtra("device");
            Log.i("ChatActivity", device.toString());

            getSupportActionBar().setTitle(name);

            client = new ConnectThread(device, handler);

            new Thread(new Runnable() {
                public void run() {
                    Log.d(TAG,"run server");
                    client.run();
                }
            }).start();
        }

        if(type.contains("server")){
            Log.d(TAG,"server");
            getSupportActionBar().setTitle("Server");

            server = new AcceptThread(handler);

            new Thread(new Runnable() {
                public void run() {
                    Log.d(TAG,"run server");
                    server.run();
                }
            }).start();
        }
    }



    private boolean sendChatMessage() throws IOException {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        if(type.contains("server")) {
            server.write(chatText.getText().toString().getBytes());
        } else {
            if (type.contains("client")) {
                client.write(chatText.getText().toString().getBytes());
            }
        }
        chatText.setText("");
        //side = !side;
        return true;
    }

}
