package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;

public class Routins {
    public static final String TABLE = "Routins";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_ROUTINE_NAME = "routine";
    public static final String KEY_PAIRED_DEVICE = "paired_device";
    public static final String KEY_APP = "app";

    @Override
    public String toString() {
        return "Routins{" +
                "routins_ID=" + routins_ID +
                ", routine='" + routine + '\'' +
                ", paired_dev='" + paired_dev + '\'' +
                ", app='" + app + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    public static final String KEY_ACTION = "action";
    // property help us to keep data
    public int routins_ID;
    public String routine;
    public String paired_dev;
    public String app;
    public String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getRoutins_ID() {
        return routins_ID;
    }

    public void setRoutins_ID(int routins_ID) {
        this.routins_ID = routins_ID;
    }

    public String getRoutine() {
        return routine;
    }

    public void setRoutine(String routine) {
        this.routine = routine;
    }

    public String getPaired_dev() {
        return paired_dev;
    }

    public void setPaired_dev(String paired_dev) {
        this.paired_dev = paired_dev;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
