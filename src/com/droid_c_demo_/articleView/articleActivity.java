package com.droid_c_demo_.articleView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.droid_c_demo_.R;
import com.droid_c_demo_.db.DatabaseHelper;
import com.droid_c_demo_.lib.fileLib;
import com.droid_c_demo_.lib.saveFile;
import com.droid_c_demo_.scanedList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 13.09.12
 * Time: 20:57
 * To change this template use File | Settings | File Templates.
 */
public class articleActivity extends Activity {
    Activity con;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.article);
       setValues();
    }
                                            
    private void setValues() {
        saveFile sf= new saveFile(this);
        
        Element el= sf.readXmlFile("product");
      //  Element envelope=(Element) (el.getElementsByTagName("soap:Envelope").item(0));
        Element body=(Element) (el).getElementsByTagName("soap:Body").item(0);
        Element Return=(Element) ((Element) body.getElementsByTagName("m:FindProductResponse").item(0)).getElementsByTagName("m:return").item(0);

        NodeList bcodeLt= Return.getElementsByTagName("m:Barcode");
        Toast.makeText(this, bcodeLt.item(0).getFirstChild().getNodeValue(), Toast.LENGTH_SHORT).show();
       //bara code

        try{ brCode=Return.getElementsByTagName("m:Barcode").item(0).getFirstChild().getNodeValue(); }catch (Exception e){}
        ((TextView) findViewById(R.id.articel_bcode)).setText(brCode);
        //code 1c
        try{ code_1C=Return.getElementsByTagName("m:Kod").item(0).getFirstChild().getNodeValue(); }catch (Exception e){}
        ((TextView) findViewById(R.id.articel_1c_code)).setText(code_1C);
        //name
       try{ name=Return.getElementsByTagName("m:Name").item(0).getFirstChild().getNodeValue();}catch (Exception e){}
        ((TextView) findViewById(R.id.articel_name)).setText(name);

        //article id
        try{ article= Return.getElementsByTagName("m:Article").item(0).getFirstChild().getNodeValue(); }catch (Exception e){}
        ((TextView) findViewById(R.id.articel_art_id)).setText(article);
        //name full
        try{ fullName=Return.getElementsByTagName("m:FullName").item(0).getFirstChild().getNodeValue(); }catch (Exception e){}
        ((TextView) findViewById(R.id.articel_name_full)).setText(fullName);

        //unit
       try{ unit=Return.getElementsByTagName("m:Unit").item(0).getFirstChild().getNodeValue(); }catch (Exception e){}
        ((TextView) findViewById(R.id.articel_unit)).setText(unit);

        findViewById(R.id.article_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextView();
            }
        });
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
private void nextView(){
    try { ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50);} catch (Exception e) {} ;
    int count;
    try{
        count=Integer.parseInt(((EditText) con.findViewById(R.id.article_v_count)).getText().toString());
    }catch (Exception e){
        count=0;
    }

    fileLib fl=new fileLib(con);
    String id_store= fl.read("currentCode").split(";")[2];
    DatabaseHelper dh=new DatabaseHelper(con);
    double r= dh.insertToScaned(brCode,code_1C,name,article,fullName,count,id_store,unit);
    if(r>0){Toast.makeText(con,"добавлено",Toast.LENGTH_SHORT).show();}else {
        Toast.makeText(con,"ошибка добавления",Toast.LENGTH_SHORT).show();
    }
    /*
    fileLib fl=new fileLib(con);
    String scanList=fl.read("scanList");
    if(scanList==null){scanList="";};
    */
    //fl.write("scanList",scanList+brCode+","+name+","+"5"+";");
    con.startActivity(new Intent(con, scanedList.class));
    con.finish();
}

     String brCode="";
     String code_1C="";
     String name="";
     String article="";
     String fullName="";
    String unit="";

}