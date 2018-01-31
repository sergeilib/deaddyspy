package com.example.a30467984.deaddyspy;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.a30467984.deaddyspy.gps.LocationData;

public class ShowSpeedometer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_speedometer);
        LocationData locationData = new LocationData(this);
        TextView textView_speed = (TextView) findViewById(R.id.speed_number);
        textView_speed.setText("" + locationData.getSpeed());
    }
}
