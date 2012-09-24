package com.one_c;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.one_c.db.DatabaseHelper;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 19.09.12
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
 */

public class articleEdit extends Activity {
    Activity con;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.article);
        setValues();
    }

    String id;
    private void setValues() {

        DatabaseHelper dh=new DatabaseHelper(con);

        try{
        id= getIntent().getStringExtra("id");
        }catch (Exception e){id="";}
        Cursor c=dh.getScanedById(id);

       // saveFile sf= new saveFile(this);

        //Element el= sf.readXmlFile("product");
       // Element body=(Element) ((Element) (el.getElementsByTagName("soap:Envelope").item(0))).getElementsByTagName("soap:Body").item(0);
       // Element Return=(Element) ((Element) body.getElementsByTagName("m:FindProductResponse").item(0)).getElementsByTagName("m:return").item(0);

       // NodeList bcodeLt= Return.getElementsByTagName("m:Barcode");
       // Toast.makeText(this, bcodeLt.item(0).getFirstChild().getNodeValue(), Toast.LENGTH_SHORT).show();
        //bara code
        final String brCode=c.getString(1);
        ((TextView) findViewById(R.id.articel_bcode)).setText(brCode);
        //code 1c
        final String code_1C=c.getString(2);
        ((TextView) findViewById(R.id.articel_1c_code)).setText(code_1C);
        //name
        final String name=c.getString(3);
        ((TextView) findViewById(R.id.articel_name)).setText(name);

        //article id
        final String article= c.getString(4);
        ((TextView) findViewById(R.id.articel_art_id)).setText(article);
        //name full
        final String fullName=c.getString(5);
        ((TextView) findViewById(R.id.articel_name_full)).setText(fullName);

        ((EditText) con.findViewById(R.id.article_v_count)).setText(c.getString(7));

        findViewById(R.id.article_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextView();
            }
        });
        c.close();

        ((EditText) con.findViewById(R.id.article_v_count)).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&  (i == KeyEvent.KEYCODE_ENTER)) {
                    nextView();
                }
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });





    }

    void nextView(){
        int count;
        try{
            count=Integer.parseInt (((EditText) con.findViewById(R.id.article_v_count)).getText().toString());
        }catch (Exception e){       count=0; }
        DatabaseHelper dh=new DatabaseHelper(con);
        dh.updateScanedId(id,count);

        startActivity(new Intent(con, scanedList.class));
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this,scanedList.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    };

}
