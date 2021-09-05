package com.example.a30467984.deaddyspy.utils;

import java.util.HashMap;

public class SingleToneServerListOfTrips {
    HashMap listOfTrips = new HashMap();


    private static final SingleToneServerListOfTrips ourInstance = new SingleToneServerListOfTrips();

    public static SingleToneServerListOfTrips getInstance() {
        return ourInstance;
    }

    private SingleToneServerListOfTrips() {
    }
    public HashMap getListOfTrips() {
        return listOfTrips;
    }

    public void setListOfTrips(HashMap listOfTrips) {
        this.listOfTrips = listOfTrips;
    }
}
