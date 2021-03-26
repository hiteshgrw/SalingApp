package com.example.saleservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class schoolnameaddhelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME;
    public static String TABLE_NAME = "schoolnamedet";
    private Context mcontext;
    public schoolnameaddhelper(@Nullable Context context,String name) {
        super(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/.SaleServiceDB/Schooldata/" + name + ".db",null,1);
        DATABASE_NAME = name + ".db";
        mcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLE_NAME+" (NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(String data)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",data);
        db.insert(TABLE_NAME,null,contentValues);
    }
    public String getname()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
            if(cursor.getCount()>0)
            {
                cursor.moveToFirst();
                String name = cursor.getString(0);
                return name;
            }
            else {
                return null;
            }
        }catch (Exception e)
        {
            Toast.makeText(mcontext, "No School is assigned to this user ", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    public void update(String oldname,String newname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",newname);
        db.update(TABLE_NAME,contentValues,"NAME  = ?",new String[]{oldname});
    }
}
