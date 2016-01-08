package ru.hse.socialnetwork;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by пользователь on 08.01.2016.
 */
public class Message implements Serializable{
    private String macAdressOfFriend;
    private String textOfMessage;
    private String dateAndTime;
    private boolean flag;
    ArrayList<Message> arr = new ArrayList<Message>();
    public Message(String macAdressOfFriend, String textOfMessage,String dateAndTime, boolean flag){
        this.macAdressOfFriend=macAdressOfFriend;
        this.textOfMessage=textOfMessage;
        this.dateAndTime=dateAndTime;
        this.flag=flag;
    }

    public String getMacAdressOfFriend(){
        return macAdressOfFriend;
    }

    public String getTextOfMessage(){
        return textOfMessage;
    }

    public String getDateAndTime(){
        return dateAndTime;
    }
    public boolean getFlag(){
        return flag;
    }
}
