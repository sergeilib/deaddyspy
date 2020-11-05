package com.example.a30467984.deaddyspy.background;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.Server.ServerConnection;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.ConnectionResponse;
import com.example.a30467984.deaddyspy.utils.SingleToneAuthToen;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


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


        public BackgroundHandler(Context context, Activity activity ,int interval,String uuid){
            connectionResponse = new ConnectionResponse();
            this.context = context;
            this.interval = interval;
            this.activity = activity;
            serverConnection = new ServerConnection(this.context,this.activity);
            this.uuid = uuid;
        }

        public void waitingHandler(){
            final HashMap settingList = getSettings();
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(runnable,interval);
                    checkSharingLlocation(settingList);
                }
            },interval);

        }

        public HashMap getSettings(){
            SettingsRepo settingsRepo = new SettingsRepo(this.context);
            HashMap settingsList = settingsRepo.getSettingsList();
            return settingsList;
        }

        public void checkSharingLlocation(HashMap settingList) {
            if (settingList.containsKey("sharing_location")) {

                String ifShareLocation = ((HashMap<String, String>) settingList.get("sharing_location")).get("value");
                if (ifShareLocation.equals("true")) {
                    locationData = new LocationData(context,settingList,activity,1);
                    Double longitude = locationData.getLongitude();
                    Double latitude = locationData.getLatitude();
                    if (longitude > 0 && latitude > 0){
                        String android_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                        String url_suffix = "location/sharing_location?android_id=" +  android_id + "&last_longitude=" + longitude +
                                "&last_latitude=" + latitude;
                        Object object = prepareConnectionObject(url_suffix);
                        try {
                            updateServerSharingLlocation(object);
                        }catch (Exception e){
                            Log.i("ERROR",e.getMessage());
                        }
                    }

                }
            }
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
        params.put("token", tkn);
        params.put("method", "POST");
        object[1] = params;
        JSONObject jsonObject = new JSONObject(params);
        // RequestHandler requestHandler = new RequestHandler();
        //requestHandler.sendPost(url,jsonObject);
        ServerConnection serverConnection = new ServerConnection(context, activity);
        serverConnection.getAuthRequest(object);
        return jsonObject;
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
}
