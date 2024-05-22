package com.example.task9_1p;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class MySqliteOpenHelper extends SQLiteOpenHelper {
    private static SQLiteOpenHelper mInstance;

    public static synchronized SQLiteOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySqliteOpenHelper(context, "AnglinDB", null, 1);
        }
        return mInstance;
    }

    private MySqliteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table person(_id integer primary key autoincrement, value0 text, value1 text, value2 text, value3 text, value4 text, type text,latitude text,longitude text)";
//        String sql="create table person(_id varchar(30),value0 varchar(30),value1 varchar(30),value2 varchar(30),value3 varchar(30),value4 varchar(30),value1 varchar(30),)";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


    public static List<Bean> query(Context context) {
        SQLiteOpenHelper helper = MySqliteOpenHelper.getInstance(context);
        //database
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Bean> list = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from person", null);
            while (cursor.moveToNext()) {
                @SuppressLint("Range")
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                @SuppressLint("Range")
                String value0 = cursor.getString(cursor.getColumnIndex("value0"));
                String value1 = cursor.getString(cursor.getColumnIndex("value1"));
                String value2 = cursor.getString(cursor.getColumnIndex("value2"));
                String value3 = cursor.getString(cursor.getColumnIndex("value3"));
                String value4 = cursor.getString(cursor.getColumnIndex("value4"));
                String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                list.add(new Bean(_id, value0, value1, value2, value3, value4, type, latitude, longitude));
            }

            cursor.close();

            db.close();
        }
        return list;

    }


    public static void insert(Context context,
                              String value0, String value1, String value2, String value3, String value4
            , String type,  String latitude, String longitude
    ) {
        SQLiteOpenHelper helper = MySqliteOpenHelper.getInstance(context);

        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("value0", value0);
            values.put("value1", value1);
            values.put("value2", value2);
            values.put("value3", value3);
            values.put("value4", value4);
            values.put("type", type);
            values.put("latitude", latitude);
            values.put("longitude", longitude);
            db.insert("person", null, values);

            db.close();
        }


    }


    public static void delete(Context context, int _id) {
        SQLiteOpenHelper helper = MySqliteOpenHelper.getInstance(context);

        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {

            String sql = "delete from person  where _id =?";
            db.execSQL(sql, new Object[]{_id});

            db.close();
        }

    }


}
