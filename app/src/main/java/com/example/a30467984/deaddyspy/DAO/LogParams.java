package com.example.a30467984.deaddyspy.DAO;

/**
 * Created by 30467984 on 3/19/2019.
 */

public class LogParams {
    public static final String TABLE = "AlertLog";

    public static final String KEY_ID = "id";
    public static final String KEY_date = "date";
    public static final String KEY_alert_name = "alertname";
    public static final String KEY_method = "method";
    public static final String KEY_destination = "destination";
    public static final String KEY_message = "message";
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String date;
    private String method;
    private String destination;
    private String message;
    private String alertName;

    public String getDate() {
        return date;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
