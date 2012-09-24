package com.one_c.lib;

import android.util.Base64;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 12.09.12
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class soap {
public String text=null;


public soap(){}


public   Element call(String url, String soapAction,  String envelope)  {
        final DefaultHttpClient httpClient=new DefaultHttpClient();

        // параметры запроса
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 15000);
        // устанавливаем параметры
        HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

        // С помощью метода POST отправляем конверт
        HttpPost httppost = new HttpPost(url);
        // и заголовки
        httppost.setHeader("soapaction", soapAction);
        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
        //athorisation
        String pass64= new String(Base64.encode("admin:123".getBytes(), Base64.NO_WRAP));
        Log.d("pass64", pass64);        //YWRtaW46MTIz
        httppost.setHeader("Authorization", ("Basic "+pass64));

        String responseString=null;

        try {
            // выполняем запрос
            HttpEntity entity = new StringEntity(envelope);
            httppost.setEntity(entity);
            // Заголоаок запроса
            ResponseHandler<String> rh=new ResponseHandler<String>() {
                // вызывается, когда клиент пришлет ответ
                public String handleResponse(HttpResponse response)  throws ClientProtocolException, IOException {
                    // получаем ответ
                    HttpEntity entity = response.getEntity();
                    // читаем его в массив
                    StringBuffer out = new StringBuffer();
                    byte[] b = EntityUtils.toByteArray(entity);
                    // write the response byte array to a string buffer
                    out.append(new String(b, 0, b.length));
                    return out.toString();
                }
            };
            responseString=httpClient.execute(httppost, rh);
        }
        catch (Exception e) {      Log.v("exception", e.toString());}

        // закрываем соединение
        httpClient.getConnectionManager().shutdown();
        text= responseString;
        if(responseString!=null){
          Log.d("soap",responseString);
        }else {Log.d("soap","responseString =null");}

        //парсим

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(responseString)));
            Element body=(Element) ((Element) (doc.getElementsByTagName("soap:Envelope").item(0))).getElementsByTagName("soap:Body").item(0);
            return body;

        } catch (Exception e) {       e.printStackTrace(); return null; }

       // return null;
    }

}
