package com.example.a30467984.deaddyspy;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.a30467984.deaddyspy.modules.AlertDetails;
import com.example.a30467984.deaddyspy.modules.AppList;
import com.example.a30467984.deaddyspy.modules.CustomLanguageSpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.nio.file.Paths.get;

/**
 * Created by 30467984 on 7/22/2018.
 */

public class SettingsManagerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SettingsRepo settingsRepo = new SettingsRepo(this);
    private String[] languages = {"english", "hebrew", "russian"};
    private String[] langShortCuts = {"en", "iw", "ru"};
    private String currentLanguage, currentLang;
    private String pairedDevicesDB;
    private String appDependancyDB;
    private String selectedPairedDeviceName;
    private String selectedAppName;

    int flags[] = {R.drawable.british_flag, R.drawable.israel_flag, R.drawable.russian_flag};
    final Context context = this;
    private ViewGroup.LayoutParams pairedDependencyLayoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Intent i = getIntent();
        //ArrayList rideData = (ArrayList) i.getSerializableExtra();
        setContentView(R.layout.settings_top_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SettingsRepo settingsRepo = new SettingsRepo(this);
        HashMap settingsList = settingsRepo.getSettingsList();
        //currentLanguage = getIntent().getStringExtra(currentLang);
        displaySettings(settingsList);
        initializateDependancyParams(settingsList);
//        Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);
//        //speed_unit_sw.setOnClickListener(this);
//        speed_unit_sw.setOnC
    }

    public void displaySettings(HashMap settingsList) {
        displayLanguageParams(settingsList);
        displaySpeedUnit(settingsList);
        displayRecordParam(settingsList);

    }

    public void updateScaleParam(View v) {
        Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);
        if (speed_unit_sw.isChecked()) {
            settingsRepo.updateSettings("scale", "km/h");
            Toast.makeText(SettingsManagerActivity.this, "Switch to ", Toast.LENGTH_SHORT).show();

        } else {
            settingsRepo.updateSettings("scale", "miles/h");
            Toast.makeText(SettingsManagerActivity.this, "Switch to miles/h", Toast.LENGTH_SHORT).show();
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////
    /////
    public void displayLanguageParams(HashMap settingsList) {
        if (settingsList.containsKey("language")) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner_select_language);
            spinner.setOnItemSelectedListener(this);

            CustomLanguageSpinnerAdapter customAdapter = new CustomLanguageSpinnerAdapter(getApplicationContext(), flags, languages);
            spinner.setAdapter(customAdapter);

            currentLanguage = ((HashMap<String, String>) settingsList.get("language")).get("value");
            switch (currentLanguage) {
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

    public void displaySpeedUnit(HashMap settingsList) {
        if (settingsList.containsKey("scale")) {
            Switch speed_unit_sw = (Switch) findViewById(R.id.speed_unit_switch);

            String unit = ((HashMap<String, String>) settingsList.get("scale")).get("value");
            if (unit.equals("km")) {
                speed_unit_sw.setChecked(true);
            } else {
                speed_unit_sw.setChecked(false);
            }
        }
    }

    public void displayRecordParam(HashMap settingsList) {
        if (settingsList.containsKey("record")) {
            Switch record_sw = (Switch) findViewById(R.id.record_switch);

            String unit = ((HashMap<String, String>) settingsList.get("record")).get("value");
            if (unit.equals("true")) {
                record_sw.setChecked(true);
            } else {
                record_sw.setChecked(false);
            }
        }
    }

    public void updateRecordParam(View v) {
        Switch record_sw = (Switch) findViewById(R.id.record_switch);
        if (record_sw.isChecked()) {
            settingsRepo.updateSettings("record", "true");
            Toast.makeText(SettingsManagerActivity.this, "Switch to Record", Toast.LENGTH_SHORT).show();

        } else {
            settingsRepo.updateSettings("record", "false");
            Toast.makeText(SettingsManagerActivity.this, "Cancel Record", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        //Toast.makeText(getApplicationContext(), languages[position], Toast.LENGTH_LONG).show();

        if (!currentLanguage.equals(languages[position])) {
            settingsRepo.updateSettings("language", languages[position]);
            Locale locale = new Locale(langShortCuts[position]);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, dm);
            //Locale.setDefault(locale);
            //Resources resources = getResources();
            //Configuration configuration = new Configuration(resources.getConfiguration());
            //Configuration conf = getBaseContext().getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= 17) {

                conf.setLocale(locale);
                getApplicationContext().createConfigurationContext(conf);

            } else {
                conf.locale = locale;
                getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
            }
            currentLang = langShortCuts[position];
            //Intent mainInent = new Intent(this,MainActivity.class);
            //startActivity(mainInent);
//            resources.updateConfiguration(configuration);
            MainActivity.CHANGE_FLAG = true;
            Intent refresh = new Intent(this, SettingsManagerActivity.class);
            //refresh.putExtra("language",languages[position]);
            startActivity(refresh);
            finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void openAlertActivity(View view){
        Intent i = new Intent(this, AlertManagerActivity.class);
        //i.putExtra("RideData", rideData);
        // Starts TargetActivity
        startActivity(i);
    }
    /////////////////////////////////////////////////////////////////////////
    /////// DEPENDANCY SECTON
    ////////////////////////////////
    public void openDependencyActivity(View view){
        // Intent i = new Intent(this, AlertManagerActivity.class);
        //i.putExtra("RideData", rideData);
        // Starts TargetActivity
        // startActivity(i);

        ////////////////////////////////////////////////////////////////////////////
        /// bluetooth paired devices  depeendancy section
        /////////////////////////////////////////////////////////////////////////////
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();

        ArrayList<String> pairedDevicesString = new ArrayList();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();

                if (pairedDevicesDB != null && pairedDevices.equals(deviceName)){
                    deviceName = deviceName + " (V)";
                }

                String deviceHardwareAddress = device.getAddress(); // MAC address
                pairedDevicesString.add(deviceName);
            }
        }
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dependency_configuration);
            ListView pairedlistView = (ListView)dialog.findViewById(R.id.ppaired_dependency_window_list);
            //GridView appDependListView = (GridView)dialog.findViewById(R.id.app_deppend_greedview);
            //dialog.setTitle("Title...");

            // set the custom dialog components - text, image and button
            //       TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Android custom dialog example!");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  pairedDevicesString){
            @Override
            public View getView (int position, View convertView, ViewGroup parent){
                View view =super.getView(position,convertView,parent);
                pairedDependencyLayoutParams = view.getLayoutParams();
                pairedDependencyLayoutParams.height = 120;
                view.setLayoutParams(pairedDependencyLayoutParams);
                return view;
            }
        };

        pairedlistView.setAdapter(arrayAdapter);

        pairedlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                if (!item.equals(pairedDevicesDB+ " (V)")) {
                    selectedPairedDeviceName = item;
                    Toast.makeText(context, item + "", Toast.LENGTH_SHORT).show();
                    //settingsRepo.updateSettings("paired_depend", item);
//                AlertDetails alertDetails = fetchAlertDetails(item);
                    //         Map curentAlertParams = alertRepo.getAlertParamsByName(item);
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////
        /// APP depeendancy section
        /////////////////////////////////////////////////////////////////////////////
        List<AppList> installedApps = getInstalledApps();
        //Integer[] appsImages = new Integer[installedApps.size()];
        final Drawable[] appsImages = new Drawable[installedApps.size()];
        final String[] appsNames = new String[installedApps.size()];
        int appCounter = 0;
        for(AppList app: installedApps){
            appsImages[appCounter] = app.getIcon();
            appsNames[appCounter] = app.getName();
            appCounter++;//app.getIcon();
        }
        GridView appDependListView = (GridView)dialog.findViewById(R.id.app_deppend_greedview);
        ArrayAdapter<AppList> appsInstaledarrayAdapter = new ArrayAdapter<AppList>(this, android.R.layout.simple_list_item_1,  installedApps){
            @Override
            public View getView (int position, View convertView, ViewGroup parent){
//                View view =super.getView(position,convertView,parent);
//                pairedDependencyLayoutParams = view.getLayoutParams();
//                pairedDependencyLayoutParams.height = 120;
//                view.setLayoutParams(pairedDependencyLayoutParams);
//                return view;
                ImageView imageView;
                if(convertView==null) {
                    imageView = new ImageView(context);
                    imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setPadding(8, 20, 8, 20);
                }else{
                    imageView=(ImageView)convertView;
                }
                imageView.setImageDrawable(appsImages[position]);
                return imageView;
            }
        };
        appDependListView.setAdapter(appsInstaledarrayAdapter);
        appDependListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position).toString();
   //             final String item = (String) parent.getSelectedItem();
                String selectedAppName = appsNames[position];
                Toast.makeText(context, selectedAppName + "", Toast.LENGTH_SHORT).show();
                //settingsRepo.updateSettings("app_depend", selectedAppName);
//                AlertDetails alertDetails = fetchAlertDetails(item);
                //         Map curentAlertParams = alertRepo.getAlertParamsByName(item);

            }
        });

        ////////////////////////////////////////////////
        ///// SAVE DEPENDANCY
        ////////////////////////////////////////////////////////////////////////////
        final Button dependDialogButtonSave = (Button) dialog.findViewById(R.id.dependencyButtonOK);
        // if button is clicked, close the custom dialog
        dependDialogButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedPairedDeviceName.equals(pairedDevicesDB+ " (V)")) {
                    settingsRepo.updateSettings("paired_depend", selectedPairedDeviceName);

                }
                if (!selectedAppName.equals(appDependancyDB)) {
                    settingsRepo.updateSettings("app_depend", selectedAppName);

                }
                dialog.dismiss();
            }
        });
        final Button dependDialogButtonCancel = (Button) dialog.findViewById(R.id.dependency_cancel_button);
        // if button is clicked, close the custom dialog
        dependDialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        /////////////////////////
        dialog.show();

    }

    private List<AppList> getInstalledApps() {
        List<AppList> res = new ArrayList<AppList>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((isSystemPackage(p) == false)) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                res.add(new AppList(appName, icon));
            }
        }
        return res;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }
    private void initializateDependancyParams(HashMap settingsList){
        if (settingsList.containsKey("paired_depend")) {
            pairedDevicesDB = ((HashMap<String, String>) settingsList.get("paired_depend")).get("value");
        }else{
            pairedDevicesDB = null;
        }
        if (settingsList.containsKey("app_depend")) {
            pairedDevicesDB = ((HashMap<String, String>) settingsList.get("app_depend")).get("value");
        }else{
            pairedDevicesDB = null;
        }
    }
}