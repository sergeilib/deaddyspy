package com.example.a30467984.deaddyspy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.gps.LocationData;

import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Paths.get;

/**
 * Created by 30467984 on 7/22/2018.
 */

public class SettingsManagerActivity extends AppCompatActivity {
    SettingsRepo settingsRepo = new SettingsRepo(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_top_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SettingsRepo settingsRepo = new SettingsRepo(this);
        HashMap settingsList = settingsRepo.getSettingsList();
        displaySettings(settingsList);
//        Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);
//        //speed_unit_sw.setOnClickListener(this);
//        speed_unit_sw.setOnC
    }

    public void displaySettings(HashMap settingsList){
        displaySpeedUnit(settingsList);
        displayRecordParam(settingsList);
    }

    public void updateScaleParam(View v) {
        Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);
        if (speed_unit_sw.isChecked()){
            settingsRepo.updateSettings("scale","km");
            Toast.makeText(SettingsManagerActivity.this,"Switch to km/h",Toast.LENGTH_SHORT).show();

        }else{
            settingsRepo.updateSettings("scale","miles");
            Toast.makeText(SettingsManagerActivity.this,"Switch to miles/h",Toast.LENGTH_SHORT).show();
        }

    }


    public void displaySpeedUnit(HashMap settingsList){
        if(settingsList.containsKey("scale")){
            Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);

            String unit = ((HashMap<String,String>)settingsList.get("scale")).get("value");
            if (unit.equals("km")){
                speed_unit_sw.setChecked(true);
            }else{
                speed_unit_sw.setChecked(false);
            }
        }
    }

    public void displayRecordParam(HashMap settingsList){
        if(settingsList.containsKey("record")){
            Switch record_sw = (Switch) findViewById(R.id.record_switch);

            String unit = ((HashMap<String,String>)settingsList.get("record")).get("value");
            if (unit.equals("true")){
                record_sw.setChecked(true);
            }else{
                record_sw.setChecked(false);
            }
        }
    }

    public void updateRecordParam(View v){
        Switch record_sw = (Switch) findViewById(R.id.record_switch);
        if (record_sw.isChecked()){
            settingsRepo.updateSettings("record","true");
            Toast.makeText(SettingsManagerActivity.this,"Switch to Record",Toast.LENGTH_SHORT).show();

        }else{
            settingsRepo.updateSettings("record","false");
            Toast.makeText(SettingsManagerActivity.this,"Cancel Record",Toast.LENGTH_SHORT).show();
        }
    }
}
