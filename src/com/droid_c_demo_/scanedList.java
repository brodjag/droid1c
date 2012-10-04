package com.droid_c_demo_;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.droid_c_demo_.db.DatabaseHelper;
import com.droid_c_demo_.lib.fileLib;

public class scanedList extends Activity
{

Activity con;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.droid_c_demo_.R.layout.scan_list);
        con=this;
        
     ( findViewById(R.id.scan_bn)).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {} ;
         //   new articleRequest(con,"");
             startActivity(new Intent(con, ScanActivity.class));
             finish();
         }
     });

     findViewById(R.id.search_articul_bn).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {} ;
             Toast.makeText(con, "Поиск по артиклу в разработке", Toast.LENGTH_SHORT).show();
         }
     });



//        makeSpinnerData();

        setStore();     // init    id_store
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
        Intent setting= new Intent(con,settingActivity.class);
        setting.putExtra("back",settingActivity.backToScanList);

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
   /*
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
    */
    
private void mkList(){
    final DatabaseHelper DBhelper=new DatabaseHelper(con);
    LinearLayout list=(LinearLayout) findViewById(R.id.list_skd);
    
    DatabaseHelper dh=new DatabaseHelper(this);
  //  dh.insertToScaned("2000018987155","123","ботинки", "b-777","Ботинки адидас",1);
     Cursor c=dh.getAllScaned(id_store);


    for(int i=0; i<c.getCount();i++){
        c.moveToPosition(i);
        Log.d("scaned", c.getString(1));
       final String id=c.getString(0);
        
       final View v= getLayoutInflater().inflate(R.layout.scan_list_item, null);
        ((TextView) v.findViewById(R.id.scan_list_id)).setText(""+(i+1)+". ");

        ((TextView) v.findViewById(R.id.scan_list_baracode)).setText(c.getString(1));
        final String name=c.getString(3);
        ((TextView) v.findViewById(R.id.scan_list_name)).setText(name);

        ((TextView) v.findViewById(R.id.scan_list_count)).setText(c.getString(6)+" ед.");

       //remove item
       v.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {} ;
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




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this,select_store.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    };


    String id_store;
    private void setStore(){
        fileLib fl=new fileLib(con);
        String storeName= fl.read("currentCode").split(";")[1];
        ((TextView) findViewById(R.id.store_name)).setText("Склад: "+storeName);
        id_store=fl.read("currentCode").split(";")[2];

    }


}




