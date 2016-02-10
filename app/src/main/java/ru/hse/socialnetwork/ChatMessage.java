package ru.hse.socialnetwork;


import java.util.Date;

public class ChatMessage {
    public boolean left;
    public String message;
    public Date date;

    public ChatMessage(boolean left, String message, Date date) {
        super();
        this.left = left;
        this.message = message;
        this.date = date;
    }
}
