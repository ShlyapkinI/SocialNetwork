package ru.hse.socialnetwork;

/**
 * Created by Ilya on 12.12.2015.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AcceptThread extends Thread{
    private static final String TAG = "AcceptThread";
    private final BluetoothServerSocket mmServerSocket;
    BluetoothAdapter mBluetoothAdapter;
    private final String NAME = "BT_SERVER";
    private final String MY_UUID = "a60f35f0-b93a-11de-8a39-08002009c666";
    Handler h;

    private  InputStream mmInStream;
    private  OutputStream mmOutStream;

    public AcceptThread(Handler h){
        this.h = h;
// используем вспомогательную переменную, которую в дальнейшем
// свяжем с mmServerSocket,
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp=null;

        try{
// MY_UUID это UUID нашего приложения, это же значение
// используется в клиентском приложении
            tmp= mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, UUID.fromString(MY_UUID));
        } catch(IOException e){}
        mmServerSocket= tmp;
    }

    public void run(){
        BluetoothSocket socket=null;
// ждем пока не произойдет ошибка или не
// будет возвращен сокет
        while(true){
            try{
                Log.d(TAG,"mmServerSocket.accept();");
                socket= mmServerSocket.accept();
            } catch(IOException e){
                e.printStackTrace();
                break;
            }
// если соединение было подтверждено
            if(socket!=null){
                Log.d(TAG,"socket!=null");
// управлчем соединением (в отдельном потоке)
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

//                try {
//                    Log.d(TAG,"mmServerSocket.close();");
//                    mmServerSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                read(socket.getRemoteDevice());

            }
        }
    }

    /* Вызываем этот метод из главной деятельности, чтобы отправить данные
    удаленному устройству */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            Log.d(TAG, "write: "+ new String(bytes));
        } catch (IOException e) {
        }
    }

    public void read(BluetoothDevice device) {
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
                Log.d(TAG+" read: ", value);

                Message msg = h.obtainMessage();
                Bundle bundle = new Bundle();

                bundle.putParcelable("Device",device);
                bundle.putString("Read",value);
                msg.setData(bundle);
                h.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Log.d(TAG, "break;");
        }
    }

    /** отмена ожидания сокета */
    public void cancel(){
        try{
            Log.d(TAG,"mmServerSocket.close();");
            mmServerSocket.close();
        } catch(IOException e){}
    }
}
