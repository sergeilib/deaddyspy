package com.example.a30467984.deaddyspy.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

public class Common {
    public String base_url;
    public Context context;
    public Common(Context context) {
        this.context = context;
        this.base_url = getBaseUrlFromConfigProperties(this.context);
    }

    public Object prepareConnectionObject(String url_suffix, JSONObject body, String method) {
        String tkn = getSessionToken();
        if (tkn == null) {
            //ServerConnection serverConnection = new ServerConnection(context, activity);
            //serverConnection.getAuthRequest(object);
            return null;
        }
        Object[] object = new Object[2];
        URL url = null;
        try {

            url = new URL( base_url + url_suffix + "&token=" + tkn);
        } catch (MalformedURLException m) {
            Log.i("ERR", m.getMessage());
        }
        object[0] = url;

        HashMap<String, String> params = new HashMap<>();


        params.put("token", tkn);
        params.put("method", method);
        if (method.equals("POST")) {
            if (body != null) {
                try {
                    params.put("data", body.get("data").toString());
                    //params.put("android_id",body.get("android_id").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // url_suffix = url_suffix + "&token="+ tkn;
        }
        object[1] = params;
        //JSONObject jsonObject = new JSONObject(params);
        // RequestHandler requestHandler = new RequestHandler();
        //requestHandler.sendPost(url,jsonObject);
        //ServerConnection serverConnection = new ServerConnection(context, activity);
        //serverConnection.getAuthRequest(object);
        return object;
    }

    public static String getSessionToken(){
        SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();

        return singleToneAuthToen.getToken();
    }

    private String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static  String getBaseUrlFromConfigProperties(Context context) {

        //reads the configuration file
        PropertiesReader propertiesReader = new PropertiesReader(context);
        Properties p=propertiesReader.getProperties("config.properties");

        //recovery of the parameters
//        ip_address = p.getProperty("ip");
//        hostname = p.getProperty("host");
//        port = p.getProperty("port");
        return p.getProperty("base_url");
    }
}