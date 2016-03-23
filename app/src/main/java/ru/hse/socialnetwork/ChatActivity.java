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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

import ru.hse.socialnetwork.qr.BarcodeReader;
import ru.hse.socialnetwork.service.ServerService;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private ConnectThread client;
	private static String resultFromQR;
    Handler handler;
    public static String start_write = "b,],p{avsf_4oGFY+{2x";
    MyReceiver myReceiver;
    WorkWithMessages wwm;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String data = arg1.getStringExtra("Data");
            BluetoothDevice bd= arg1.getParcelableExtra("Device");
            if(data!=null){
                if(data.contentEquals(ChatActivity.start_write)){
                    //animation here
                    Log.d(TAG, "massege");
                    return;

                }
            }

            if(device.equals(bd)){
                Log.d(TAG, "massege111");
                chatArrayAdapter.add(new ChatMessage(!side, data, new java.util.Date()));
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
    protected void onResume(){
        super.onResume();
        if(resultFromQR!=null){
            chatText.setText(resultFromQR);
            resultFromQR=null;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        Intent intent = getIntent();
        device = intent.getParcelableExtra("device");

        wwm = new WorkWithMessages(this);
        chatArrayAdapter = new ChatArrayAdapter(this, R.layout.singlemessage);

        ArrayList<ru.hse.socialnetwork.Message> messages = wwm.loadMessages(device.getAddress().toString());

        for(ru.hse.socialnetwork.Message msg:messages){
            if(!(msg.getTextOfMessage().contentEquals("")))
                chatArrayAdapter.add(new ChatMessage(!msg.getFlag(), msg.getTextOfMessage(), msg.getDateAndTime()));
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
                    if(data.contentEquals(start_write)){
                        Toast toast = Toast.makeText(getApplicationContext(), "Собеседник пишет", Toast.LENGTH_SHORT);
                        toast.show();
                    }else
                        chatArrayAdapter.add(new ChatMessage(!side, data, new java.util.Date()));
                }
                if(start!=null){
                    buttonSend.setVisibility(View.VISIBLE);
                    chatText.setVisibility(View.VISIBLE);
                    chatText.requestFocus();
                    Toast toast = Toast.makeText(getApplicationContext(), getApplication().getResources().getString(R.string.Connect_ok), Toast.LENGTH_SHORT);
                    toast.show();
                }
                if(stop!=null){
                    Toast toast = Toast.makeText(getApplicationContext(), getApplication().getResources().getString(R.string.Connect_fall), Toast.LENGTH_SHORT);
                    toast.show();
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

        chatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.write(start_write.getBytes());
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
        if(chatText.getText().toString()!=null && !chatText.getText().toString().isEmpty()
                && !chatText.getText().toString().equals("")) {
            chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString(), new java.util.Date()));
            client.write(chatText.getText().toString().getBytes());
        }
        wwm.saveMessage(device.getAddress().toString(), chatText.getText().toString(), new Date(System.currentTimeMillis()), true);
        chatText.setText("");
        return true;
    }
	
	 public void setTextFromResultOfQR(String result){
        this.resultFromQR = result;
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_in_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.qrCode:
                Intent qrCodeintent = new Intent(this, BarcodeReader.class);
                startActivity(qrCodeintent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
