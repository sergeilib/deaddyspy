package com.example.a30467984.deaddyspy.gps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.ShowSpeedometer;

import java.util.HashMap;

/**
 * Created by 30467984 on 1/5/2018.
 */

public class LocationData extends Activity implements LocationListener {

    private final Context mContext;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    Location loc;
    double latitude;
    double longitude;
    double speed;
    double accuracy;
    int currentRideNum;
    int counter;
    String placeName;
    String scale = "km";
    HashMap settingsList;
    Activity activity;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;


    private static final long MIN_TIME_BW_UPDATES = 1000 * 1;
    protected LocationManager locationManager;

    public LocationData(Context mContext, HashMap settingsList, Activity activity,int currentRideNum) {
        this.mContext = mContext;
        this.settingsList = settingsList;
        this.activity = activity;
        this.counter = 1;
        this.currentRideNum = currentRideNum;
        getLocation();
    }

    private Location getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {

                    if (ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(mContext,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(activity, new String[]
                                {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                124);


                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }
                    }


                }


                if (checkNetwork) {


                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }

                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return loc;
    }

    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    public double getSpeed() {
        if (loc != null) {
            speed = loc.getSpeed();
        }
        return speed;
    }
    public double getAccuracy(){
        if(loc != null){
            accuracy = loc.getAccuracy();
        }
        return accuracy;
    }
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Do you want to turn on GPS?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }


    public void stopListener() {
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(LocationData.this);
        }
    }

    // @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
      //  setContentView(R.layout.activity_show_speedometer);
        Settings settings = new Settings();
        SettingsRepo settingsRepo = new SettingsRepo(this);
        //settingsList = settingsRepo.getSettingsList();
        RoutingRepo routingRepo = new RoutingRepo(mContext);
        Point point = new Point();
        point.speed = (double) location.getSpeed() * 3600 / 1000;
        point.latitude = location.getLatitude();
        point.longitude = location.getLongitude();
        point.trip_number = this.currentRideNum;
        point.date = routingRepo.getDateTime();

        accuracy = this.getAccuracy();
//        location.limit
        TextView textView_speed = (TextView) activity.findViewById(R.id.speed_number);
        // textView_speed.setText("" + point.speed);
        String pointParamsString = "Speed: " + point.speed + "\nLatitude: " + point.latitude + "\nLongitude: " + point.longitude +
                "\nAccuracy: " + String.format("%.2f",accuracy) + "\n Counter: " + this.counter++;
        TextView tv_pointParams = (TextView) activity.findViewById(R.id.low_textView_location);
        tv_pointParams.setText(pointParamsString);
        Double visual_speed = point.speed;
        if (settingsList.isEmpty() != true){
            /// if record params is checked , insert current point to DB
            if (settingsList.containsKey("record")) {
                if (((HashMap<String,String>)settingsList.get("record")).get("value").equals("true") && point.speed > 0) {
//                RoutingRepo routingRepo = new RoutingRepo(this);
                    try {
                        routingRepo.insert(point);
                    }catch (NullPointerException e){
                        Log.i("INFO",e.toString());
                    }
                }
            }
            String unit = "km/h";
            if(settingsList.containsKey("scale")){

                unit = ((HashMap<String,String>)settingsList.get("scale")).get("value");
                if (unit.equals("miles")){
                    visual_speed = point.speed * 1.6;
                }
            }
            TextView tv_unit = (TextView) activity.findViewById(R.id.speed_unit);
            tv_unit.setText(unit);
        }
        textView_speed.setText("" + visual_speed.intValue());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
