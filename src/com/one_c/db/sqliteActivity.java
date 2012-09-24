package com.one_c.db;

import android.app.Activity;
import android.database.Cursor;
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

       // dh.removeAllStores();
       // dh.insertToStore("karambae", "kokod");
      //  dh.getAllStores();
        dh.insertToScaned("2000018987155","123","ботинки", "b-777","Ботинки адидас",1,1);
        Cursor c=dh.getAllScaned();
        for(int i=0; i<c.getCount();i++){
            c.moveToPosition(i);
            Log.d("scaned",c.getString(1));
        }
        
    }
}