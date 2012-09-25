package com.one_c;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.one_c.db.DatabaseHelper;
import com.one_c.lib.soap;
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
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.splash);
        new spinTask().execute(null);
    }


    private class spinTask extends AsyncTask<Void,Void,Element> {
        @Override
        protected Element doInBackground(Void... voids) {
            String envelope="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:fpat=\"http://fpat.ru\">\n" +
                    "   <soap:Header/>\n" +
                    "   <soap:Body>\n" +
                    "      <fpat:GetStorages/>\n" +
                    "   </soap:Body>\n" +
                    "</soap:Envelope>";


            soap sp=new soap();
            Element body= sp.call("http://fpat.ru/DemoEnterprise/ws/1csoap.1cws","http://fpat.ru#WebStorageService:GetStorages",envelope)  ;

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
                Toast.makeText(con, "chek net (activity)", Toast.LENGTH_SHORT).show();}
        }
    }

}