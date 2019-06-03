    package com.example.a30467984.deaddyspy.DAO;

import android.app.Notification;
import android.app.SharedElementCallback;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Message;
import android.renderscript.ScriptIntrinsicYuvToRGB;

/**
 * Created by 30467984 on 1/18/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "deaddy_spy";

    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 8;

    public DatabaseHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        Settings settings = new Settings();
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + Point.TABLE  + " ("
                + Point.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Point.KEY_date + " DATETIME, "
                + Point.KEY_speed + " INTEGER, "
                + Point.KEY_limit + " INTEGER, "
                + Point.KEY_place + " TEXT, "
                + Point.KEY_longitude + " DOUBLE, "
                + Point.KEY_latitude + " DOUBLE, "
                + Point.KEY_tripNum + " INTEGER  )";

        db.execSQL(CREATE_LOCATION_TABLE);

        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + Settings.TABLE + " ("
                + Settings.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + Settings.KEY_Param + " TEXT, "
                + Settings.KEY_value + " TEXT, "
                + Settings.KEY_type + " TEXT) ";
        db.execSQL(CREATE_SETTINGS_TABLE);



        ContentValues stringContent = settings.getStringContentValues();
        if (stringContent != null){
            for (String key : stringContent.keySet()){
                String stringQuey = "INSERT INTO " + Settings.TABLE + " (param,value,type) VALUES " +
                        "('" + key +"','"+ stringContent.get(key) +"','String' )";
                db.execSQL(stringQuey);
            }

        }
        ContentValues integerContent = settings.getIntContentValues();
        if(integerContent != null){
            for(String key : integerContent.keySet()){
                String intQuery = "INSERT INTO " + Settings.TABLE + " (param,value,type) VALUES " +
                        "('" + key +"','"+ integerContent.get(key) +"','int' )";
                db.execSQL(intQuery);
            }
        }

        String CREATE_ALERT_TABLE = "CREATE TABLE " + Alert.TABLE + " ("
                + Alert.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + Alert.KEY_Param + " TEXT, "
                + Alert.KEY_value + " TEXT, "
                + Alert.KEY_name + " TEXT,"
                + Alert.KEY_type + " TEXT) ";
        db.execSQL(CREATE_ALERT_TABLE);

        String CREATE_ALERT_LOG_TABLE = "CREATE TABLE " + LogParams.TABLE + " ("
                + LogParams.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + LogParams.KEY_date + " DATETIME, "
                + LogParams.KEY_method + " TEXT, "
                + LogParams.KEY_alert_name + " TEXT, "
                + LogParams.KEY_destination + " TEXT,"
                + LogParams.KEY_message+ " TEXT) ";
        db.execSQL(CREATE_ALERT_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Point.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Settings.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Alert.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LogParams.TABLE);
        // Create tables again
        onCreate(db);

    }

}
