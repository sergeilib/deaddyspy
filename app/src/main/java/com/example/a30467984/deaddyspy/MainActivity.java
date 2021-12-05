package com.example.a30467984.deaddyspy;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Telephony;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.Server.ServerConnection;
import com.example.a30467984.deaddyspy.background.BackgroundBroadcastReceiver;
import com.example.a30467984.deaddyspy.background.BackgroundHandler;
import com.example.a30467984.deaddyspy.utils.MyDevice;
import com.example.a30467984.deaddyspy.utils.RequestHandler;
import com.example.a30467984.deaddyspy.utils.SingleToneAuthToen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public static boolean CHANGE_FLAG = false;
    StringBuffer response;
    URL url;
    Activity activity;
    ArrayList countries = new ArrayList();
    private ProgressDialog progressDialog;
    ListView listView;
    String responseText;
    private String path = "https://li780-236.members.linode.com:443/api/auth/";
    private static String uniqueID = null;
    private static final String PREF_MY_DADDY = "PREF_MY_DADDY";
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static final String AUTH_TKN = "AUTH_TKN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        SettingsRepo settingsRepo = new SettingsRepo(this);
//        findViewById(R.id.text_frame_layout).setVisibility(View.VISIBLE);
        checkLanguage(settingsRepo);
 //      String phoneNuber = getPhoneNumber();
        /*String uniqueAppId = getAppUUID();

        if (uniqueAppId == null){
            finish();
        }*/
//        String uuid = getAppUUID();
        String androidID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        //SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();
        // singleToneAuthToen.setToken();
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//
//        }
       // findViewById(R.id.text_frame_layout).setVisibility(View.VISIBLE);
       // findViewById(R.id.daddy_spy_first_constraint_layout).setVisibility(View.INVISIBLE);
        //appServerInit();
        MyAsyncTaskInit myAsyncTaskInit = new MyAsyncTaskInit();
        myAsyncTaskInit.execute();
        //findViewById(R.id.text_frame_layout).setVisibility(View.INVISIBLE);
        //findViewById(R.id.daddy_spy_first_constraint_layout).setVisibility(View.VISIBLE);
//        MyDevice myDevice = new MyDevice(this,activity);
//        myDevice.setWifiApState(this);
        goToBackground();
