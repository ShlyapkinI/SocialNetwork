package ru.hse.socialnetwork.qr;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import ru.hse.socialnetwork.R;


public class BarcodeReader extends Activity {
    private CameraPreview mPreview;
    private CameraManager mCameraManager;
    private HoverView mHoverView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qr);
		
		Display display = getWindowManager().getDefaultDisplay();
		mHoverView = (HoverView)findViewById(R.id.hover_view);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		mHoverView.update(metrics.widthPixels, metrics.heightPixels);

		Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Bundle bundle = msg.getData();
				String data = bundle.getString("stop_camera");

				if(data.contentEquals("stop")){
					finish();
				}
			}
		};

		
		mCameraManager = new CameraManager(this);
        mPreview = new CameraPreview(this, mCameraManager.getCamera(), handler);
        mPreview.setArea(mHoverView.getHoverLeft(), mHoverView.getHoverTop(), mHoverView.getHoverAreaWidth(), display.getWidth());
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
//        getActionBar().hide();
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        //mPreview.onPause();
        mCameraManager.onPause(); 
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCameraManager.onResume();
		mPreview.setCamera(mCameraManager.getCamera());
	}
}
