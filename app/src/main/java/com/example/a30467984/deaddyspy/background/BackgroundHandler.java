package com.example.a30467984.deaddyspy.background;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
                Thread.sleep(10000);
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
                        Object object = prepareConnectionObject(url_suffix);
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

    public Object prepareConnectionObject(String url_suffix){
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

    public void checkPairedDevicesConnected() {

    }
}
