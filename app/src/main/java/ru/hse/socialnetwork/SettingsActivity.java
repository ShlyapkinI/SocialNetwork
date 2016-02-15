package ru.hse.socialnetwork;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity  implements View.OnClickListener {

    BluetoothAdapter bluetooth;
    EditText  nameEdit;
    public static final int LABEL = 1;
    static int themeid;
    public static int count = 0;
    String mCurrentPhotoPath;
    Uri contentUri;
    private Uri fileUri;
    Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setTheme(themeid);
        this.setTheme(themeid);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        findViewById(R.id.photoImage).setOnClickListener(this);

        // Подключаем Блютуз Адаптер и получаем стандартные настройки
        bluetooth = BluetoothAdapter.getDefaultAdapter();


        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(R.drawable.bluetooth_ic);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        ImageView photoImage = (ImageView)findViewById(R.id.photoImage);
        nameEdit   = (EditText)findViewById(R.id.nameEdit);

        SharedPreferences settings = getSharedPreferences("MyUri", 0);
        String uri = settings.getString("Uri", "myUri");
        Uri myUri = Uri.parse(uri);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), myUri);
            // Log.d(TAG, String.valueOf(bitmap));

            photoImage = (ImageView) findViewById(R.id.photoImage);
            photoImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //photoImage.setImageResource(R.drawable.ivan);
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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 0);
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //intent.setType("image/*");
                    //intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, 1);
                    //startActivityForResult(Intent.createChooser(intent,
                     //       "Select Picture"), 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ImageView photoImage = (ImageView) findViewById(R.id.photoImage);
                    photoImage.setImageBitmap(imageBitmap);
                    String camerauri = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "camera.jpg", null);
                    SharedPreferences settings = getSharedPreferences("MyUri", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Uri", String.valueOf(camerauri));
                    editor.commit();

                }
                break;
            case 1:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    SharedPreferences settings = getSharedPreferences("MyUri", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("Uri", String.valueOf(uri));
                    editor.commit();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        ImageView photoImage = (ImageView) findViewById(R.id.photoImage);
                        photoImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.imageView:
                Log.d("как так ", " что за");
                break;
            case R.id.imageView2:
                break;
            case R.id.imageView3:
                break;
            case R.id.photoImage:
                selectImage();
                /*
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
*/
                break;
        }
    }
}
