package com.example.a30467984.deaddyspy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.a30467984.deaddyspy.DAO.DatabaseHelper;
import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.gps.LocationData;

import java.util.HashMap;
import java.util.Map;

public class ShowSpeedometer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DatabaseHelper db = new DatabaseHelper(this);
        setContentView(R.layout.activity_show_speedometer);
        Settings settings = new Settings();
        SettingsRepo settingsRepo = new SettingsRepo(this);
//        settingsRepo.dropSettingsTable();
        RoutingRepo routingRepo = new RoutingRepo(this);
//        routingRepo.dropRoutingTable();
        int currentRideNum = routingRepo.getMaxTripNumber() + 1;
        HashMap settingsList = settingsRepo.getSettingsList();
        LocationData locationData = new LocationData(this,settingsList,this,currentRideNum);
        Point point = new Point();
        point.speed = locationData.getSpeed();
        point.latitude = locationData.getLatitude();
        point.longitude = locationData.getLongitude();
//        location.limit
        TextView textView_speed = (TextView) findViewById(R.id.speed_number);

        Double visual_speed = point.speed;

        if (settingsList.isEmpty() != true){
            /// if record params is checked , insert current point to DB
            if (settingsList.containsKey("record")) {
//                if (((HashMap<String,String>)settingsList.get("record")).get("value").equals("true")) {
////                RoutingRepo routingRepo = new RoutingRepo(this);
//                    routingRepo.insert(point);
//                }
            }
            String unit = "km/h";
            if(settingsList.containsKey("scale")){

                unit = ((HashMap<String,String>)settingsList.get("scale")).get("value");
                if (unit.equals("miles")){
                    visual_speed = point.speed * 1.6;
                }
            }
            TextView tv_unit = (TextView) findViewById(R.id.speed_unit);
            tv_unit.setText(unit);
        }
        //// DISPLAY SPEED
        textView_speed.setText("" + visual_speed.intValue());
    }
}
