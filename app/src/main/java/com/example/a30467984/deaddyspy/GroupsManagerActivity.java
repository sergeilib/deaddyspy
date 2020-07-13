package com.example.a30467984.deaddyspy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.gps.LocationData;

import java.util.HashMap;

public class GroupsManagerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DatabaseHelper db = new DatabaseHelper(this);
        setContentView(R.layout.groups_configuration);
        Settings settings = new Settings();
        SettingsRepo settingsRepo = new SettingsRepo(this);
//        settingsRepo.dropSettingsTable();

        HashMap settingsList = settingsRepo.getSettingsList();

    }

}
