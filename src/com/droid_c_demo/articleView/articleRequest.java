package com.droid_c_demo.articleView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.droid_c_demo.articleEdit;
import com.droid_c_demo.db.DatabaseHelper;
import com.droid_c_demo.lib.fileLib;
import com.droid_c_demo.lib.soap;
import com.droid_c_demo.uploadRequest;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 14.09.12
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class articleRequest {
    public Activity con;
    private String idBare;
    String  login;
    String password;
    String id_store;

    public articleRequest(Activity c, String brId){
        con=c;        idBare=brId;



      //  Toast.makeText(con,brId,Toast.LENGTH_SHORT).show();

        DatabaseHelper dh=new DatabaseHelper(con);

        login=dh.getSetting("login");
        password=dh.getSetting("password");
        // String barecode=sym.getData().toString();
        fileLib fl=new fileLib(con);
         id_store=fl.read("currentCode").split(";")[2];

        int id= dh.getScanedIdByBare(idBare,id_store);
        if(id!=-1){
            Intent i=new Intent(con,articleEdit.class);
            i.putExtra("id",""+id);
            con.startActivity(i);
            con.finish();
            return;
           // finish();
        } else {
           // DatabaseHelper dh=new DatabaseHelper(con);
            url=dh.getURL();
            new artTask().execute();
        }



    }
   String url;


 private class artTask extends AsyncTask<Void,Void,Element> {

    public ProgressDialog waitDialog=null;
        @Override
    protected Element doInBackground(Void... voids) {
            String envelope="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "    <soap:Body>\n" +
                    "        <FindProduct xmlns=\"http://Droid-C.ru\">\n" +
                    "            <Barcode>"+idBare+"</Barcode>\n" +
                    "            <SearchCode>1</SearchCode>\n" +
                    "            <SearchArticle>1</SearchArticle>\n" +
                    "               <StorageKod>"+id_store+"</StorageKod>"+
                    "        </FindProduct>\n" +
                    "    </soap:Body>\n" +
                    "</soap:Envelope>";


             sp=new soap(login,password);
            //"http://fpat.ru/DemoEnterprise/ws/1csoap.1cws"


            Element body= sp.call(url,"http://Droid-C.ru#WebStorageService:FindProduct",envelope);
            return body;
        }
        soap sp;

    protected void onPreExecute(){
        waitDialog= ProgressDialog.show(con, "", "Loading for"+idBare+ " Please wait...", true);
    }

    protected void onPostExecute(Element body){
        waitDialog.dismiss();
        NodeList faultList= body.getElementsByTagName("soap:Fault");

        Log.d("ssa", "" + faultList.getLength());
        if(faultList.getLength()==1){
           uploadRequest.errorDialog((Element) faultList.item(0), con);
            return;
        }

        NodeList responceList= body.getElementsByTagName("m:FindProductResponse");

        if(responceList.getLength()>0){
        Element Return=(Element) ((Element) responceList.item(0)).getElementsByTagName("m:return").item(0);
        NodeList bcodeLt= Return.getElementsByTagName("m:Barcode");
            if(bcodeLt.getLength()>0){
            fileLib fl=new fileLib(con);
            fl.write("product",sp.text);


            con.startActivity(new Intent(con,articleActivity.class));
            con.finish();
            // Toast.makeText(con,bcodeLt.item(0).getFirstChild().getNodeValue(),Toast.LENGTH_SHORT).show();
           // return true;

        } else {
            Toast.makeText(con,"продукт не найден",Toast.LENGTH_SHORT).show();
           // return false;
            // Toast.makeText(con,"продукт не найден",Toast.LENGTH_SHORT).show();
        }
        }else { Toast.makeText(con,"ошибка в ответе сервера",Toast.LENGTH_SHORT).show();}

    }
    }

}
