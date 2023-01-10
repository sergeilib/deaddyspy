package com.example.a30467984.deaddyspy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.a30467984.deaddyspy.DAO.DatabaseHelper;
import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.gps.LocationData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShowSpeedometer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DatabaseHelper db = new DatabaseHelper(this);
        setContentView(R.layout.activity_show_speedometer);
        //// this activity will be allways portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Settings settings = new Settings();
        SettingsRepo settingsRepo = new SettingsRepo(this);
//        settingsRepo.dropSettingsTable();
        RoutingRepo routingRepo = new RoutingRepo(this);
//        routingRepo.dropRoutingTable();
        /////////////////////////////////////////////////////////////////
        // if interval from the last update less than 10 min so proceed with last trip number
        ///////////////////////////////////////////////////////////////////////////
        int offset = 1;
        if (getTimeInterval(routingRepo.getLastUpdateDate()) < 600){
            offset = 0;
        }
        ///////////////////////////////////////////////////////////////////
        int currentRideNum = routingRepo.getMaxTripNumber() + offset;
        HashMap settingsList = settingsRepo.getSettingsList();
        LocationData locationData = new LocationData(this,settingsList,this,currentRideNum);
        Point point = new Point();
        point.speed = locationData.getSpeed();
        point.latitude = locationData.getLatitude();
        point.longitude = locationData.getLongitude();
//        location.limit
        TextView textView_speed = (TextView) findViewById(R.id.speed_number);

        int visual_speed = point.speed;

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
                    visual_speed = (int) (point.speed * 1.6);
                }
            }
            TextView tv_unit = (TextView) findViewById(R.id.speed_unit);
            tv_unit.setText(unit);
        }
        //// DISPLAY SPEED
        textView_speed.setText("" + visual_speed);
    }

    public long getTimeInterval(String lastUpdateDate) {
        if (lastUpdateDate == null){
            return 100000;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {

            Date lastUpdate = dateFormat.parse(lastUpdateDate);
            Date curDate = dateFormat.parse(dateFormat.format(new Date()));

            long interval_in_sec =  curDate.getTime() - lastUpdate.getTime();
            return  interval_in_sec;
        }catch (Exception e){
            return 1000000;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    
}
