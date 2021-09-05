package com.example.a30467984.deaddyspy.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import static android.content.Context.WIFI_SERVICE;


public class MyDevice {
    private Context context;
    private Activity activity;
    private String myDevicePhone;
    private static final String PREF_MY_DADDY = "PREF_MY_DADDY";
    private static final String PREF_DEVICE_PHONE = "PREF_DEVICE_PHONE";
    private static final String SSID = "Mi Phone";

    public MyDevice(Context context,Activity activity){
        this.context = context;
        this.activity = activity;

    }

    public String getCountryDialCode(){
        String contryId = null;
        String contryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        contryId = telephonyMngr.getSimCountryIso().toUpperCase();
        String[] arrContryCode=context.getResources().getStringArray(R.array.DialingCountryCode);
        for(int i=0; i<arrContryCode.length; i++){
            String[] arrDial = arrContryCode[i].split(",");
            if(arrDial[1].trim().equals(contryId)){
                contryDialCode = arrDial[0];
                break;
            }
        }
        return contryDialCode;
    }

    public String getPhoneNumber() {
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);


        ///TelephonyManager mTelephony = null;
        if ((int) Build.VERSION.SDK_INT < 23) {
            //this is a check for build version below 23
            mTelephony = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);


        } else {
            //this is a check for build version above 23
            if (ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this.activity,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        101);

//                Log.e("INOF", " permission not granted");
//                if (ContextCompat.checkSelfPermission(this.getBaseContext(), Manifest.permission.READ_PHONE_STATE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    return null;
//                }else {
//                    mTelephony = (TelephonyManager) this.getBaseContext().getSystemService(
//                            Context.TELEPHONY_SERVICE);
//                }


            } else {
                mTelephony = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                Log.i("INFO", "If Permission is granted");
            }


        }
        if (mTelephony != null) {


            String country_code = mTelephony.getSimCountryIso();
            String networ_oper = mTelephony.getNetworkCountryIso();

            //String code = activity.getApplicationContext().getResources().getConfiguration().locale.getCountry();
            //Locale locale = Locale.getDefault();
            //String country_code_dd = locale.getCountry();
            String phone = mTelephony.getLine1Number();
            return mTelephony.getLine1Number();

        }else{
            return null;
        }

    }

    public String getFullPhoneNumber(){
        String phone = checkPhoneOnDisk();
        if (phone == null) {
            phone = getPhoneNumber();
            if (phone == null) {
                //phone = askForDevicePhoneNumber();
                return null;
            }
        }
        String dialCoode = getCountryDialCode();
        if (phone.startsWith(dialCoode)){
            return phone;
        }
        if(phone.startsWith("0")){
            phone = phone.replaceFirst("^0","");
        }
        return "+" + dialCoode + phone;
    }



    public String checkPhoneOnDisk(){
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_MY_DADDY, Context.MODE_PRIVATE);
        String phone = sharedPrefs.getString(PREF_DEVICE_PHONE, null);

        return phone;
    }

    public void savePhoneOnDisk(String phone){

        if (phone != null ) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_MY_DADDY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(PREF_DEVICE_PHONE, phone);
            editor.commit();

        }
    }

    public String getAppUUID(){
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }

    public static boolean turnOnHotSpot(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        //WifiConfiguration wifiConfiguration = new WifiConfiguration();

        //WifiConfiguration wifiConfiguration = null;
       // wifiConfiguration.SSID = "Mi Phone";
       // wifiManager.setWifiEnabled(true);

//        Method method;

        try {
            Method confMethod = wifiManager.getClass().getMethod("getWifiApConfiguration",null);
            WifiConfiguration wifiConfiguration = (WifiConfiguration) getWifiApConfiguration();

            // if WiFi is on, turn it off
            if(isApOn(context)) {
                wifiManager.setWifiEnabled(false);
            }
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            method.invoke(wifiManager, wifiConfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;



    }

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    public boolean setWifiApState(Context context) {
        //config = Preconditions.checkNotNull(config);
        try {
            Method method = null;
            WifiManager mWifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            //Method methodConf = mWifiManager.getClass().getDeclaredMethod("getWifiApConfiguration");
            //WifiConfiguration conf = (WifiConfiguration) methodConf.invoke(mWifiManager);

            if(isApOn(context))  {
                mWifiManager.setWifiEnabled(false);
            }
            //WifiConfiguration conf = getWifiApConfiguration();
            Method confMethod = mWifiManager.getClass().getMethod("getWifiApConfiguration",null);
            WifiConfiguration wifiConfiguration = (WifiConfiguration) getWifiApConfiguration();

            Method confSetMethod = mWifiManager.getClass().getMethod("setWifiApConfiguration",WifiConfiguration.class);
            //confSetMethod.invoke(mWifiManager,new Object[]{wifiConfiguration});
            //mWifiManager.addNetwork(conf);
            method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class,boolean.class);
            return (Boolean) method.invoke(mWifiManager, null, true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static WifiConfiguration getWifiApConfiguration() {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID =  SSID;
        conf.preSharedKey = "ser1ver1";

        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedAuthAlgorithms.set(0);
        return conf;
    }

    public void startTethering() {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mWifiManager != null) {
            int wifiState = mWifiManager.getWifiState();
            boolean isWifiEnabled = ((wifiState == WifiManager.WIFI_STATE_ENABLED) || (wifiState == WifiManager.WIFI_STATE_ENABLING));
            if (isWifiEnabled)
                mWifiManager.setWifiEnabled(false);
        }
        if (mConnectivityManager != null) {
            try {
                Field internalConnectivityManagerField = ConnectivityManager.class.getDeclaredField("mService");
                internalConnectivityManagerField.setAccessible(true);
                WifiConfiguration apConfig = new WifiConfiguration();
                apConfig.SSID = "Mi Phone";
                apConfig.preSharedKey = "ser1ver1";

                StringBuffer sb = new StringBuffer();
                Class internalConnectivityManagerClass = Class.forName("android.net.IConnectivityManager");
                ResultReceiver dummyResultReceiver = new ResultReceiver(null);
                try {

                    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
                    Method mMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                    mMethod.invoke(wifiManager, apConfig);
                    Method startTetheringMethod = internalConnectivityManagerClass.getDeclaredMethod("startTethering",
                            int.class,
                            ResultReceiver.class,
                            boolean.class);

                    startTetheringMethod.invoke(internalConnectivityManagerClass,
                            0,
                            dummyResultReceiver,
                            true);
                } catch (NoSuchMethodException e) {
                    Method startTetheringMethod = internalConnectivityManagerClass.getDeclaredMethod("startTethering",
                            int.class,
                            ResultReceiver.class,
                            boolean.class,
                            String.class);

                    startTetheringMethod.invoke(internalConnectivityManagerClass,
                            0,
                            dummyResultReceiver,
                            false,
                            context.getPackageName());
                } catch (InvocationTargetException e) {
                    sb.append(11 + (e.getMessage()));
                    e.printStackTrace();
                } finally {
                    //log.setText(sb.toString());
                }


            } catch (Exception e) {
                Log.e("Info", Log.getStackTraceString(e));
            }
        }
    }

    public boolean checkIfWifiConnected(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni!= null && ni.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }
}
