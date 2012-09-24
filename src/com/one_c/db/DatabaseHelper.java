package com.one_c.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        
        db.execSQL("CREATE TABLE scaned (id integer PRIMARY KEY,barcode text,kod text,name text,article text,FullName text,store_id integer,count integer)");

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
public long insertToScaned(String barcode, String kod, String name, String article, String FullName, int store_id,int count) {
    SQLiteDatabase db=this.getWritableDatabase();
    ContentValues cv=new ContentValues(2);
    cv.put("barcode" ,barcode);
    cv.put("kod" ,kod);
    cv.put("name" ,name);
    cv.put("article" ,article);
    cv.put("FullName" ,FullName);
    cv.put("store_id" ,store_id);
    cv.put("count",count);
    long  res=db.insert("scaned",null,cv);
    db.close();
    return res;
}

public Cursor getAllScaned()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from scaned",new String [] {});
        cur.moveToFirst();
        db.close();
        return cur;
    }
    public void removeAllScaned(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+"scaned");
        db.execSQL("CREATE TABLE scaned (id integer PRIMARY KEY,barcode text,kod text,name text,article text,FullName text,store_id integer,count integer)");
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

    public int getScanedIdByBare(String barcode)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
       String s= String.format("SELECT * from scaned where barcode='"+barcode+"'",new String [] {});
        Log.d("selectBare", s);

              cur= db.rawQuery("SELECT * from scaned where barcode='"+barcode+"'",new String [] {});
        cur.moveToFirst();
        int res=-1;
        if(cur.getCount()>0){res=cur.getInt(0); }
        db.close();
         cur.close();
        return res;
    }

}