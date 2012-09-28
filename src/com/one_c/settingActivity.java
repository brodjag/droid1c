package com.one_c;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import com.one_c.db.DatabaseHelper;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 27.09.12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
public class settingActivity extends Activity {
    private Activity con;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con=this;
        setContentView(R.layout.settingsv);
        setValues();

        findViewById(R.id.setting_server_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                serverDialog();
            }
        });

        findViewById(R.id.setting_authofication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                authDialog();
            }
        });

        findViewById(R.id.setting_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                DatabaseHelper dh=new DatabaseHelper(con);
               String flashValue=dh.getSetting("flash");
               if(flashValue.equals("вкл.")){
                   dh.setSetting("flash","выкл.");
               }else {
                   dh.setSetting("flash","вкл.");
               }
                setValues();
            }
        });
    }


    private void serverDialog(){
        // AlertDialog.Builder builder = new AlertDialog.Builder(con);
        final Dialog dialog = new Dialog(con,R.style.FullHeightDialog);
        dialog.setContentView(R.layout.setting_server_dialog);
        dialog.findViewById(R.id.dialog_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                DatabaseHelper dh=new DatabaseHelper(con);
                dh.setSetting("url_pre", ((TextView) dialog.findViewById(R.id.url_pre)).getText().toString());
                dh.setSetting("url_area", ((TextView) dialog.findViewById(R.id.url_area)).getText().toString());

                setValues();
                dialog.dismiss();
            }
        });

        DatabaseHelper dh=new DatabaseHelper(con);
        ((TextView) dialog.findViewById(R.id.url_pre)).setText(dh.getSetting("url_pre"));
        ((TextView) dialog.findViewById(R.id.url_area)).setText(dh.getSetting("url_area"));



      dialog.show();
    }

    private void authDialog(){
        // AlertDialog.Builder builder = new AlertDialog.Builder(con);
        final Dialog dialog = new Dialog(con,R.style.FullHeightDialog);
        dialog.setContentView(R.layout.settings_auth_dialog);
        dialog.findViewById(R.id.dialog_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {  ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) { }
                DatabaseHelper dh=new DatabaseHelper(con);

                dh.setSetting("login", ((TextView) dialog.findViewById(R.id.d_login)).getText().toString());
                dh.setSetting("password", ((TextView) dialog.findViewById(R.id.d_password)).getText().toString());

                setValues();
                dialog.dismiss();
            }
        });

        DatabaseHelper dh=new DatabaseHelper(con);
        ((TextView) dialog.findViewById(R.id.d_login)).setText(dh.getSetting("login"));
        ((TextView) dialog.findViewById(R.id.d_password)).setText(dh.getSetting("password"));



        dialog.show();
    }

    void setValues(){
        DatabaseHelper dh=new DatabaseHelper(con);
        ((TextView) findViewById(R.id.url_pre)).setText(dh.getSetting("url_pre"));
        ((TextView) findViewById(R.id.url_area)).setText(dh.getSetting("url_area"));
        ((TextView) findViewById(R.id.login)).setText(dh.getSetting("login"));
        ((TextView) findViewById(R.id.password)).setText(dh.getSetting("password"));
        ((TextView) findViewById(R.id.flash)).setText(dh.getSetting("flash"));

    }
}