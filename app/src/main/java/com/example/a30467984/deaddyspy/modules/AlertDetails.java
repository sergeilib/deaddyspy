package com.example.a30467984.deaddyspy.modules;

/**
 * Created by 30467984 on 3/11/2019.
 */

public class AlertDetails {


    private int alertThreshold;
    private String alertUnit;
    private String alertStatus;
    private NotificationDetails notificationDetails;
    private NotificationDetails notificationDetails2;
    private NotificationDetails notificationDetails3;
    public int getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(int alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public String getAlertUnit() {
        return alertUnit;
    }

    public void setAlertUnit(String alertUnit) {
        this.alertUnit = alertUnit;
    }

    public String getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(String alertStatus) {
        this.alertStatus = alertStatus;
    }

    private String alertName;

    public NotificationDetails getNotificationDetails() {
        return notificationDetails;
    }

    public void setNotificationDetails(NotificationDetails notificationDetails) {
        this.notificationDetails = notificationDetails;
    }

    public NotificationDetails getNotificationDetails2() {
        return notificationDetails2;
    }

    public void setNotificationDetails2(NotificationDetails notificationDetails2) {
        this.notificationDetails2 = notificationDetails2;
    }

    public NotificationDetails getNotificationDetails3() {
        return notificationDetails3;
    }

    public void setNotificationDetails3(NotificationDetails notificationDetails3) {
        this.notificationDetails3 = notificationDetails3;
    }
    @Override
    public String toString() {
        return "AlertDetails{" +
                "alertName='" + alertName + '\'' +
                ", alertThreshold=" + alertThreshold +
                ", alertUnit='" + alertUnit + '\'' +
                ", alertStatus='" + alertStatus + '\'' +
                ", notificationDetails=" + notificationDetails +
                ", notificationDetails2=" + notificationDetails2 +
                ", notificationDetails3=" + notificationDetails3 +
                '}';
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }
}
