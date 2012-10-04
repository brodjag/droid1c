package com.droid_c_demo_;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.droid_c_demo_.db.DatabaseHelper;
import com.droid_c_demo_.lib.soap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 18.09.12
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class splashActivity extends Activity {
    Activity con;
    String login;
    String password;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.splash);
        DatabaseHelper dh=new DatabaseHelper(con);
        url=dh.getURL();
        login=dh.getSetting("login");
        password=dh.getSetting("password");
        new spinTask().execute(null);
    }
    String url;

    private class spinTask extends AsyncTask<Void,Void,Element> {
        @Override
        protected Element doInBackground(Void... voids) {
            String envelope="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:dro=\"http://Droid-C.ru\">\n" +
                    "   <soap:Header/>\n" +
                    "   <soap:Body>\n" +
                    "      <dro:GetStorages/>\n" +
                    "   </soap:Body>\n" +
                    "</soap:Envelope>";


            soap sp=new soap(login,password);
            //"http://fpat.ru/DemoEnterprise/ws/1csoap.1cws"
            Element body= sp.call(url,"http://Droid-C.ru#WebStorageService:GetStorages",envelope)  ;

            return body;  //To change body of implemented methods use File | Settings | File Templates.
        }
       // public ProgressDialog waitDialog=null;
        protected void onPreExecute(){
         //   waitDialog= ProgressDialog.show(con, "", "Loading. Please wait...", true);
        }

        protected void onPostExecute(Element body){
          //  waitDialog.dismiss();
            if (body!=null){

                DatabaseHelper dh=new DatabaseHelper(con);
                dh.removeAllStores();




                Element Return=(Element) ((Element) body.getElementsByTagName("m:GetStoragesResponse").item(0)).getElementsByTagName("m:return").item(0);
                NodeList storages=Return.getElementsByTagName("m:Storage");

                for(int i=0; i<storages.getLength();i++){
                    String name=   ((Element) storages.item(i)).getElementsByTagName("m:Name").item(0).getFirstChild().getNodeValue().toString();
                    String kod=   ((Element) storages.item(i)).getElementsByTagName("m:Kod").item(0).getFirstChild().getNodeValue().toString();
                    dh.insertToStore(name, kod);

                    // Log.d("name1", name + "))") ;

                };
                con.startActivity(new Intent(con,select_store.class));
                con.finish();
               // makeSpinnerData();
//                dh.getAllStores();
            }else {
                showDailog();
                Toast.makeText(con, "chek net (activity)", Toast.LENGTH_SHORT).show();}
        }
    }


    void showDailog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Ошибка доступа к серверу. ")
                .setCancelable(false)
                .setTitle("Droid-C")
                .setPositiveButton("Настройки", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        con.startActivity(new Intent(con, settingActivity.class));
                        con.finish();
                        //  Toast.makeText(con,"показать настройки",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        con.finish();
                    }
                })
                .setNeutralButton("Рестарт", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        con.startActivity(new Intent(con, splashActivity.class));
                        con.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}