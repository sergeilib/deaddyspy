package com.example.a30467984.deaddyspy.modules;

/**
 * Created by 30467984 on 3/11/2019.
 */

public class NotificationDetails {
    private String notificationName;
    private String sound;
    private String daddyNumber;
    private String email;
    private String sms;
    private String status;
    private String parent;

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getDaddyNumber() {
        return daddyNumber;
    }

    public void setDaddyNumber(String daddyNumber) {
        this.daddyNumber = daddyNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
