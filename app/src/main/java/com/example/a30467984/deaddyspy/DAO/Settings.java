package com.example.a30467984.deaddyspy.DAO;


import android.content.ContentValues;

/**
 * Created by 30467984 on 7/18/2018.
 */

public class Settings {
    public static final String TABLE = "Settings";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_Param = "param";
    public static final String KEY_value = "value";
    public static final String KEY_type = "type";
    // property help us to keep data
    public int settings_ID;
    public String param;
    public String value;
    public String type;
    ContentValues stringContentValues = new ContentValues();

    public ContentValues getStringContentValues() {
        stringContentValues.put("language","english");
        stringContentValues.put("scale","km");
        stringContentValues.put("record","true");
        stringContentValues.put("paired_depend","");
        stringContentValues.put("app_depend","");
        stringContentValues.put("alert_activity","false");
        return stringContentValues;
    }
    public void setStringContentValues(String param,String value){

    }
    ContentValues intContentValues = new ContentValues();

    public ContentValues getIntContentValues() {
        //intContentValues.put("scale",)
        return intContentValues;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "settings_ID=" + settings_ID +
                ", param='" + param + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", stringContentValues=" + stringContentValues +
                ", intContentValues=" + intContentValues +
                '}';
    }
}

