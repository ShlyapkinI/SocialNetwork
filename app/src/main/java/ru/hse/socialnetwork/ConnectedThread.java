package ru.hse.socialnetwork;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ilya on 12.12.2015.
 */
public class ConnectedThread extends Thread {
    private static final String TAG = "ConnectedThread";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

// Получить входящий и исходящий потоки данных
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];// буферный массив
        int bytes;// bytes returned from read()

// Прослушиваем InputStream пока не произойдет исключение
        while (true) {
            try {
// читаем из InputStream
                bytes = mmInStream.read(buffer);
                String value = new String(buffer, "UTF-8");
                Log.d("get - ", value);
// посылаем прочитанные байты главной деятельности
                //mHandler.obtainMessage(MESSAGE_READ, bytes,-1, buffer).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Вызываем этот метод из главной деятельности, чтобы отправить данные
    удаленному устройству */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Вызываем этот метод из главной деятельности,
    чтобы разорвать соединение */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}