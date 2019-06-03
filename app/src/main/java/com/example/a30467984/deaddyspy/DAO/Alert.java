package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;

/**
 * Created by 30467984 on 3/6/2019.
 */

public class Alert {
    public static final String TABLE = "Alerts";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_Param = "param";
    public static final String KEY_value = "value";
    public static final String KEY_name = "name";
    public static final String KEY_type = "type";
    // property help us to keep data
    public int alert_ID;
    public String param;
    public String value;
    public String name;
    public String type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParam() {

        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "Settings{" +

                "alert_ID=" + alert_ID +
                ", param='" + param + '\'' +
                ", value='" + value + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

