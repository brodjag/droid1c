package com.droid_c_demo_;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.droid_c_demo_.db.DatabaseHelper;
import com.droid_c_demo_.lib.fileLib;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 24.09.12
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class select_store extends Activity {
    Activity con;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_store);
        con=this;
        makeList();
    }


    public void   makeList(){

        DatabaseHelper dh=new DatabaseHelper(con);
        Cursor cur= dh.getAllStores();

        LinearLayout list=(LinearLayout) findViewById(R.id.store_list);
        for(int i=0;i<cur.getCount(); i++) {
            cur.moveToPosition(i);
            View item=View.inflate(con,R.layout.select_store_item,null);
            final String id=cur.getString(0);
            final String name=cur.getString(1);
            final String code=cur.getString(2);
            ((TextView) item.findViewById(R.id.id_s)).setText(cur.getString(0)+".");
            ((TextView) item.findViewById(R.id.store_name)).setText(cur.getString(1));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {} ;
                    fileLib fl=new fileLib(con);
                    fl.write("currentCode", id+";"+name+";"+code);

                    con.startActivity(new Intent(con, scanedList.class));
                    con.finish();
                }
            });

            list.addView(item);
            //listStore.add(cur.getString(1));
            // Log.d("curs",cur.getString(0)+" "+cur.getString(1)+" "+cur.getString(2));
        }


        cur.close();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent setting= new Intent(con,settingActivity.class);
        setting.putExtra("back",settingActivity.backToSelectStore);

        switch (item.getItemId()) {
            case R.id.setting:
                con.startActivity(setting);
                con.finish();
                break;
            case R.id.export:     Toast.makeText(this, "Функция выгрузки в процессе разработки", Toast.LENGTH_LONG).show();
                break;
            // case R.id.info: Toast.makeText(this, "You pressed the icon and info!", Toast.LENGTH_LONG).show();
            //    break;
        }
        return true;
    }
}