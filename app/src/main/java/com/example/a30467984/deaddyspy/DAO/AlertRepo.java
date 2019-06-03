package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 30467984 on 3/6/2019.
 */

public class AlertRepo {
    private DatabaseHelper databaseHelper;

    public AlertRepo(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public int insert(Alert alert) {

        //Open connection to write data
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Alert.KEY_Param, alert.param);
        values.put(Alert.KEY_value, alert.value);
        values.put(Alert.KEY_name, alert.name);
        values.put(Alert.KEY_type, alert.type);


        // Inserting Row
        long setting_id = db.insert(Alert.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) setting_id;
    }
    public Map getAlertList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Alert.KEY_ID + "," +
                Alert.KEY_Param + "," +
                Alert.KEY_value + "," +
                Alert.KEY_name + "," +
                Alert.KEY_type  +
                " FROM " + Alert.TABLE;

        //Student student = new Student();
        Map<String,HashMap<String,String>> alertHash = new HashMap<String, HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {


                HashMap<String,String> s = new HashMap<String, String>();
                s.put("value",cursor.getString(cursor.getColumnIndex(Alert.KEY_value)));
                s.put("type",cursor.getString(cursor.getColumnIndex(Alert.KEY_type)));
                s.put("param",cursor.getString(cursor.getColumnIndex(Alert.KEY_Param)));
                s.put("name",cursor.getString(cursor.getColumnIndex(Alert.KEY_name)));
                alertHash.put(cursor.getString(cursor.getColumnIndex(Alert.KEY_ID)),s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alertHash;

    }

    public Map getAlertParamsByName(String name){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Alert.KEY_ID + "," +
                Alert.KEY_Param + "," +
                Alert.KEY_value + "," +
                Alert.KEY_name + "," +
                Alert.KEY_type  +
                " FROM " + Alert.TABLE +
                " WHERE " +
                Alert.KEY_name + "=?";

        //Student student = new Student();
        //ArrayList<HashMap<String, String>> alertHash = new ArrayList<HashMap<String, String>>();
        Map<String,HashMap<String,String>> alertHash = new HashMap<String, HashMap<String, String>>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {""+name});
        if (cursor.moveToFirst()) {
            do {


                HashMap<String,String> s = new HashMap<String, String>();
                s.put("value",cursor.getString(cursor.getColumnIndex(Alert.KEY_value)));
                s.put("type",cursor.getString(cursor.getColumnIndex(Alert.KEY_type)));
                s.put("param",cursor.getString(cursor.getColumnIndex(Alert.KEY_Param)));
                s.put("name",cursor.getString(cursor.getColumnIndex(Alert.KEY_name)));
                alertHash.put(cursor.getString(cursor.getColumnIndex(Alert.KEY_ID)),s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alertHash;
    }

    public Map getAlertValuesByName(String name){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Alert.KEY_ID + "," +
                Alert.KEY_Param + "," +
                Alert.KEY_value + "," +
                Alert.KEY_name + "," +
                Alert.KEY_type  +
                " FROM " + Alert.TABLE +
                " WHERE " +
                Alert.KEY_name + "=?";

        //Student student = new Student();
        //ArrayList<HashMap<String, String>> alertHash = new ArrayList<HashMap<String, String>>();
        Map<String,String> alertHash = new HashMap<String, String>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {""+name});
        if (cursor.moveToFirst()) {
            do {


//                HashMap<String,String> s = new HashMap<String, String>();
//                s.put("value",cursor.getString(cursor.getColumnIndex(Alert.KEY_value)));
//                s.put("type",cursor.getString(cursor.getColumnIndex(Alert.KEY_type)));
//                s.put("param",cursor.getString(cursor.getColumnIndex(Alert.KEY_Param)));
//                s.put("name",cursor.getString(cursor.getColumnIndex(Alert.KEY_name)));
//                alertHash.put(cursor.getString(cursor.getColumnIndex(Alert.KEY_ID)),s);
                   alertHash.put(cursor.getString(cursor.getColumnIndex(Alert.KEY_Param)),cursor.getString(cursor.getColumnIndex(Alert.KEY_value)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alertHash;
    }

    public void dropAlertTable(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Alert.TABLE);
    }

    public void updateAlert(String param,String value) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Alert.KEY_value, value);


        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Alert.TABLE, values, Alert.KEY_Param + " = ? ",new String[] {param});
        db.close(); // Closing database connection
    }
    public void deleteAlert(String name) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Alert.TABLE, Alert.KEY_name + "= ?", new String[] { name });
        db.close(); // Closing database connection
    }
}
