package com.one_c;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.one_c.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class scanedList extends Activity
{

Activity con;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.one_c.R.layout.scan_list);
        con=this;
        
     ( findViewById(R.id.scan_bn)).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         //   new articleRequest(con,"");
             startActivity(new Intent(con, ScanActivity.class));
             finish();
         }
     });



        makeSpinnerData();
        mkList();
       // new articleRequest(con,"2000002026013") ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:     Toast.makeText(this, "You pressed the setting!", Toast.LENGTH_LONG).show();
                break;
            case R.id.export:     Toast.makeText(this, "You pressed the expor!", Toast.LENGTH_LONG).show();
                break;
            case R.id.info: Toast.makeText(this, "You pressed the icon and info!", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    public void   makeSpinnerData(){
    List<String> listStore = new ArrayList<String>();
    DatabaseHelper dh=new DatabaseHelper(con);
    Cursor cur= dh.getAllStores();
    //cur.moveToFirst();
    for(int i=0;i<cur.getCount(); i++) {
        cur.moveToPosition(i);
        listStore.add(cur.getString(1));
       // Log.d("curs",cur.getString(0)+" "+cur.getString(1)+" "+cur.getString(2));
    }

    Spinner stores_pinner = (Spinner) findViewById(R.id.stores_pinner);
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(con,  android.R.layout.simple_spinner_item, listStore);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    stores_pinner.setAdapter(dataAdapter);
    cur.close();

}
    
private void mkList(){
    final DatabaseHelper DBhelper=new DatabaseHelper(con);
    LinearLayout list=(LinearLayout) findViewById(R.id.list_skd);
    
    DatabaseHelper dh=new DatabaseHelper(this);
  //  dh.insertToScaned("2000018987155","123","ботинки", "b-777","Ботинки адидас",1);
     Cursor c=dh.getAllScaned();


    for(int i=0; i<c.getCount();i++){
        c.moveToPosition(i);
        Log.d("scaned", c.getString(1));
       final String id=c.getString(0);
        
       final View v= getLayoutInflater().inflate(R.layout.scan_list_item, null);
        ((TextView) v.findViewById(R.id.scan_list_baracode)).setText(c.getString(1));
        final String name=c.getString(3);
        ((TextView) v.findViewById(R.id.scan_list_name)).setText(name);

        ((TextView) v.findViewById(R.id.scan_list_count)).setText(c.getString(7)+"шт.");

       //remove item
       v.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try {
                   ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);
               } catch (Exception e) {} ;
             //  Toast.makeText(con, "удалить ))" + id, Toast.LENGTH_SHORT).show();

               final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(con);
               dlgAlert.setMessage("Удалить '"+name+"'?");
                     //dlgAlert.setTitle("App Title");
                    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                            DBhelper.removeScanedId(id);
                            ((ViewManager) v.getParent()).removeView(v);
                          }
                     });
               dlgAlert.setNegativeButton("Отмена",new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.dismiss();
                   }
               });
               dlgAlert.setCancelable(true);
               dlgAlert.create().show();


           }
       });
       //item click
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {   };
              //  Toast.makeText(con,"клик ))"+id,Toast.LENGTH_SHORT).show();
                Intent i=new Intent(con,articleEdit.class);
                i.putExtra("id",""+id);
                con.startActivity(i);
                con.finish();
            }
        });

       list.addView(v);
    }
    c.close();
}  


}
