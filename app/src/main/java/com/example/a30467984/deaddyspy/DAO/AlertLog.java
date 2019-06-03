package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 30467984 on 3/19/2019.
 */

public class AlertLog {
    private DatabaseHelper databaseHelper;

    public AlertLog(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }
    public int insert(LogParams alert) {

        //Open connection to write data
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LogParams.KEY_date, alert.getDate());
        values.put(LogParams.KEY_method, alert.getMethod());
        values.put(LogParams.KEY_destination, alert.getDestination());
        values.put(LogParams.KEY_message, alert.getMessage());


        // Inserting Row
        long log_id = db.insert(LogParams.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) log_id;
    }
    public Map getLogList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                LogParams.KEY_ID + "," +
                LogParams.KEY_date + "," +
                LogParams.KEY_message + "," +
                LogParams.KEY_destination + "," +
                LogParams.KEY_message  +
                " FROM " + LogParams.TABLE;

        //Student student = new Student();
        Map<String,HashMap<String,String>> logHash = new HashMap<String, HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {


                HashMap<String,String> s = new HashMap<String, String>();
                s.put("date",cursor.getString(cursor.getColumnIndex(LogParams.KEY_date)));
                s.put("allertname",cursor.getString(cursor.getColumnIndex(LogParams.KEY_alert_name)));
                s.put("method",cursor.getString(cursor.getColumnIndex(LogParams.KEY_method)));
                s.put("destination",cursor.getString(cursor.getColumnIndex(LogParams.KEY_destination)));
                s.put("message",cursor.getString(cursor.getColumnIndex(LogParams.KEY_message)));
                logHash.put(cursor.getString(cursor.getColumnIndex(LogParams.KEY_ID)),s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return logHash;

    }

    public String getLastUpdatedByName(String alertName){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  MAX(" + LogParams.KEY_date + ") AS " + LogParams.KEY_date+ " FROM " + LogParams.TABLE +
                " WHERE " + LogParams.KEY_alert_name + " = \"" + alertName +"\"";
//        Cursor cursor = db.query(Location.TABLE,null,selectQuery,null,null,null,null); //where en like '"+name+"%'");
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor != null) {
            cursor.moveToFirst();
            String date = cursor.getString(0);
            return date;
        }
        return null;
    }
}
