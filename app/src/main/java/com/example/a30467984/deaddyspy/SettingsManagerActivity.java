package com.example.a30467984.deaddyspy;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.CustomLanguageSpinnerAdapter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.nio.file.Paths.get;

/**
 * Created by 30467984 on 7/22/2018.
 */

public class SettingsManagerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    SettingsRepo settingsRepo = new SettingsRepo(this);
    String[] languages={"english","hebrew","russian"};
    String[] langShortCuts = {"en","iw","ru"};
    String currentLanguage = "english",currentLang;
    int flags[] = {R.drawable.british_flag, R.drawable.israel_flag, R.drawable.russian_flag};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_top_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SettingsRepo settingsRepo = new SettingsRepo(this);
        HashMap settingsList = settingsRepo.getSettingsList();
        //currentLanguage = getIntent().getStringExtra(currentLang);
        displaySettings(settingsList);
//        Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);
//        //speed_unit_sw.setOnClickListener(this);
//        speed_unit_sw.setOnC
    }

    public void displaySettings(HashMap settingsList){
        displayLanguageParams(settingsList);
        displaySpeedUnit(settingsList);
        displayRecordParam(settingsList);

    }

    public void updateScaleParam(View v) {
        Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);
        if (speed_unit_sw.isChecked()){
            settingsRepo.updateSettings("scale","km/h");
            Toast.makeText(SettingsManagerActivity.this,"Switch to " ,Toast.LENGTH_SHORT).show();

        }else{
            settingsRepo.updateSettings("scale","miles/h");
            Toast.makeText(SettingsManagerActivity.this,"Switch to miles/h",Toast.LENGTH_SHORT).show();
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    /////
    public void displayLanguageParams(HashMap settingsList){
        if(settingsList.containsKey("language")){
            Spinner spinner = (Spinner) findViewById(R.id.spinner_select_language);
            spinner.setOnItemSelectedListener(this);

            CustomLanguageSpinnerAdapter customAdapter=new CustomLanguageSpinnerAdapter(getApplicationContext(),flags,languages);
            spinner.setAdapter(customAdapter);

            currentLanguage = ((HashMap<String,String>)settingsList.get("language")).get("value");
            switch (currentLanguage){
                case "english":
                    spinner.setSelection(0);
                    break;

                case "hebrew":
                    spinner.setSelection(1);
                    break;

                case "russian":
                    spinner.setSelection(2);
                    break;
                default:
                    spinner.setSelection(0);
            }

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

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        //Toast.makeText(getApplicationContext(), languages[position], Toast.LENGTH_LONG).show();

        if(!currentLanguage.equals(languages[position])) {
            settingsRepo.updateSettings("language",languages[position]);
            Locale locale = new Locale(langShortCuts[position]);
            //Locale.setDefault(locale);
            Resources resources = getResources();
            Configuration configuration = new Configuration(resources.getConfiguration());
            if(Build.VERSION.SDK_INT >= 17) {
                configuration.setLocale(locale);
                getApplicationContext().createConfigurationContext(configuration);

            }else{
                configuration.locale = locale;
                resources.updateConfiguration(configuration,resources.getDisplayMetrics());
            }
//            resources.updateConfiguration(configuration);
            Intent refresh = new Intent(this,SettingsManagerActivity.class);
            refresh.putExtra(currentLang,languages[position]);
            startActivity(refresh);
            finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
