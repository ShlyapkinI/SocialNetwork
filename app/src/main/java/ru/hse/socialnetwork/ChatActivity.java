package ru.hse.socialnetwork;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private OutputStream outputStream;
    private InputStream inStream;

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;

    // ���������� �����
    private BluetoothDevice device;

    Intent intent;
    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.chat);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        // ������ ������ �� �������
        device = intent.getParcelableExtra("device");
        Log.i("ChatActivity", device.toString());

        // ��� ��� � �������
        ParcelUuid[] uuids = device.getUuids();

        try {
            // �������������� �����
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            // ���������� � ������
            socket.connect();
            // ������ ������ ����� � ������ � �����
            outputStream = socket.getOutputStream();
            inStream = socket.getInputStream();
            // ��������� ����������� ����� ������ �� ������ (� ������)
            Read();
        }catch (Exception ex){

        }

        getSupportActionBar().setIcon(R.drawable.bluetooth_ic);
        getSupportActionBar().setTitle(name);
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
    }

    // ������ � �����. ����� ���������� ������
    public void write(String s) throws IOException {
        Log.i("write(String s)", s);
        outputStream.write(s.getBytes());
    }

    // ������ �� ������
    // ������ � �������� ���� ���
    // �������� � �����
    public void Read() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final int BUFFER_SIZE = 1024;
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytes = 0;
                int b = BUFFER_SIZE;

                while (true) {
                    try {
                        bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
                        Log.i("READ: ", new String(buffer));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private boolean sendChatMessage() throws IOException {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        // ����� ������ ��� �����
        write(chatText.getText().toString());
        chatText.setText("");
        side = !side;
        return true;
    }

}
