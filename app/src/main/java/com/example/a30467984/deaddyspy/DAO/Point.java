package com.example.a30467984.deaddyspy.DAO;

import android.text.method.DateTimeKeyListener;

/**
 * Created by 30467984 on 7/22/2018.
 */

public class Point {
    public static final String TABLE = "ROUTING_DETAILS";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_latitude = "latitude";
    public static final String KEY_longitude = "longitude";
    public static final String KEY_place = "place";
    public static final String KEY_speed = "speed";
    public static final String KEY_limit = "speed_limit";
    public static final String KEY_date = "date";
    public static final String KEY_tripNum = "trip_number";
    // property help us to keep data
    public int place_ID;
    public Double latitude;
    public Double longitude;
    public int speed;
    public int limit;
    public String date;
    public String place;
    public int trip_number;
}
