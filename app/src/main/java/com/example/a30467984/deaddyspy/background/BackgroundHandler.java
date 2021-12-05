package com.example.a30467984.deaddyspy.background;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.ArraySet;
import android.util.Log;

import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Routins;
import com.example.a30467984.deaddyspy.DAO.RoutinsRepo;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.Server.ServerConnection;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.ConnectionResponse;
import com.example.a30467984.deaddyspy.utils.AccessPointManager;
import com.example.a30467984.deaddyspy.utils.MyDevice;
import com.example.a30467984.deaddyspy.utils.SingleToneAuthToen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class BackgroundHandler {
        private int interval;
        private Context context;
        private LocationData locationData;
        private Activity activity;
        private String uuid;
        private Handler handler = new Handler();
        private Runnable runnable;
        private String path = "https://li780-236.members.linode.com:443/api/";
        private ServerConnection serverConnection;
        private ConnectionResponse connectionResponse;
        private final HashMap settingList;
        private MyDevice myDevice;
        public static boolean SETTINGS_CHANGE_FLAG = false;
        private static int BACKUP_FLAG = 0;

        public BackgroundHandler(Context context, Activity activity ,int interval,String uuid){
            connectionResponse = new ConnectionResponse();
            this.context = context;
            this.interval = interval;
            this.activity = activity;
            serverConnection = new ServerConnection(this.context,this.activity);
            this.uuid = uuid;
            settingList = getSettings();
            locationData = new LocationData(context,settingList,activity,1);
            myDevice = new MyDevice(context,activity);
        }

        public void waitingHandler(){
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(runnable,interval);
                    checkSharingLlocation(settingList);
                    //checkPairedDevicesConnected();
                    checkIfPairedDeviceConected();
                    checkTripBackup(settingList);
                }
            },interval);

        }

        public void jobsHandler(){
            //boolean hotspotOn = myDevice.turnOnHotSpot(context);
            //boolean hotspot = myDevice.setWifiApState(context);
            boolean hotspot = AccessPointManager.openWifiAp(context,"Mi Phone","ser1ver1");
            //myDevice.startTethering();
            checkSharingLlocation(settingList);
            //checkPairedDevicesConnected();
            checkIfPairedDeviceConected();
        }

        public HashMap getSettings(){
            SettingsRepo settingsRepo = new SettingsRepo(this.context);
            HashMap settingsList = settingsRepo.getSettingsList();
            return settingsList;
        }
        ////////////////////////////////////////////////////////////////////////////////
        ///// method check if trip history should be backuped,
        //// check if connection should be wifi only and perform backup
        //////////////////////////////////////////////////////////////////////////////////
        private void checkTripBackup(HashMap settingList) {
            if (settingList.containsKey("trip_backup")) {
                if (SETTINGS_CHANGE_FLAG == true){
                    settingList = getSettings();
                    SETTINGS_CHANGE_FLAG = false;
                }

                String ifTripBackup = ((HashMap<String, String>) settingList.get("trip_backup")).get("value");
                if (ifTripBackup.equals("true")) {
                    String ifBackupWifitrue = ((HashMap<String, String>) settingList.get("backup_wifi_only")).get("value");
                    if (ifBackupWifitrue.equals("true")){
                        if (!checkConnectivityType().equals("WIFI")){
                             Log.i("INFO","Can't backup trip data,no WIFI connection detected ");
                             return;
                        }
                    }
                    RoutingRepo routingRepo = new RoutingRepo(context);
                    ArrayList<HashMap<String, String>> backupData = routingRepo.getLocationListByBackupFlag(BACKUP_FLAG);
                   // ArrayList<HashMap<String, String>> backupData = routingRepo.getStartingLocationList();
                    if(backupData.size() == 0){
                        Log.i("INFO","Nothing to backup on server");
                        return;
                    }
                    JSONObject jsonObject= new JSONObject();
                    JSONArray innerJsonArray = new JSONArray();
                    String android_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    HashMap keyHash = getTripHistoryFields();
                    for (HashMap row: backupData) {
                        Iterator<Map.Entry<String,String>> it = row.entrySet().iterator();
                        JSONObject rowJsonObj = new JSONObject();
                        while (it.hasNext()){
                            Map.Entry<String,String> pair = it.next();
                            try {
                                String key = pair.getKey();
                                if (keyHash.get(key) != null) {
                                    rowJsonObj.put(keyHash.get(key).toString(), pair.getValue());
                                } else{
                                    continue;
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        innerJsonArray.put(rowJsonObj);
                    }
                    try {
                        jsonObject.put("data",innerJsonArray);
                        //jsonObject.put("android_id",android_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    String phone = myDevice.getFullPhoneNumber();


                        String url_suffix = "location/update_trip_history?android_id=" +  android_id;
                        Object object = prepareConnectionObject(url_suffix,jsonObject);

                        if (object != null) {
                            try {
                                updateServerTripBackup(object);
                            } catch (Exception e) {
                                Log.i("ERROR", "CHeckSharingLoocation" + e.getMessage());
                            }
                        } else {
                            Log.i("INFO", "The token is NULL, Can't update location");
                        }


                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////
        //// this method check if option "share location" is checked in settings,
        //// and pass current location to server
        ///////////////////////////////////////////////////////////////////////////
        public void checkSharingLlocation(HashMap settingList) {
            if (settingList.containsKey("sharing_location")) {
                if (SETTINGS_CHANGE_FLAG == true){
                    settingList = getSettings();
                    SETTINGS_CHANGE_FLAG = false;
                }
                String ifShareLocation = ((HashMap<String, String>) settingList.get("sharing_location")).get("value");
                if (ifShareLocation.equals("true")) {
                    String phone = myDevice.getFullPhoneNumber();
                    Location location = getLocationWithCheckNetworkAndGPS(context);
                    Double longitude = location.getLongitude();
                    Double latitude = location.getLatitude();
                    if (longitude > 0 && latitude > 0){
                        String android_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                        String url_suffix = "location/sharing_location?phone=" + phone + "&android_id=" +  android_id + "&last_longitude=" + longitude +
                                "&last_latitude=" + latitude;
                        Object object = prepareConnectionObject(url_suffix,null);
                        if(object != null) {
                            try {
                                updateServerSharingLlocation(object);
                            } catch (Exception e) {
                                Log.i("ERROR", "CHeckSharingLoocation" + e.getMessage());
                            }
                        }else {
                            Log.i("INFO", "The token is NULL, Can't update location");
                        }
                    }

                }
            }
        }

    public static Location getLocationWithCheckNetworkAndGPS(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location networkLoacation = null, gpsLocation = null, finalLoc = null;
        if (isGpsEnabled)
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkLocationEnabled)
            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null && networkLoacation != null) {

            //smaller the number more accurate result will
            if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy())
                return finalLoc = networkLoacation;
            else
                return finalLoc = gpsLocation;

        } else {

            if (gpsLocation != null) {
                return finalLoc = gpsLocation;
            } else if (networkLoacation != null) {
                return finalLoc = networkLoacation;
            }
        }
        return finalLoc;
    }

    public Object prepareConnectionObject(String url_suffix,JSONObject body){
        Object[] object = new Object[2];
        URL url = null;
        try {
            url = new URL(path + url_suffix);
        }catch (MalformedURLException m){
            Log.i("ERR",m.getMessage());
        }
        object[0] = url;

        HashMap<String,String> params = new HashMap<>();

        String tkn = getSessionToken();
        if (tkn == null){
            //ServerConnection serverConnection = new ServerConnection(context, activity);
            //serverConnection.getAuthRequest(object);
            return null;
        }
        params.put("token", tkn);
        params.put("method", "POST");
        if(body != null) {
            try {
                params.put("data", body.get("data").toString());
                //params.put("android_id",body.get("android_id").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        object[1] = params;
        //JSONObject jsonObject = new JSONObject(params);
        // RequestHandler requestHandler = new RequestHandler();
        //requestHandler.sendPost(url,jsonObject);
        //ServerConnection serverConnection = new ServerConnection(context, activity);
        //serverConnection.getAuthRequest(object);
        return object;
    }

    public String getSessionToken(){
        SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();

        return singleToneAuthToen.getToken();
    }

    public void updateServerSharingLlocation(Object object){

        serverConnection.updateDaddyServer(object);

    }

    public void updateServerTripBackup(Object object){
        serverConnection.updateDaddyServer(object);
    }
    /*private JSONObject convertJson2Object(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        }catch (JSONException e){
            Log.i("ERROR",e.getMessage());
        }
        return null;
    }*/

    public void checkPairedDeviceRoutines(){
        RoutinsRepo routinsRepo = new RoutinsRepo(context);
        Set routinsArrayList = routinsRepo.getRoutinsPairedDeviceList();

        for (Object paired : routinsArrayList){
            String pairedDevice = (String) paired.toString();
            if (checkIfPairedDeviceConected()){

            }
        }

    }

    public boolean checkIfPairedDeviceConected(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.getState() != BluetoothAdapter.STATE_ON){
            return false;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            //List<BluetoothDevice> devices = bluetoothManager.getConnectionState();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            int status = -1;
            for (BluetoothDevice device : pairedDevices){
                status = bluetoothManager.getConnectionState(device,BluetoothGatt.GATT);

            }
        }else{

        }
        return true;
    }
    public boolean check_if_wifi_backup_only(){

        return true;
    }

    public String checkConnectivityType(){
        MyDevice myDevice = new MyDevice(context,activity);
        if(myDevice.checkIfWifiConnected()){
            return "WIFI";
        }
        return "Other";
    }
    public void checkPairedDevicesConnected() {

    }

    public HashMap getTripHistoryFields(){
        HashMap convertKeys = new HashMap();
        convertKeys.put("date","update_date");
        convertKeys.put("trip_number","trip_number");
        convertKeys.put("limit","speed_limit");
        convertKeys.put("latitude","latitude");
        convertKeys.put("longitude","longitude");
        convertKeys.put("place","place");
        convertKeys.put("speed","speed");
        return convertKeys;
    }
}
