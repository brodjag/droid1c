package com.droid_c_demo.lib;


import android.content.Context;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: 1
 * Date: 17.02.11
 * Time: 21:54
 * class for read and write files
 */
public class fileLib {

    private Context context;

    public fileLib(Context context) {
        this.context = context;
    }

    //read file
    public String read(String fileName) {
        String q = "";
        String buf;
        try {
            FileInputStream ff = context.openFileInput(fileName);
            InputStreamReader inputreader = new InputStreamReader(ff);
            BufferedReader buffreader = new BufferedReader(inputreader, 8);

            while ((buf = buffreader.readLine()) != null) {
                // if (q.equals("")){q = q+buf;}else {q ="\n"+ q+buf;}
                q = q + buf;
            }

            ff.close();
        } catch (Exception e) {
            //q = null;
            return null;
        }
        if (q.equals("")) {
            q = null;
        }
        return q;
    }

    //write file
    public void write(String fileName, String data) {
        try {
            FileOutputStream ff = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ff.write(data.getBytes());
            ff.close();
        } catch (Exception e) {
        }
    }









    private Handler mHandler = new Handler();
    //loading fade..
   // private ProgressDialog dialog;

    public void waitShow() {
        try {
            mHandler.post(new Runnable() {
                public void run() {
                  //  dialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
                }
            });

        } catch (Exception e) {
        }


    }

    public void waitHide() {
        try {
            mHandler.post(new Runnable() {
                public void run() {
                   // dialog.dismiss();
                }
            });
        } catch (Exception e) {
        }

    }



}

