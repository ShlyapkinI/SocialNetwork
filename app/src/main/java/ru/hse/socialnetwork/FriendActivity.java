package ru.hse.socialnetwork;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FriendActivity extends ListActivity {

    ListView lvFriend;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = new String[] { "Polly", "Ilya", "Ksenia",
                "Anton" };

        setListAdapter(new MyArrayAdapter(this, values));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String name = (String) getListAdapter().getItem(position);

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);

    }
}
