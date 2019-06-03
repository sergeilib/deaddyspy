package com.example.a30467984.deaddyspy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.SettingsRepo;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static boolean CHANGE_FLAG = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsRepo settingsRepo = new SettingsRepo(this);
        checkLanguage(settingsRepo);

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
    @Override
    protected void onResume(){
        super.onResume();
        /// if language was changed in settings activity ,should be changed in mainActivity allsow
        if(CHANGE_FLAG == true) {
            SettingsRepo settingsRepo = new SettingsRepo(this);
            checkLanguage(settingsRepo);
            //Intent refresh = new Intent(this,MainActivity.class);
            //refresh.putExtra("language",languages[position]);
            //startActivity(refresh);
            //finish();
            setContentView(R.layout.activity_main);
        }else{
            CHANGE_FLAG = false;
        }
    }
    public void checkLanguage(SettingsRepo settingsRepo){
        String db_langwage = ((HashMap<String,String>)(settingsRepo.getSettingsList()).get("language")).get("value");
        //String unit = ((HashMap<String,String>)settingsList.get("scale")).get("value");
        Locale locale;
        Resources res = getResources();
        if (db_langwage != null){
            switch (db_langwage){
                case "english":
                    locale = new Locale("en");
                    break;
                case "hebrew":
                    locale = new Locale("iw");
                    break;
                case"russian":
                    locale = new Locale("ru");
                    break;
                default:
                    locale = new Locale("en");
            }

            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, dm);
        }else {
            String lang = Locale.getDefault().getLanguage();
            locale = new Locale(lang);
        }

        Configuration conf = getBaseContext().getResources().getConfiguration();
        if(Build.VERSION.SDK_INT >= 17) {

            conf.setLocale(locale);
            getApplicationContext().createConfigurationContext(conf);

        }else{
            conf.locale = locale;
            getBaseContext().getResources().updateConfiguration(conf,getBaseContext().getResources().getDisplayMetrics());
        }

    }
}
