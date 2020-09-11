package com.example.a30467984.deaddyspy.DAO;

public class Group {
    public static final String TABLE = "Groups";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";

    // property help us to keep data
    public int group_ID;
    public String name;

    public int getGroup_ID() {
        return group_ID;
    }

    public void setGroup_ID(int alert_ID) {
        this.group_ID = alert_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
