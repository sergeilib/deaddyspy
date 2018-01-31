package com.example.a30467984.deaddyspy.DAO;

import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.ScriptIntrinsicYuvToRGB;

/**
 * Created by 30467984 on 1/18/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "deaddy_spy";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_KOORDINATES = "koordinates";
    private static final String TABLE_SETTINGS = "settings";
    private static final String KEY_ID = "id";
    private static final String LATT = "latitude";
    private static final String LONG = "longitude";
    private static final String PLACE = "place";
    private static final String SPEED = "speed";
    private static final String LIMIT = "limit";
    private static final String DATE = "update_date";
    

}
