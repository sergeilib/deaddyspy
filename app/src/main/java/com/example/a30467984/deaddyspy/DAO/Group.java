package com.example.a30467984.deaddyspy.DAO;

public class Group {
    public static final String TABLE = "Groups";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";

    // property help us to keep data
    public int alert_ID;
    public String name;

    public int getAlert_ID() {
        return alert_ID;
    }

    public void setAlert_ID(int alert_ID) {
        this.alert_ID = alert_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
