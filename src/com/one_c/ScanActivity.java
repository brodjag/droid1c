/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.one_c;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.one_c.articleView.articleRequest;
import com.one_c.lib.CameraPreview;
import net.sourceforge.zbar.*;

/* Import ZBar Class files */

public class ScanActivity extends Activity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private ScanActivity con;

    TextView scanText;
    Button scanButton;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        releaseCamera();

        //
        //INITIALIZE RECEIVER
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);


         /// Toast.makeText(this,"",Toast.LENGTH_SHORT).show();
        //
        setContentView(R.layout.scan);


      //  headButtons(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
       // Log.d("yyyy","y="+Config.Y_DENSITY);
       // Log.d("yyyy","x="+Config.X_DENSITY);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        scanText = (TextView)findViewById(R.id.scanText);

        scanButton = (Button)findViewById(R.id.ScanButton);

        scanButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    try {  ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {   }
                    scanBn();
                }
            });
       // Toast.makeText(this,"created",Toast.LENGTH_LONG).show();
    }

    public void scanBn(){
        if (barcodeScanned) {
            barcodeScanned = false;
            scanText.setText("Scanning...");
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
    }

    @Override
    public void onPause() {

        releaseCamera();
        super.onPause();

        //finish();
    }
    
    void p(){super.onPause();}

    public void onResume(){
     super.onResume();
      //  if (!started){started=true; return;}
       //    if(started){restart();}
    }









    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {  c = Camera.open(); } catch (Exception e){ }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

    PreviewCallback previewCb = new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                int result = scanner.scanImage(barcode);
                
                if (result != 0) {
                    previewing = false;
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    
                    SymbolSet syms = scanner.getResults();
                    for (Symbol sym : syms) {
                        scanText.setText("barcode result " + sym.getData());


                        if (!barcodeScanned){onScaned(sym.getData().toString()); }
                        barcodeScanned = true;
                       /*
                        artikelRequest ar = new artikelRequest(con,sym.getData()){
                            @Override
                            public void noInet(){

                            };

                        };
                        ar.setCallback(new backError() {
                            @Override
                            public void callback() {
                                scanBn();
                            }
                        });  */
                    }
                }
            }
        };

    private void onScaned(String barecode) {


        new articleRequest(con,barecode) ;

    }


    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };


    public  class ScreenReceiver extends BroadcastReceiver {

                // THANKS JASON
        public  boolean wasScreenOn = true;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // DO WHATEVER YOU NEED TO DO HERE
                wasScreenOn = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                // AND DO WHATEVER YOU NEED TO DO HERE
                restart();
               // Toast.makeText(getBaseContext(),"unlocked",Toast.LENGTH_LONG).show();
                wasScreenOn = true;
            }
            //else if (intent.getAction().equals(Intent.FLAG_ACTIVITY_SINGLE_TOP)){Toast.makeText(con,"reboot",Toast.LENGTH_SHORT).show();}


        }


    }

    private void restart(){
        startActivity(new Intent(getBaseContext(),ScanActivity.class));
        finish();
    }



    private boolean started=false;
    @Override
public void onWindowFocusChanged(boolean b){
        super.onWindowFocusChanged(b);
        if (b&&!started){started=true; return;}
        if(b&&started){restart();}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(this,scanedList.class));

        }
        return super.onKeyDown(keyCode, event);
    };
}
