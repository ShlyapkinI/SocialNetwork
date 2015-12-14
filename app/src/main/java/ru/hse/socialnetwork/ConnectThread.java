package ru.hse.socialnetwork;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Ilya on 12.12.2015.
 */
public class ConnectThread extends Thread{
    private static final String TAG = "ConnectThread";
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    BluetoothAdapter mBluetoothAdapter;
    private final String MY_UUID = "a60f35f0-b93a-11de-8a39-08002009c666";
    private  InputStream mmInStream;
    private  OutputStream mmOutStream;
    Handler h;

    public ConnectThread(BluetoothDevice device, Handler h){
// используем вспомогательную переменную, которую в дальнейшем
// свяжем с mmSocket,
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmp=null;
        mmDevice= device;
        this.h = h;


// получаем BluetoothSocket чтобы соединиться с BluetoothDevice
        try{
// MY_UUID это UUID, который используется и в сервере
            tmp= device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch(IOException e){}
        mmSocket= tmp;
    }

    public void run(){
// Отменяем сканирование, поскольку оно тормозит соединение
        mBluetoothAdapter.cancelDiscovery();

        try{
// Соединяемся с устройством через сокет.
// Метод блокирует выполнение программы до
// установки соединения или возникновения ошибки
            Log.d(TAG, "mmSocket.connect();");
            mmSocket.connect();
        } catch(IOException connectException){
            connectException.printStackTrace();
// Невозможно соединиться. Закрываем сокет и выходим.
            try{
                Log.d(TAG, "mmSocket.close();");
                mmSocket.close();
            } catch(IOException closeException){}

            Message msg = h.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("ToServer", "ToServer");
            msg.setData(bundle);
            h.sendMessage(msg);
            return;
        }

        Log.d(TAG, "needmanage;");

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

// Получить входящий и исходящий потоки данных
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
// управлчем соединением (в отдельном потоке)
        //manageConnectedSocket(mmSocket);
        Message msg = h.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("Start", "Start");
        msg.setData(bundle);
        h.sendMessage(msg);

        read();

        Message msg1 = h.obtainMessage();
        Bundle bundle1 = new Bundle();
        bundle1.putString("Stop", "Stop");
        msg1.setData(bundle);
        h.sendMessage(msg);
    }

    /* Вызываем этот метод из главной деятельности, чтобы отправить данные
    удаленному устройству */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            Log.d(TAG, "write: " + new String(bytes));
        } catch (IOException e) {
        }
    }

    public void read() {
        byte[] buffer = new byte[1024];// буферный массив
        int bytes;// bytes returned from read()

// Прослушиваем InputStream пока не произойдет исключение
        while (true) {
            try {
// читаем из InputStream
                bytes = mmInStream.read(buffer);

                byte[] b = new byte[bytes];
                for(int i = 0; i<bytes; i++){
                    b[i]=buffer[i];
                }
                String value = new String(b);
                Log.d(TAG+" get - ", value);

                Message msg = h.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("Key", value);
                msg.setData(bundle);
                h.sendMessage(msg);
// посылаем прочитанные байты главной деятельности
                //mHandler.obtainMessage(MESSAGE_READ, bytes,-1, buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        Log.d(TAG, "break;");
    }

    /** отмена ожидания сокета */
    public void cancel(){
        try{
            mmSocket.close();
        } catch(IOException e){}
    }
}
