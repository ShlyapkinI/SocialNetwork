package ru.hse.socialnetwork;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /** Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getSupportActionBar().setIcon(R.drawable.bluetooth_ic);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState); // state will be bundle your activity state which you get in onCreate

        TabHost tabHost     = (TabHost) findViewById(android.R.id.tabhost);
        // инициализация
        tabHost.setup(mLocalActivityManager);

        TabHost.TabSpec tabSpec;


        tabSpec = tabHost.newTabSpec("tag1");       // создаем вкладку и указываем тег
        tabSpec.setIndicator("Список устройств");   // название вкладки
        Intent deviceIntent = new Intent(this, DeviceActivity.class);
        tabSpec.setContent(deviceIntent);            // указываем id компонента из FrameLayout, он и станет содержимым
        tabHost.addTab(tabSpec);                    // добавляем в корневой элемент

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Друзья");
        Intent friendIntent = new Intent(this, FriendActivity.class);
        tabSpec.setContent(friendIntent);
        tabHost.addTab(tabSpec);


        tabHost.setCurrentTabByTag("tag1");// первая вкладка будет выбрана по умолчанию

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.white_color));
        }

        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                //Toast.makeText(getBaseContext(), "tabId = " + tabId, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.settings://Переход на опции
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.refresh:
                Intent serverintent = new Intent(getApplicationContext(), ChatActivity.class);
                serverintent.putExtra("type", "server");
                serverintent.putExtra("device", "server");
                startActivity(serverintent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
