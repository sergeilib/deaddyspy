package com.example.a30467984.deaddyspy.modules;

import android.graphics.drawable.Drawable;

/**
 * Created by 30467984 on 5/27/2019.
 */

public class AppList {
    private String name;
    Drawable icon;

    public AppList(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }
}
