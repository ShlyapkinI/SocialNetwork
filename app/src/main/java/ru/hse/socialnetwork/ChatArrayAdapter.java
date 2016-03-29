package ru.hse.socialnetwork;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatArrayAdapter extends ArrayAdapter {

    private TextView chatText;
    private List chatMessageList = new ArrayList();
    private LinearLayout singleMessageContainer;
    private ImageView chatImage;

    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return (ChatMessage) this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.singlemessage, parent, false);
        }
        chatImage = (ImageView) row.findViewById(R.id.imageView16);
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);

        if (chatMessageObj.image != null){
            chatImage.setVisibility(View.VISIBLE);
            chatImage.setImageBitmap(chatMessageObj.image);
        }else{
            chatImage.setVisibility(View.GONE);
        }
        if (chatMessageObj.message != null){
            chatText.setVisibility(View.VISIBLE);
            chatText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int index = ((TextView) v).getText().toString().lastIndexOf("\n");
                    String result = ((TextView) v).getText().toString().substring(0, index - 1);
                    ((ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE))
                            .setText(result);

                    Toast toast = Toast.makeText(getContext(), "Message copied: \n " + result, Toast.LENGTH_SHORT);
                    toast.show();
                    //int index = ((TextView) v).getText().toString().lastIndexOf("\n");

                    return false;
                }
            });

            Date today = new Date();
            if((today.getDay() == chatMessageObj.date.getDay()) && (today.getMonth() == chatMessageObj.date.getMonth()) && (today.getYear() == chatMessageObj.date.getYear()))
            {
                long sec = chatMessageObj.date.getTime() / 1000;
                long sec1 = today.getTime() / 1000;
                long result = sec1 - sec;
                if (result < 60)
                {
                    chatText.setText(chatMessageObj.message + "\n\n" + "just now");
                }else{
                    if (result < 120){
                        chatText.setText(chatMessageObj.message + "\n\n" + "minute ago");
                    }else {
                        if ( result < 3600) {
                            chatText.setText(chatMessageObj.message + "\n\n" + result / 60 + " minutes ago");
                        } else {
                            chatText.setText(chatMessageObj.message + "\n\n" + new SimpleDateFormat("HH:mm").format(chatMessageObj.date));
                        }
                    }
                }

            } else {
                chatText.setText(chatMessageObj.message + "\n\n" + new SimpleDateFormat("dd.MM").format(chatMessageObj.date));
            }
        }else{
            chatText.setVisibility(View.GONE);
        }


        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);
        chatText.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public void getDateMassege(Date date) {
        long sec = date.getTime() / 1000;



    }

//    public String getNormDate(Date date){b
//
//    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
