package com.one_c.articleView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.one_c.articleEdit;
import com.one_c.db.DatabaseHelper;
import com.one_c.lib.fileLib;
import com.one_c.lib.soap;
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
    
    public articleRequest(Activity c, String brId){
        con=c;        idBare=brId;
      //  Toast.makeText(con,brId,Toast.LENGTH_SHORT).show();

        DatabaseHelper dh=new DatabaseHelper(con);
        // String barecode=sym.getData().toString();
        fileLib fl=new fileLib(con);
        String id_store=fl.read("currentCode").split(";")[2];

        int id= dh.getScanedIdByBare(idBare,id_store);
        if(id!=-1){
            Intent i=new Intent(con,articleEdit.class);
            i.putExtra("id",""+id);
            con.startActivity(i);
            con.finish();
            return;
           // finish();
        } else { new artTask().execute();}



    }



 private class artTask extends AsyncTask<Void,Void,Element> {

    public ProgressDialog waitDialog=null;
        @Override
    protected Element doInBackground(Void... voids) {
            String envelope="<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "    <soap:Body>\n" +
                    "        <FindProduct xmlns=\"http://fpat.ru\">\n" +
                    "            <Barcode>"+idBare+"</Barcode>\n" +
                    "            <SearchCode>true</SearchCode>\n" +
                    "            <SearchArticle>false</SearchArticle>\n" +
                    "        </FindProduct>\n" +
                    "    </soap:Body>\n" +
                    "</soap:Envelope>";


             sp=new soap();
            Element body= sp.call("http://fpat.ru/DemoEnterprise/ws/1csoap.1cws","http://fpat.ru#WebStorageService:FindProduct",envelope);
            return body;
        }
        soap sp;

    protected void onPreExecute(){
        waitDialog= ProgressDialog.show(con, "", "Loading for"+idBare+ " Please wait...", true);
    }

    protected void onPostExecute(Element body){


        Element Return=(Element) ((Element) body.getElementsByTagName("m:FindProductResponse").item(0)).getElementsByTagName("m:return").item(0);
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



        waitDialog.dismiss();
    }
    }

}
