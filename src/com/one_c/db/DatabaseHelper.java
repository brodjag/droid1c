package com.one_c.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IntelliJ IDEA.
 * User: brodjag
 * Date: 17.09.12
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName="tempDB";
    static final int version=1;
    static final String storeDb="stores";





    public DatabaseHelper(Context context) {
        super(context, dbName, null,version);
    }

    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE stores (id INTEGER PRIMARY KEY, name TEXT, kod TEXT)");
        
        db.execSQL("CREATE TABLE scaned (id integer PRIMARY KEY,barcode text,kod text,name text,article text,FullName text,count integer,id_store text, unit text)");

        db.execSQL("CREATE TABLE setting (id INTEGER PRIMARY KEY, name TEXT, value TEXT)");

        db.execSQL("Insert into setting (name, value)  values('url_pre','http://fpat.ru/DemoEnterprise/ws/')");
        db.execSQL("Insert into setting (name, value)  values('url_area','DroidC')");
        db.execSQL("Insert into setting (name, value)  values('login','admin')");
        db.execSQL("Insert into setting (name, value)  values('password','123')");
        db.execSQL("Insert into setting (name, value)  values('flash','вкл.')");

//      setSetting("url_pre","http://fpat.ru/DemoEnterprise/ws/");
       // setSetting("url_area","1csoap");


        /*
        db.execSQL("CREATE TABLE "+deptTable+" ("+colDeptID+ " INTEGER PRIMARY KEY , "+
                colDeptName+ " TEXT)");

        db.execSQL("CREATE TABLE "+employeeTable+" ("+colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
        colName+" TEXT, "+colAge+" Integer, "+colDept+
                " INTEGER NOT NULL ,FOREIGN KEY ("+colDept+") REFERENCES"+deptTable+" ("+colDeptID+"));");


        db.execSQL("CREATE TRIGGER fk_empdept_deptid " +
                " BEFORE INSERT "+
                " ON "+employeeTable
                +" FOR EACH ROW BEGIN"+
                " SELECT CASE WHEN ((SELECT "+colDeptID+" FROM "+deptTable+
                " WHERE "+colDeptID+"=new."+colDept+" ) IS NULL)"+
        " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
                "  END;");

        db.execSQL("CREATE VIEW "+viewEmps+
                " AS SELECT "+employeeTable+"."+colID+" AS _id,"+
                " "+employeeTable+"."+colName+","+
                " "+employeeTable+"."+colAge+","+
                " "+deptTable+"."+colDeptName+""+
                " FROM "+employeeTable+" JOIN "+deptTable+
                " ON "+employeeTable+"."+colDept+" ="+deptTable+"."+colDeptID
        );
        */
        //Inserts pre-defined departments
      //  InsertDepts(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+"stores");
        db.execSQL("DROP TABLE IF EXISTS "+"scaned");
        db.execSQL("DROP TABLE IF EXISTS "+"setting");

         /*
        db.execSQL("DROP TABLE IF EXISTS "+employeeTable);
        db.execSQL("DROP TABLE IF EXISTS "+deptTable);
        db.execSQL("DROP TRIGGER IF EXISTS dept_id_trigger");
        db.execSQL("DROP TRIGGER IF EXISTS dept_id_trigger22");
        db.execSQL("DROP TRIGGER IF EXISTS fk_empdept_deptid");
        db.execSQL("DROP VIEW IF EXISTS "+viewEmps);
        */
        onCreate(db);
    }


/***************************************
stores
 ***************************************/
public long insertToStore(String name, String kod) {
    SQLiteDatabase db=this.getWritableDatabase();
    ContentValues cv=new ContentValues(2);
     cv.put("name" ,name);
    cv.put("kod" ,kod);
      long  res=db.insert("stores",null,cv);
    db.close();
    return res;
}

public Cursor getAllStores()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from stores",new String [] {});
         cur.moveToFirst();
        db.close();
        return cur;
    }

public void removeAllStores(){
    SQLiteDatabase db=this.getWritableDatabase();
    db.execSQL("DROP TABLE IF EXISTS "+"stores");
    db.execSQL("CREATE TABLE stores (id INTEGER PRIMARY KEY, name TEXT, kod TEXT)");
    db.close();
};



/***************************************
 scaned
 ***************************************/
public long insertToScaned(String barcode, String kod, String name, String article, String FullName,int count, String id_store, String unit ) {
    SQLiteDatabase db=this.getWritableDatabase();
    ContentValues cv=new ContentValues();
    cv.put("barcode" ,barcode);
    cv.put("kod" ,kod);
    cv.put("name" ,name);
    cv.put("article" ,article);
    cv.put("FullName" ,FullName);
    //cv.put("store_id" ,store_id);
    cv.put("count",count);
    cv.put("id_store",id_store);
    cv.put("unit",unit);
    long  res=db.insert("scaned",null,cv);
    db.close();
    return res;
}

public Cursor getAllScaned(String id_store)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from scaned where id_store=?",new String [] {id_store});
        cur.moveToFirst();
        db.close();
        return cur;
    }
    public void removeAllScaned(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+"scaned");
        db.execSQL("CREATE TABLE scaned (id integer PRIMARY KEY,barcode text,kod text,name text,article text,FullName text,count integer,id_store text, unit text)");
        db.close();
    };

    public void removeScanedId(String id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete("scaned","id=?",new String[]{id});
        db.close();

    }

    public Cursor getScanedById(String id)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from scaned where id=?",new String [] {id});
        cur.moveToFirst();
        db.close();
        return cur;
    }

    public void updateScanedId(String id,int count)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("count", count);
        db.update("scaned",args,"id=?",new String[]{id});
       // db.delete("scaned","id=?",new String[]{id});
        db.close();

    }

    public int getScanedIdByBare(String barcode,String id_store)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
      // String s= String.format("SELECT * from scaned where barcode='"+barcode+"' ",new String [] {});
      //  Log.d("selectBare", s);

        cur= db.rawQuery("SELECT * from scaned where barcode='"+barcode+"' and id_store=?",new String [] {id_store});
        cur.moveToFirst();
        int res=-1;
        if(cur.getCount()>0){res=cur.getInt(0); }
        db.close();
         cur.close();
        return res;
    }

//********************************
//setting values
//********************************
public String getSetting(String value){
    SQLiteDatabase db=this.getReadableDatabase();
    Cursor cur;
    cur= db.rawQuery("SELECT * from setting where name=\""+value+"\"",new String [] {});
    cur.moveToFirst();
    String res="";
    if (cur.getCount()>0){ res=cur.getString(2);  }
    cur.close();
    db.close();
    return res;
}

    public double newSetting(String name,String value){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("name" ,name);
        cv.put("value" ,value);
        long  res=db.insert("setting",null,cv);
        db.close();
        return res;
    }

    private void settingUpdate(String name, String value){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("value", value);
        db.update("setting",args,"name=?",new String[]{name});
        db.close();
    }

    private double getSettingCount(String name){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        cur= db.rawQuery("SELECT * from setting where name='"+name+"'",new String [] {});
        double res=cur.getCount();
        cur.close();
        db.close();
        return res;
    }

    public void setSetting(String name, String value){
     if (getSettingCount(name)==0){
         newSetting(name,value);
     } else {
         settingUpdate(name,value);
     }
    }


}