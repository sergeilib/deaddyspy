package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArraySet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class RoutinsRepo {
    private DatabaseHelper databaseHelper;

    public RoutinsRepo(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public int insert(Routins routins) {
        //Open connection to write data

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Routins.KEY_ROUTINE_NAME, routins.getRoutine());
        values.put(Routins.KEY_PAIRED_DEVICE, routins.getPaired_dev());
        values.put(Routins.KEY_APP, routins.getApp());
        values.put(Routins.KEY_ACTION, routins.getAction());



        // Inserting Row
        long routins_Id = db.insert(Routins.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) routins_Id;
    }

    public void deleteByName(String routine_name) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Routins.TABLE, Routins.KEY_ROUTINE_NAME + "= ?", new String[] { routine_name });
        db.close(); // Closing database connection
    }

    public void deleteByID(int id) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Routins.TABLE, Routins.KEY_ID + "= ?", new String[] { String.valueOf(id) });
        db.close(); // Closing database connection
    }

    public void update(Routins routins) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Routins.KEY_ROUTINE_NAME, routins.getRoutine());
        values.put(Routins.KEY_PAIRED_DEVICE,routins.getPaired_dev());
        values.put(Routins.KEY_APP,routins.getApp());
        values.put(Routins.KEY_ACTION,routins.getAction());


        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Routins.TABLE, values, routins.KEY_ID + "= ?", new String[] { String.valueOf(routins.getRoutins_ID()) });
        db.close(); // Closing database connection
    }

    /*public ArrayList<HashMap<String, String>> getRoutinsPairedDeviceList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Routins.KEY_ID + "," +
                Routins.KEY_ROUTINE_NAME + "," +
                Routins.KEY_PAIRED_DEVICE + "," +
                Routins.KEY_APP + ","+
                Routins.KEY_ACTION  +
                " FROM " + Routins.TABLE;


        //Student student = new Student();
        ArrayList<HashMap<String, String>> routinsList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> routine = new HashMap<String, String>();
                String routinePairedDeviceTriger = null;
                if (cursor.getString(cursor.getColumnIndex(Routins.KEY_PAIRED_DEVICE)) != null){
                    routinePairedDeviceTriger = cursor.getString(cursor.getColumnIndex(Routins.KEY_PAIRED_DEVICE));
                }else{
                    break;
                }
                routine.put(routinePairedDeviceTriger,
                        cursor.getString(cursor.getColumnIndex(Routins.KEY_ACTION)));
//                student.put("name", cursor.getString(cursor.getColumnIndex(Location.KEY_name)));
                routinsList.add(routine);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return routinsList;

    }*/

    public Set getRoutinsPairedDeviceList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Routins.KEY_ID + "," +
                Routins.KEY_ROUTINE_NAME + "," +
                Routins.KEY_PAIRED_DEVICE + "," +
                Routins.KEY_APP + ","+
                Routins.KEY_ACTION  +
                " FROM " + Routins.TABLE;


        //Student student = new Student();
        Set routinsList = null;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {

//                HashMap<String, String> routine = new HashMap<String, String>();
//                String routinePairedDeviceTriger = null;
//                if (cursor.getString(cursor.getColumnIndex(Routins.KEY_PAIRED_DEVICE)) != null){
//                    routinePairedDeviceTriger = cursor.getString(cursor.getColumnIndex(Routins.KEY_PAIRED_DEVICE));
//                }else{
//                    break;
//                }
//                routine.put(routinePairedDeviceTriger,
//                        cursor.getString(cursor.getColumnIndex(Routins.KEY_ACTION)));
//                student.put("name", cursor.getString(cursor.getColumnIndex(Location.KEY_name)));
                routinsList.add(cursor.getString(cursor.getColumnIndex(Routins.KEY_PAIRED_DEVICE)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return routinsList;

    }


    public HashMap<String, Routins> getRoutinByName(String routine_name){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Routins.KEY_ID + "," +
                Routins.KEY_ROUTINE_NAME + "," +
                Routins.KEY_PAIRED_DEVICE + "," +
                Routins.KEY_APP + ","+
                Routins.KEY_ACTION  +
                " FROM " + Point.TABLE
                + " WHERE " +
                Routins.KEY_ROUTINE_NAME + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;

        HashMap<String,Routins> routineHash = new HashMap<>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { routine_name } );

        if (cursor.moveToFirst()) {
            do {
                String action = cursor.getString(cursor.getColumnIndex(Routins.KEY_ACTION));
                //routine.put(action,cursor.getInt(cursor.getColumnIndex(Point.KEY_ID)));
                Routins routins = new Routins();
                routins.setRoutine(cursor.getString(cursor.getColumnIndex(Routins.KEY_ROUTINE_NAME)));
                routins.setPaired_dev(cursor.getString(cursor.getColumnIndex(Routins.KEY_PAIRED_DEVICE)));
                routins.setApp(cursor.getString(cursor.getColumnIndex(Routins.KEY_APP)));
                routins.setRoutins_ID(cursor.getInt(cursor.getColumnIndex(Routins.KEY_ID)));
                routineHash.put(action,routins);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return routineHash;
    }

    public HashMap<String, Routins> getRoutineByPairedDev(String pairedDev){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Routins.KEY_ID + "," +
                Routins.KEY_ROUTINE_NAME + "," +
                Routins.KEY_PAIRED_DEVICE + "," +
                Routins.KEY_APP + ","+
                Routins.KEY_ACTION  +
                " FROM " + Routins.TABLE
                + " WHERE " +
                Routins.KEY_PAIRED_DEVICE + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;

        HashMap<String,Routins> routineHash = new HashMap<>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { pairedDev } );

        if (cursor.moveToFirst()) {
            do {
                String action = cursor.getString(cursor.getColumnIndex(Routins.KEY_ACTION));
                //routine.put(action,cursor.getInt(cursor.getColumnIndex(Point.KEY_ID)));
                Routins routins = new Routins();
                routins.setRoutine(cursor.getString(cursor.getColumnIndex(Routins.KEY_ROUTINE_NAME)));
                routins.setPaired_dev(cursor.getString(cursor.getColumnIndex(Routins.KEY_PAIRED_DEVICE)));
                routins.setApp(cursor.getString(cursor.getColumnIndex(Routins.KEY_APP)));
                routins.setRoutins_ID(cursor.getInt(cursor.getColumnIndex(Routins.KEY_ID)));
                routineHash.put(action,routins);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return routineHash;
    }

}
