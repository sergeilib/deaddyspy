package com.example.a30467984.deaddyspy;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startSpeedometer(View view){

        Intent i = new Intent(this, ShowSpeedometer.class);

        // Starts TargetActivity
        startActivity(i);
    }

    public void startSettingsManager(View view){
        Intent i = new Intent(this, SettingsManagerActivity.class);

        // Starts TargetActivity
        startActivity(i);
    }
    /// Open
    public void showHistory(View view){
        Intent i = new Intent(this, HistoryManagerActivity.class);

        // Starts TargetActivity
        startActivity(i);
    }

}
