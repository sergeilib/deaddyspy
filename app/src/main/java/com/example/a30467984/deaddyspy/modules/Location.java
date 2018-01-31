package com.example.a30467984.deaddyspy.modules;

import java.sql.Date;

/**
 * Created by 30467984 on 1/28/2018.
 */

public class Location {
    private int id;
    private Double longitude;
    private Double latitude;
    private String name;
    private int speed;
    private int limit;
    private Date update_time;

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", name='" + name + '\'' +
                ", speed=" + speed +
                ", limit=" + limit +
                ", update_time=" + update_time +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
