package com.example.a30467984.deaddyspy.utils;

/**
 * Created by 30467984 on 7/22/2019.
 */

public class SingleToneAuthToen {
    String token;


    private static final SingleToneAuthToen ourInstance = new SingleToneAuthToen();

    public static SingleToneAuthToen getInstance() {
        return ourInstance;
    }

    private SingleToneAuthToen() {
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
