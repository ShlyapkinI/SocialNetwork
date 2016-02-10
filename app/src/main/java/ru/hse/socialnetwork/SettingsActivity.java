package ru.hse.socialnetwork;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth;
    EditText  nameEdit;
    public static final int LABEL = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Подключаем Блютуз Адаптер и получаем стандартные настройки
        bluetooth = BluetoothAdapter.getDefaultAdapter();


        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(R.drawable.bluetooth_ic);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView photoImage = (ImageView)findViewById(R.id.photoImage);
        nameEdit   = (EditText)findViewById(R.id.nameEdit);

        photoImage.setImageResource(R.drawable.andr);
        // Выводим текущее имя устройства, удаляя метку приложения
        nameEdit.setText(bluetooth.getName().substring(LABEL));
    }

    // Клик по кнопке "Изменить имя"
    public void changeName(View view) {
        // Устанавливаем новое имя (добавляя метку приложения)
        bluetooth.setName("@" + nameEdit.getText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
