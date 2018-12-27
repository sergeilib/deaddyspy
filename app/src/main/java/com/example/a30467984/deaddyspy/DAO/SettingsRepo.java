package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by 30467984 on 7/18/2018.
 */

public class SettingsRepo {
    private DatabaseHelper databaseHelper;

    public SettingsRepo(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public int insert(Settings settings) {

        //Open connection to write data
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Settings.KEY_Param, settings.param);
        values.put(Settings.KEY_value, settings.value);
        values.put(Settings.KEY_type, settings.type);


        // Inserting Row
        long setting_id = db.insert(Settings.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) setting_id;
    }
    public HashMap getSettingsList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Settings.KEY_ID + "," +
                Settings.KEY_Param + "," +
                Settings.KEY_value + "," +
                Settings.KEY_type  +
                " FROM " + Settings.TABLE;

        //Student student = new Student();
        HashMap<String,HashMap<String,String>> settingsHash = new HashMap<String, HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
//                HashMap<String, String> settings = new HashMap<String, String>();
//                settings.put(cursor.getString(cursor.getColumnIndex(Settings.KEY_value)), cursor.getString(cursor.getColumnIndex(Settings.KEY_ID)));
//                student.put("name", cursor.getString(cursor.getColumnIndex(Location.KEY_name)));
//                Settings s = new Settings();
//                s.param = cursor.getString(cursor.getColumnIndex(Settings.KEY_Param));
//                s.value = cursor.getString(cursor.getColumnIndex(Settings.KEY_value));
//                s.type = cursor.getString(cursor.getColumnIndex(Settings.KEY_type));

                HashMap<String,String> s = new HashMap<String, String>();
                s.put("value",cursor.getString(cursor.getColumnIndex(Settings.KEY_value)));
                s.put("type",cursor.getString(cursor.getColumnIndex(Settings.KEY_type)));
                settingsHash.put(cursor.getString(cursor.getColumnIndex(Settings.KEY_Param)),s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return settingsHash;

    }
    public void dropSettingsTable(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Settings.TABLE);
    }

    public void updateSettings(String param,String value) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Settings.KEY_value, value);


        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Settings.TABLE, values, Settings.KEY_Param + " = ? ",new String[] {param});
        db.close(); // Closing database connection
    }

}
