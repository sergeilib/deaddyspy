package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by 30467984 on 7/17/2018.
 */

public class RoutingRepo {
    private DatabaseHelper databaseHelper;

    public RoutingRepo(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public int insert(Point point) {
        //Open connection to write data

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Point.KEY_date, point.date);
        values.put(Point.KEY_speed, point.speed);
        values.put(Point.KEY_longitude, point.longitude);
        values.put(Point.KEY_latitude, point.latitude);
        values.put(Point.KEY_place, point.place);
        values.put(Point.KEY_tripNum, point.trip_number);
        values.put(Point.KEY_limit,point.limit);


        // Inserting Row
        long location_Id = db.insert(Point.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) location_Id;
    }

    public void delete(int location_Id) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Point.TABLE, Point.KEY_tripNum + "= ?", new String[] { String.valueOf(location_Id) });
        db.close(); // Closing database connection
    }

    public void update(Point point) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Point.KEY_place, point.place);
        values.put(Point.KEY_date, point.date);
        values.put(Point.KEY_speed, point.speed);
        values.put(Point.KEY_limit, point.limit);
        values.put(Point.KEY_longitude, point.longitude);
        values.put(Point.KEY_latitude, point.latitude);
        values.put(Point.KEY_tripNum, point.trip_number);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Point.TABLE, values, Point.KEY_ID + "= ?", new String[] { String.valueOf(point.place_ID) });
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getLocationList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Point.KEY_ID + "," +
                Point.KEY_speed + "," +
                Point.KEY_limit + "," +
                Point.KEY_latitude + "," +
                Point.KEY_longitude + "," +
                Point.KEY_date + "," +
                Point.KEY_tripNum + "," +

                " FROM " + Point.TABLE;

        //Student student = new Student();
        ArrayList<HashMap<String, String>> locationList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> location = new HashMap<String, String>();
                location.put("id", cursor.getString(cursor.getColumnIndex(Point.KEY_ID)));
//                student.put("name", cursor.getString(cursor.getColumnIndex(Location.KEY_name)));
                locationList.add(location);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locationList;

    }

    public Point getRoutingById(int Id){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Point.KEY_ID + "," +
                Point.KEY_speed + "," +
                Point.KEY_limit + "," +
                Point.KEY_latitude + "," +
                Point.KEY_longitude + "," +
                Point.KEY_date + "," +
                Point.KEY_tripNum +
                " FROM " + Point.TABLE
                + " WHERE " +
                Point.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;

        Point location = new Point();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                location.place_ID =cursor.getInt(cursor.getColumnIndex(Point.KEY_ID));
                location.place =cursor.getString(cursor.getColumnIndex(Point.KEY_place));
                location.date  =cursor.getString(cursor.getColumnIndex(Point.KEY_date));
                location.speed =cursor.getDouble(cursor.getColumnIndex(Point.KEY_speed));
                location.limit =cursor.getInt(cursor.getColumnIndex(Point.KEY_limit));
                location.latitude =cursor.getDouble(cursor.getColumnIndex(Point.KEY_latitude));
                location.latitude =cursor.getDouble(cursor.getColumnIndex(Point.KEY_longitude));
                location.trip_number = cursor.getInt(cursor.getColumnIndex(Point.KEY_tripNum));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return location;
    }
    public ArrayList<HashMap<String, String>> getLocationListByTripNumber(int trip) {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Point.KEY_ID + "," +
                Point.KEY_speed + "," +
                Point.KEY_limit + "," +
                Point.KEY_latitude + "," +
                Point.KEY_longitude + "," +
                Point.KEY_date + "," +
                Point.KEY_place + "," +
                Point.KEY_tripNum +

                " FROM " + Point.TABLE
                + " WHERE " +
                Point.KEY_tripNum + "=?";

        //Student student = new Student();
        ArrayList<HashMap<String, String>> locationList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, new String[] {""+trip});
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String>  location= new HashMap<String, String>();
                location.put("id", cursor.getString(cursor.getColumnIndex(Point.KEY_ID)));
                location.put("longitude",cursor.getString(cursor.getColumnIndex(Point.KEY_longitude)));
                location.put("latitude",cursor.getString(cursor.getColumnIndex(Point.KEY_latitude)));
                location.put("date",cursor.getString(cursor.getColumnIndex(Point.KEY_date)));
                location.put("limit",cursor.getString(cursor.getColumnIndex(Point.KEY_limit)));
                location.put("place",cursor.getString(cursor.getColumnIndex(Point.KEY_place)));
                location.put("speed",cursor.getString(cursor.getColumnIndex(Point.KEY_speed)));
                location.put("trip_number",cursor.getString(cursor.getColumnIndex(Point.KEY_tripNum)));
//                student.put("name", cursor.getString(cursor.getColumnIndex(Location.KEY_name)));
                locationList.add(location);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locationList;

    }

    public ArrayList<HashMap<String, String>> getStartingLocationList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Point.KEY_ID + "," +
                Point.KEY_speed + "," +
                Point.KEY_limit + "," +
                Point.KEY_latitude + "," +
                Point.KEY_longitude + "," +
                Point.KEY_date + "," +
                Point.KEY_place + "," +
                Point.KEY_tripNum +

                " FROM " + Point.TABLE
                + " GROUP BY " +
                Point.KEY_tripNum + " HAVING MIN(" + Point.KEY_ID + ")";

        //Student student = new Student();
        ArrayList<HashMap<String, String>> locationList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, new String[] {});
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String>  location= new HashMap<String, String>();
                location.put("id", cursor.getString(cursor.getColumnIndex(Point.KEY_ID)));
                location.put("longitude",cursor.getString(cursor.getColumnIndex(Point.KEY_longitude)));
                location.put("latitude",cursor.getString(cursor.getColumnIndex(Point.KEY_latitude)));
                location.put("date",cursor.getString(cursor.getColumnIndex(Point.KEY_date)));
                location.put("limit",cursor.getString(cursor.getColumnIndex(Point.KEY_limit)));
                location.put("place",cursor.getString(cursor.getColumnIndex(Point.KEY_place)));
                location.put("speed",cursor.getString(cursor.getColumnIndex(Point.KEY_speed)));
                location.put("trip_number",cursor.getString(cursor.getColumnIndex(Point.KEY_tripNum)));
//                student.put("name", cursor.getString(cursor.getColumnIndex(Location.KEY_name)));
                locationList.add(location);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locationList;

    }

    public int getMaxTripNumber(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  MAX(" + Point.KEY_tripNum + ") FROM " + Point.TABLE;
//        Cursor cursor = db.query(Location.TABLE,null,selectQuery,null,null,null,null); //where en like '"+name+"%'");
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor != null) {
            cursor.moveToFirst();
            int id= cursor.getInt(0);
            return id;
        }
        return 0;
    }
    public void dropRoutingTable(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Point.TABLE);
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