//        scheduleAlarm();


    }

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), BackgroundBroadcastReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, BackgroundBroadcastReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                60000, pIntent);

    }
    public void goToBackground(){
        BackgroundHandler backgroundHandler = new BackgroundHandler(getBaseContext(),activity,20000,getAppUUID());
        try {
            backgroundHandler.waitingHandler();
        }catch (RuntimeException e){
            Log.i("ERR",e.getMessage());
        }
    }

    public void startSpeedometer(View view) {

        Intent i = new Intent(this, ShowSpeedometer.class);

        // Starts TargetActivity
        startActivity(i);
    }

    public void startSettingsManager(View view) {
        Intent i = new Intent(this, SettingsManagerActivity.class);

        // Starts TargetActivity
        startActivity(i);
    }

    /// Open
    public void showHistory(View view) {
        Intent i = new Intent(this, HistoryManagerActivity.class);

        // Starts TargetActivity
        startActivity(i);
    }

    public void startGroupsActivity(View view){
        Intent i = new Intent(this, GroupsManagerActivity.class);

        // Starts TargetActivity
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /// if language was changed in settings activity ,should be changed in mainActivity allsow
        if (CHANGE_FLAG == true) {
            SettingsRepo settingsRepo = new SettingsRepo(this);
            checkLanguage(settingsRepo);
            //Intent refresh = new Intent(this,MainActivity.class);
            //refresh.putExtra("language",languages[position]);
            //startActivity(refresh);
            //finish();
            setContentView(R.layout.activity_main);
        } else {
            CHANGE_FLAG = false;
        }
    }

    public void checkLanguage(SettingsRepo settingsRepo) {
        String db_langwage = ((HashMap<String, String>) (settingsRepo.getSettingsList()).get("language")).get("value");
        //String unit = ((HashMap<String,String>)settingsList.get("scale")).get("value");
        Locale locale;
        Resources res = getResources();
        if (db_langwage != null) {
            switch (db_langwage) {
                case "english":
                    locale = new Locale("en");
                    break;
                case "hebrew":
                    locale = new Locale("iw");
                    break;
                case "russian":
                    locale = new Locale("ru");
                    break;
                default:
                    locale = new Locale("en");
            }

            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, dm);
        } else {
            String lang = Locale.getDefault().getLanguage();
            locale = new Locale(lang);
        }

        Configuration conf = getBaseContext().getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= 17) {

            conf.setLocale(locale);
            getApplicationContext().createConfigurationContext(conf);

        } else {
            conf.locale = locale;
            getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
        }

    }

    public void appServerInit() {

        getUniqId(getBaseContext());
        SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();

        //singleToneAuthToen.setToken();
        connectionInit();
    }

    private String serverToken() {
        SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();
        return singleToneAuthToen.getToken();
    }

    /*class GetServerData extends AsyncTask<Object,String,Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("DADDY SPY STARTING");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Object doInBackground(Object[] objects)
        {
            String uuid = getAppUUID();
            /// CHECK IF UUID exists on server

            try {
                url = new URL(path + "me");
            }catch (Exception e){

            }

            return getWebServiceResponseData();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            // For populating list data
//            CustomCountryList customCountryList = new CustomCountryList(activity, countries);
//            listView.setAdapter(customCountryList);

//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    Toast.makeText(getApplicationContext(),"You Selected "+countries.get(position).getCountryName()+ " as Country",Toast.LENGTH_SHORT).show();        }
//            });
        }
    }*/

    protected Void getWebServiceResponseData() {


        try {


//            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            //String mPhoneNumber = tMgr.getLine1Number();
            if (serverToken() == null) {
                url = new URL(path + "me");
            } else {

            }
            url = new URL(path);
            Log.d(TAG, "ServerData: " + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("");
            int responseCode = conn.getResponseCode();

            Log.d(TAG, "Response code: " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // Reading response from input Stream
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String output;
                response = new StringBuffer();

                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseText = response.toString();
        //Call ServerData() method to call webservice and store result in response
        //  response = service.ServerData(path, postDataParams);
        Log.d(TAG, "data:" + responseText);
        try {
            JSONArray jsonarray = new JSONArray(responseText);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                int id = jsonobject.getInt("id");
                String country = jsonobject.getString("countryName");
                Log.d(TAG, "id:" + id);
                Log.d(TAG, "country:" + country);
//                Country countryObj=new Country(id,country);
//                countries.add(countryObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAppUUID(){
        String uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }

    public String getPhoneNumber() {
        TelephonyManager mTelephony = (TelephonyManager) getBaseContext().getSystemService(
                Context.TELEPHONY_SERVICE);


        ///TelephonyManager mTelephony = null;
        if ((int) Build.VERSION.SDK_INT < 23) {
            //this is a check for build version below 23
            mTelephony = (TelephonyManager) getBaseContext().getSystemService(
                    Context.TELEPHONY_SERVICE);


        } else {
            //this is a check for build version above 23
            if (ContextCompat.checkSelfPermission(this.getBaseContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
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
                mTelephony = (TelephonyManager) getBaseContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
                Log.i("INFO", "If Permission is granted");
            }


        }
        if (mTelephony != null) {
            

            String country_code = mTelephony.getSimCountryIso();
            String networ_oper = mTelephony.getNetworkCountryIso();
            
            String code = getApplicationContext().getResources().getConfiguration().locale.getCountry();
            Locale locale = Locale.getDefault();
            String country_code_dd = locale.getCountry();
            String phn = mTelephony.getLine1Number();
            return country_code+mTelephony.getLine1Number();
        }else{
            return null;
        }

    }
    public synchronized  String getUniqId(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_MY_DADDY, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            String tkn = sharedPrefs.getString(AUTH_TKN,null);
            //firstTimeInit(uniqueID);
            if (uniqueID == null || tkn == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
                //// Create first time initialization to server
                firstTimeInit(uniqueID);
            }
        }
        return uniqueID;
    }


    /////////////////////////////////////////////////////////////////////////
    //// this method invoked in first time application running
    //// pass android_id and uuid to register on server
    //////////////////////////////////////////////////////////////////////////
    private void firstTimeInit(String uuid){
        try {
            Object[] object = new Object[2];
            URL url = new URL(path + "first_register");
            object[0] = url;

            HashMap<String,String> params = new HashMap<>();
            //params.put("url",path + "first_register");
            //params.put("method","POST");
            params.put("android_id",android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
            params.put("uuid",uniqueID);
            params.put("method","POST");
            object[1] = params;
            JSONObject jsonObject = new JSONObject(params);
           // RequestHandler requestHandler = new RequestHandler();
            //requestHandler.sendPost(url,jsonObject);
            ServerConnection serverConnection = new ServerConnection(getBaseContext(),activity);
            serverConnection.getInitRequest(object);
            Log.i("INFO","Async status" );
        }catch (Exception e){
            Log.i("INFO", e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////// this method called every time  we try to connect to server to get token
    ////// for establish session
    /////////////////////////////////////////////////////////////////////////////

    public void connectionInit(){
        try {
            Object[] object = new Object[2];
            URL url = new URL(path + "get_tmp_tkn");
            object[0] = url;

            HashMap<String,String> params = new HashMap<>();
            //params.put("url",path + "first_register");
            //params.put("method","POST");
            SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(
                    PREF_MY_DADDY, Context.MODE_PRIVATE);

            String tkn = sharedPrefs.getString(AUTH_TKN,null);
            params.put("android_id",android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
            //params.put("uuid",uniqueID);
            if (tkn != null) {
                params.put("token", tkn);
                params.put("method", "POST");
                object[1] = params;
                //JSONObject jsonObject = new JSONObject(params);
                // RequestHandler requestHandler = new RequestHandler();
                //requestHandler.sendPost(url,jsonObject);
                ServerConnection serverConnection = new ServerConnection(getBaseContext(), activity);
                serverConnection.getAuthRequest(object);
            }else{
                Toast.makeText(getApplicationContext(),"Can't establish connection with server",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.i("INFO", e.getMessage());
        }
    }

    public Object[] getRequestObject (String url,HashMap<String,String> params){
        Object[] obj = new Object[2];
        obj[0] = params;
        //obj[1] = params;
        return obj;
    }

    private class MyAsyncTaskInit extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            setContentView(R.layout.activity_main_init);
            appServerInit();
            //goToBackground();
            scheduleAlarm();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            setContentView(R.layout.activity_main);
            //findViewById(R.id.text_frame_layout).setVisibility(View.INVISIBLE);
            //findViewById(R.id.daddy_spy_first_constraint_layout).setVisibility(View.VISIBLE);
        }
    }

}

