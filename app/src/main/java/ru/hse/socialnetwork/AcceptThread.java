package ru.hse.socialnetwork;

/**
 * Created by Ilya on 12.12.2015.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;
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
    private Context context;
    private Uri uri;

    public AcceptThread(Handler h, Context context){
        this.h = h;
        this.context = context;
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
            } catch(Exception e){
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
    public synchronized void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            Log.d(TAG, "write: "+ new String(bytes));
        } catch (IOException e) {
        }
    }

    private String saveImage(Bitmap finalBitmap) {

        File myDir=new File("/sdcard/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".png";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    public void read(BluetoothDevice device) {
        byte[] buffer = new byte[1024];// буферный массив
        int bytes;// bytes returned from read()
        byte[] imgBuffer = null;
        //int pos = 0;
        Boolean flag = false;
        Bitmap image;
        int lenght = 0;

// Прослушиваем InputStream пока не произойдет исключение
        while (true) {
            try {
// читаем из InputStream
                bytes = mmInStream.read(buffer);

                Message msg = h.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Device", device);

                byte[] b = new byte[bytes];
                for(int i = 0; i<bytes; i++){
                    b[i]=buffer[i];
                }

                String value = new String(b);
                Log.d(TAG+ " read: ", value);

                if(value.contentEquals("1234567890")) {
                    flag = true;
                    continue;
                }
                if (flag)
                {
                    if(imgBuffer==null){
                        lenght = Integer.parseInt(value);
                        imgBuffer = new byte[0];
                        continue;
                    }

                        byte[] combined = new byte[imgBuffer.length + b.length];

                        System.arraycopy(imgBuffer,0,combined,0         ,imgBuffer.length);
                        System.arraycopy(b,0,combined,imgBuffer.length, b.length);

                        imgBuffer = combined;

                        if(imgBuffer.length == lenght)
                        {
                            flag = false;
                            image = BitmapFactory.decodeByteArray(imgBuffer, 0, imgBuffer.length);
                            String uri =  saveImage(image);
                            bundle.putString("IMAGE_URI", uri);
                            msg.setData(bundle);
                            h.sendMessage(msg);
                            continue;
                        }
                        continue;

                }

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
