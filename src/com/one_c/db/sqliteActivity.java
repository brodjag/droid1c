package com.one_c.db;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 17.09.12
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
public class sqliteActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"db!",Toast.LENGTH_SHORT).show();
       DatabaseHelper dh=new DatabaseHelper(this);
       dh.setSetting("qq","b-qq");

        Log.d("setting",   dh.getSetting("qqq")) ;
        
    }
}