package ru.hse.socialnetwork;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by пользователь on 08.01.2016.
 */
public class WorkWithMessages implements Serializable{

    File directoryOfApp;

    public WorkWithMessages(Context context){
        directoryOfApp = context.getDir("SocialNetwork", Context.MODE_PRIVATE); //Creating an internal dir;
        if(!directoryOfApp.exists())
        {
            directoryOfApp.mkdirs();
        }
    }

    public void saveMessage(String macAdress, String textOfMessage,String dateAndTime, boolean flag){
//        если flag=true, это значит этот девайс написал сообщение собеседнику
        ArrayList<Message> arr = new ArrayList<>();
        Message s = new Message(macAdress,textOfMessage,dateAndTime, flag);
        File[] listFiles = directoryOfApp.listFiles();
        boolean f=false;
        for(int i=0;i<listFiles.length;i++) {
            if (listFiles[i].getName().equals(macAdress)) {
                f = true;
                break;
            }
        }
        File d= new File(directoryOfApp.getPath(),macAdress);
        if(f){
            arr=readFromFile(d);
        }
        arr.add(s);
        try{
            FileOutputStream fos = new FileOutputStream(d);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(arr);
            oos.flush();
            fos.close();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Message> loadMessages(String macAdress){
        ArrayList<Message> arrayList = new ArrayList<>();
        File d= new File(directoryOfApp.getPath(),macAdress);
        arrayList=readFromFile(d);
        return arrayList;
    }

    private ArrayList<Message> readFromFile(File f) {
        ArrayList<Message> arrlist = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream oin = new ObjectInputStream(fis);
            arrlist = (ArrayList<Message>) oin.readObject();
            oin.close();
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return arrlist;
    }
}
