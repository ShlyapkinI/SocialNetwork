package ru.hse.socialnetwork;


import android.graphics.Bitmap;

import java.util.Date;

public class ChatMessage {
    public boolean left;
    public String message;
    public Date date;
    public Bitmap image;

    public ChatMessage(boolean left, String message, Date date) {
        super();
        this.left = left;
        this.message = message;
        this.date = date;
    }
    public ChatMessage(boolean left,Bitmap image) {
        super();
        this.left = left;
        this.image = image;
    }
}
