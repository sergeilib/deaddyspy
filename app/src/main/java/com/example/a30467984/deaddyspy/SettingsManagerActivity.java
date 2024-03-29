package com.example.a30467984.deaddyspy;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Routins;
import com.example.a30467984.deaddyspy.DAO.RoutinsRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.background.BackgroundHandler;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.AlertDetails;
import com.example.a30467984.deaddyspy.modules.AppList;
import com.example.a30467984.deaddyspy.modules.CustomLanguageSpinnerAdapter;
import com.example.a30467984.deaddyspy.utils.MyDevice;

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
    private boolean DADDYSPY_BUTTON_CHANGE_FLAG = false;
    private boolean HOTSPPOT_BUTTON_CHANGE_FLAG = false;

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
        displaySharingLlocation(settingsList);
        displayBackupTrip(settingsList);
        displayBackupWifiOnly(settingsList);

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

    public void displaySharingLlocation(HashMap settingsList) {
        if (settingsList.containsKey("sharing_location")) {
            Switch sw = (Switch) findViewById(R.id.location_sharing_switch);

            String unit = ((HashMap<String, String>) settingsList.get("sharing_location")).get("value");
            if (unit.equals("true")) {
                sw.setChecked(true);

            } else {
                sw.setChecked(false);
            }
        }
    }

    public void updateSharingLocation(View v) {
        Switch sw = (Switch) findViewById(R.id.location_sharing_switch);
        MyDevice myDevice = new MyDevice(context, SettingsManagerActivity.this);
        if (sw.isChecked()) {
            if (myDevice.getFullPhoneNumber() != null) {

                settingsRepo.updateSettings("sharing_location", "true");
                BackgroundHandler.SETTINGS_CHANGE_FLAG = true;
                Toast.makeText(SettingsManagerActivity.this, "Allow sharing location", Toast.LENGTH_SHORT).show();
            } else {
                String phone = askForDevicePhoneNumber(myDevice);
                Toast.makeText(SettingsManagerActivity.this, "Phone not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            settingsRepo.updateSettings("sharing_location", "false");
            BackgroundHandler.SETTINGS_CHANGE_FLAG = true;
            Toast.makeText(SettingsManagerActivity.this, "Cancel sharing location", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayBackupTrip(HashMap settingsList) {
        if (settingsList.containsKey("trip_backup")) {
            Switch sw = (Switch) findViewById(R.id.trip_backup_history_switch);

            String unit = ((HashMap<String, String>) settingsList.get("trip_backup")).get("value");
            if (unit.equals("true")) {
                sw.setChecked(true);

            } else {
                sw.setChecked(false);
            }
        }
    }

    public void backupTripHistory(View v) {
        Switch sw = (Switch) findViewById(R.id.trip_backup_history_switch);

        if (sw.isChecked()) {


            settingsRepo.updateSettings("trip_backup", "true");
            BackgroundHandler.SETTINGS_CHANGE_FLAG = true;
            Toast.makeText(SettingsManagerActivity.this, "Allow trip history backup", Toast.LENGTH_SHORT).show();

        } else {
            settingsRepo.updateSettings("trip_backup", "false");
            BackgroundHandler.SETTINGS_CHANGE_FLAG = true;
            Toast.makeText(SettingsManagerActivity.this, "Cancel trip history backup", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayBackupWifiOnly(HashMap settingsList) {
        if (settingsList.containsKey("backup_wifi_only")) {
            Switch sw = (Switch) findViewById(R.id.backup_wifi_only_switch);

            String unit = ((HashMap<String, String>) settingsList.get("backup_wifi_only")).get("value");
            if (unit.equals("true")) {
                sw.setChecked(true);

            } else {
                sw.setChecked(false);
            }
        }
    }
    public void backupWifiOnlyUpdate(View v) {
        Switch sw = (Switch) findViewById(R.id.backup_wifi_only_switch);

        if (sw.isChecked()) {


            settingsRepo.updateSettings("backup_wifi_only", "true");
            BackgroundHandler.SETTINGS_CHANGE_FLAG = true;
            Toast.makeText(SettingsManagerActivity.this, "Allow backup wifi only", Toast.LENGTH_SHORT).show();

        } else {
            settingsRepo.updateSettings("backup_wifi_only", "false");
            BackgroundHandler.SETTINGS_CHANGE_FLAG = true;
            Toast.makeText(SettingsManagerActivity.this, "Cancel backup wifi only", Toast.LENGTH_SHORT).show();
        }
    }

    public String askForDevicePhoneNumber(final MyDevice myDevice) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
        alertDialog.setTitle("My Phone number");
        alertDialog.setMessage("Enter Phone number");

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //myDevicePhone = input.getText().toString();
                        //if (myDevicePhone.compareTo("") == 0) {

                        //}
                        myDevice.savePhoneOnDisk(input.getText().toString());
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

        //return myDevicePhone;
        return "blal";
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

    public void openAlertActivity(View view) {
        Intent i = new Intent(this, AlertManagerActivity.class);
        //i.putExtra("RideData", rideData);
        // Starts TargetActivity
        startActivity(i);
    }

    /////////////////////////////////////////////////////////////////////////
    /////// DEPENDANCY SECTON
    ////////////////////////////////
    public void openDependencyActivity(View view) {
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

                if (pairedDevicesDB != null && pairedDevices.equals(deviceName)) {
                    deviceName = deviceName + " (V)";
                }

                String deviceHardwareAddress = device.getAddress(); // MAC address
                pairedDevicesString.add(deviceName);
            }
        }
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dependency_configuration);
        //// in order dialog will not disapear in rotation , make orientation as portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /////////////////////////////////////////////////////////////////////
        ListView pairedlistView = (ListView) dialog.findViewById(R.id.ppaired_dependency_window_list);
        //GridView appDependListView = (GridView)dialog.findViewById(R.id.app_deppend_greedview);
        //dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        //       TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Android custom dialog example!");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pairedDevicesString) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
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
                if (!item.equals(pairedDevicesDB + " (V)")) {
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
        for (AppList app : installedApps) {
            appsImages[appCounter] = app.getIcon();
            appsNames[appCounter] = app.getName();
            appCounter++;//app.getIcon();
        }
        GridView appDependListView = (GridView) dialog.findViewById(R.id.app_deppend_greedview);
        ArrayAdapter<AppList> appsInstaledarrayAdapter = new ArrayAdapter<AppList>(this, android.R.layout.simple_list_item_1, installedApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
//                View view =super.getView(position,convertView,parent);
//                pairedDependencyLayoutParams = view.getLayoutParams();
//                pairedDependencyLayoutParams.height = 120;
//                view.setLayoutParams(pairedDependencyLayoutParams);
//                return view;
                ImageView imageView;
                if (convertView == null) {
                    imageView = new ImageView(context);
                    imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setPadding(8, 20, 8, 20);
                } else {
                    imageView = (ImageView) convertView;
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

        /////////////////////////////////////////////////////////////////////////////////
        /// config routine
        ///////////////////////////////////////////////////////////////////////////////
        final Button routineOpenDialogButton = (Button) dialog.findViewById(R.id.deendancy_routine_button);
        // if button is clicked, close the custom dialog
        routineOpenDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPairedDeviceName == null && selectedAppName == null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsManagerActivity.this);
                    builder1.setMessage("Please select one of the pared devices or application");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();


                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.routine_configuration);
                    final RoutinsRepo routinsRepo = new RoutinsRepo(context);
                    final HashMap<String, Routins> pairedDeviceRoutines = routinsRepo.getRoutineByPairedDev(selectedPairedDeviceName);
                    //dialog.setTitle("Title...");

                    // set the custom dialog components - text, image and button

                    TextView text = (TextView) dialog.findViewById(R.id.routine_selected_paired_dev);
                    text.setText(selectedPairedDeviceName);
                    dialog.show();
                    CheckBox start_daddyCB = (CheckBox) dialog.findViewById(R.id.checkBox_start_daddyspy);

                    CheckBox start_hotspotCB = (CheckBox) dialog.findViewById(R.id.checkbox_start_hotspot);

                    if (pairedDeviceRoutines != null) {
                        for (String action : pairedDeviceRoutines.keySet()) {
                            switch (action) {
                                case "hotspot":
                                    start_hotspotCB.setChecked(true);
                                    break;
                                case "daddy_spy":
                                    start_daddyCB.setChecked(true);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    start_daddyCB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //XOR
                            DADDYSPY_BUTTON_CHANGE_FLAG = DADDYSPY_BUTTON_CHANGE_FLAG ^ true;
                        }
                    });

                    start_hotspotCB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // XOR
                            HOTSPPOT_BUTTON_CHANGE_FLAG = HOTSPPOT_BUTTON_CHANGE_FLAG ^ true;
                        }
                    });
                    ///////////////////////////////////////////////
                    // Oppened dialog box
                    ////////////////////////////////////////////////
                    final Button routineDialogButtonSave = (Button) dialog.findViewById(R.id.button_save_routine);
                    final Button routineDialogButtonCancel = (Button) dialog.findViewById(R.id.button_cancel_routine);
                    routineDialogButtonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Routins routins = new Routins();

                            routins.setPaired_dev(selectedPairedDeviceName);
                            CheckBox start_daddyCB = (CheckBox) dialog.findViewById(R.id.checkBox_start_daddyspy);
                            start_daddyCB.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            CheckBox start_hotspotCB = (CheckBox) dialog.findViewById(R.id.checkbox_start_hotspot);

                            if (selectedAppName != null) {
                                routins.setApp(selectedAppName);
                            }
                            if (selectedPairedDeviceName != null) {
                                routins.setPaired_dev(selectedPairedDeviceName);
                            }

                            /////////////////////////////////////////////////////////
                            /// paired device handle hotspot
                            if (HOTSPPOT_BUTTON_CHANGE_FLAG) {
                                if (start_hotspotCB.isChecked()) {
                                    routins.setAction("hotspot");
                                    routinsRepo.insert(routins);

                                } else {
                                    routinsRepo.deleteByID(pairedDeviceRoutines.get("hotspot").getRoutins_ID());
                                    //routinsRepo.deleteByName;
                                }
                                HOTSPPOT_BUTTON_CHANGE_FLAG = false;
                            }
                            //////////////////////////////////////////////////////////////////////
                            ///////////////////////////////////////////////////////////////////
                            ////  paired device handle start daddy spy
                            if (DADDYSPY_BUTTON_CHANGE_FLAG) {
                                if (start_daddyCB.isChecked()) {
                                    routins.setAction("daddy_spy");
                                    routinsRepo.insert(routins);
                                } else {
                                    routinsRepo.deleteByID(pairedDeviceRoutines.get("daddy_spy").getRoutins_ID());
                                }
                                DADDYSPY_BUTTON_CHANGE_FLAG = false;
                            }
                            ////////////////////////////////////////////
                            dialog.dismiss();
                        }
                    });
                    routineDialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                }
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
                if (!selectedPairedDeviceName.equals(pairedDevicesDB + " (V)")) {
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
                /// AFTER CLOSE DIALOG RETURN TO REGULAR ORIENTATION
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                dialog.dismiss();
            }
        });

        /////////////////////////
        dialog.show();

    }

//    @Override
//    public void onShow(DialogInterface dialog) {
//    }

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

    private void initializateDependancyParams(HashMap settingsList) {
        if (settingsList.containsKey("paired_depend")) {
            pairedDevicesDB = ((HashMap<String, String>) settingsList.get("paired_depend")).get("value");
        } else {
            pairedDevicesDB = null;
        }
        if (settingsList.containsKey("app_depend")) {
            pairedDevicesDB = ((HashMap<String, String>) settingsList.get("app_depend")).get("value");
        } else {
            pairedDevicesDB = null;
        }
    }
}