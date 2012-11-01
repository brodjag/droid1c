package com.droid_c_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.droid_c_demo.db.DatabaseHelper;
import com.droid_c_demo.lib.fileLib;
import com.droid_c_demo.lib.soap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 04.10.12
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class uploadRequest {


public Activity con;

String productArray="";
String  login;
String password;
String id_store;
    public uploadRequest(Activity c){
        con=c;       // storeId=storageId;   , String storageId

        fileLib fl=new fileLib(con);
        id_store=fl.read("currentCode").split(";")[2];

        DatabaseHelper dh=new DatabaseHelper(con);

        login=dh.getSetting("login");
        password=dh.getSetting("password");
        // String barecode=sym.getData().toString();



            url=dh.getURL();
            setProductArray();
            if (productArray.equals("")){Toast.makeText(con, "Список пуст. Выгрузка невозможна.", Toast.LENGTH_LONG).show(); return;}
            new artTask().execute();




    }
String url;


private class artTask extends AsyncTask<Void,Void,Element> {

    public ProgressDialog waitDialog=null;
    @Override
    protected Element doInBackground(Void... voids) {
        String envelope="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:dro=\"http://Droid-C.ru\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <dro:AddDocument>\n" +
                "         <dro:Type>1</dro:Type>\n" +
                "         <dro:Products>\n" +
                "            <!--1 or more repetitions:-->\n" +
                productArray+
                "            <!--You may enter ANY elements at this point-->\n" +
                "         </dro:Products>\n" +
                "         <dro:StorageKod>"+id_store+"</dro:StorageKod>\n" +
                "           <dro:User>"+login +"</dro:User>"+
                "      </dro:AddDocument>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>";


        sp=new soap(login,password);
        //"http://fpat.ru/DemoEnterprise/ws/1csoap.1cws"


        Element body= sp.call(url,"http://Droid-C.ru#WebStorageService:AddDocument",envelope);
        return body;
    }
    soap sp;

    protected void onPreExecute(){
        waitDialog= ProgressDialog.show(con, "", "выгрузка, пожайлуста подожите...", true);
    }

    protected void onPostExecute(Element body){
        waitDialog.dismiss();
        NodeList faultList= body.getElementsByTagName("soap:Fault");

        Log.d("ssa",""+faultList.getLength());
        if(faultList.getLength()==1){
            errorDialog((Element) faultList.item(0), con);
            return;
        }



        Element Return=(Element) ((Element) body.getElementsByTagName("m:AddDocumentResponse").item(0)).getElementsByTagName("m:return").item(0);
        String result=Return.getFirstChild().getNodeValue();

        Log.d("upload_result",result);
        if (result.equals("true")){
            Toast.makeText(con, "выгрузка прошла удачно",Toast.LENGTH_SHORT).show();
            scanedList.cleanStore(con);
        }else {
            Toast.makeText(con, "упс!!! выгрузка прошла Неудачно",Toast.LENGTH_SHORT).show();
        }

    }
}

void setProductArray(){
    fileLib fl=new fileLib(con);
    String id_store=fl.read("currentCode").split(";")[2];

    DatabaseHelper dh=new DatabaseHelper(con);
    Cursor c= dh.getAllScaned(id_store);

    String textList="";

    for(int i=0; i<c.getCount();i++){
        c.moveToPosition(i);
        Log.d("scaned", c.getString(1));
         String kod=c.getString(2);
         String count=c.getString(6);
        textList= textList+"  <dro:ProductSOAP>\n" +
                "               <dro:Kod>"+kod+"</dro:Kod>\n" +
                "               <dro:Count>"+count+"</dro:Count>\n"+
                 "            </dro:ProductSOAP>\n";;

    }
   // textList=textList+  "</dro:ProductSOAP>\n";

    c.close();
    productArray=textList;
    Log.d("upload_result",textList);

}


public static void errorDialog(Element fault, Context c){

     String reason="";

     try{
            reason= ((Element) fault.getElementsByTagName("soap:Reason").item(0)).getElementsByTagName("soap:Text").item(0).getFirstChild().getNodeValue();
     }catch (Exception e){}

    try{
      reason=fault.getElementsByTagName("detail").item(0).getFirstChild().getNodeValue().toString();
    }catch (Exception e){}


    AlertDialog.Builder builder = new AlertDialog.Builder(c);
    builder.setMessage(reason)
            .setCancelable(true)
            .setTitle("Ошибка на сервере:")

            .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.cancel();
                }
            });


    AlertDialog alert = builder.create();
    alert.show();

}

}
