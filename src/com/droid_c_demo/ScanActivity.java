/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.droid_c_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.droid_c_demo.articleView.articleRequest;
import com.droid_c_demo.db.DatabaseHelper;
import com.droid_c_demo.lib.CameraPreview;
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
            //flash on
            DatabaseHelper dh=new DatabaseHelper(con);
            String flashSwitcher= dh.getSetting("flash");
            if(flashSwitcher.equals("вкл.")){
            Camera.Parameters p = mCamera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(p);
            }
            //end flash on
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
                    //stop flash
                    DatabaseHelper dh=new DatabaseHelper(con);
                    String flashSwitcher= dh.getSetting("flash");
                    if(flashSwitcher.equals("вкл.")){
                        Camera.Parameters p = camera.getParameters();
                         p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        try{ mCamera.setParameters(p);}catch (Exception e){}
                    }
                    //end stop flash
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
        /*
        int nechet=0;
        for (int i=0;i<barecode.length()-2; i=i+2){
            Log.d("summ=","nechet="+barecode.charAt(i));
            nechet=nechet+Integer.parseInt(""+barecode.charAt(i));
        }
        int chet=0;
        for (int i=1;i<barecode.length(); i=i+2){
            Log.d("summ=","chet="+barecode.charAt(i));
            chet=chet+Integer.parseInt(""+barecode.charAt(i));
        }

        int summ=nechet+chet*3;
       // Log.d("summ=",""+summ);
        int chekSumm=(int) Math.ceil((double) summ/10)*10-summ;
        Log.d("summ=","chekSumm="+chekSumm);
        Log.d("summ=","chekSummReaded="+barecode.charAt(barecode.length()-1));
        */
        try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(100);} catch (Exception e) {} ;
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

void cameraOn(){
 Camera   camera = Camera.open();
    Camera.Parameters p = camera.getParameters();
    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
    camera.setParameters(p);
    camera.startPreview();

}
    void cameraOff(){
        Camera  camera = Camera.open();
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        camera.stopPreview();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scaner, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent setting= new Intent(con,settingActivity.class);
        setting.putExtra("back",settingActivity.backToScan);

        switch (item.getItemId()) {
            case R.id.setting:
                con.startActivity(setting);
                con.finish();
                break;
            case R.id.search_hand: showDailog();
                break;
            // case R.id.info: Toast.makeText(this, "You pressed the icon and info!", Toast.LENGTH_LONG).show();
            //    break;
        }
        return true;
    }

    /**
     * Create and return an example alert dialog with an edit text box.
     */

        void showDailog(){

            final View v=con.getLayoutInflater().inflate(R.layout.dialog_enter_barecode,null) ;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Введите штрихкод в ручную ")
                    .setCancelable(true)
                    .setTitle("руччной ввод")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                              String bare= ((EditText) v.findViewById(R.id.bcode_entered)).getText().toString();
                              ///Toast.makeText(con,bare,Toast.LENGTH_SHORT).show();
                            new articleRequest(con,bare) ;
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        }
                    })
                    .setView(v);
            AlertDialog alert = builder.create();
            alert.show();

        }




}

