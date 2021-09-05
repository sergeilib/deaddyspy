package com.example.a30467984.deaddyspy.maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.a30467984.deaddyspy.DAO.GroupRepo;
import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.GroupsManagerActivity;
import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.R;
//import com.example.a30467984.deaddyspy.Server.ServerConnection;
import com.example.a30467984.deaddyspy.Server.ServerConnection;
import com.example.a30467984.deaddyspy.modules.ConnectionResponse;
import com.example.a30467984.deaddyspy.modules.TaskCompleted;
import com.example.a30467984.deaddyspy.utils.RequestHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

//import static com.example.a30467984.deaddyspy.background.HotSpot.context;

/**
     * Created by 30467984 on 8/29/2018.
     */

    public class DisplayMapWithMarkers extends AppCompatActivity implements OnMapReadyCallback {
        SupportMapFragment mapFragment;
        final Context context = this;
        private Activity activity;
        private ArrayList rideData;
        private String groupLocation;
        private HashMap phone2contact;
        static private float ZOOM_IN_VALLUE = 13f;
        static private int BOUNDS_ZOOMM_IN_VALUE = 15;
        private String path = "https://li780-236.members.linode.com:443/api/";
        private static String uniqueID = null;
        private static final String PREF_MY_DADDY = "PREF_MY_DADDY";
        private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        private static final String AUTH_TKN = "AUTH_TKN";
        private Handler handler = new Handler();
        private Runnable runnable;
        private  ConnectionResponse connectionResponse = new ConnectionResponse();
        private GroupRepo groupRepo = new GroupRepo(context);
        private int interval;
        private int interval_default = 30000;
        private HashMap<String,Marker> googleMapMarkerHash = new HashMap<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            Intent i = getIntent();
            //rideData = (ArrayList) i.getSerializableExtra("RideData");
            Bundle extras = i.getExtras();
            groupLocation = extras.getString("GroupLocation");
            phone2contact = (HashMap)extras.getSerializable("phone2ContactName");
            try {
                interval = getIntervalFromConfigFile();
            }catch (IOException e){
                interval = interval_default;
            }

            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);



            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    JSONObject jsonObj = convertJson2Object(groupLocation);
                    JSONObject jsonResultObj;

                    //JSONObject contactsJsonObj = convertJson2Object(phone2contact);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    try {
                        jsonResultObj = jsonObj.getJSONObject("json_result");
                        Iterator<String> temp = jsonResultObj.keys();
                        LatLng latLng = null;
                        int marersCounter = 0;
                        while (temp.hasNext()) {
                            marersCounter++;
                            String key = temp.next();
                            JSONArray groupMemberParams = jsonResultObj.getJSONArray(key);

                            JSONObject groupMemberParamsJSONArray = groupMemberParams.getJSONObject(0);
                            Double latitude = (Double) groupMemberParamsJSONArray.get("last_latitude");
                            Double longitude = (Double) groupMemberParamsJSONArray.get("last_longitude");
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .anchor(0.5f, 0.5f)
                                    .title(phone2contact.get(key).toString())
                                    .snippet("last seen: " + groupMemberParamsJSONArray.get("last_update_date").toString())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            latLng = new LatLng(latitude,longitude);

                            builder.include(latLng);
                            googleMapMarkerHash.put(key,marker);
                        }
                        CameraUpdate cu = null;
                        if (marersCounter == 1){
                            if (latLng != null) {
                                cu = CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_IN_VALLUE);

                            }
                        }else{
                            LatLngBounds bounds = builder.build();
                            cu = CameraUpdateFactory.newLatLngBounds(bounds,BOUNDS_ZOOMM_IN_VALUE);
                        }
                        googleMap.animateCamera(cu);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /// stop scheduler of map activity on back press
        handler.removeCallbacksAndMessages(null);
        this.finish();
    }



        @Override
        public void onMapReady(GoogleMap googleMap) {
            Toast.makeText(getApplicationContext(), "The map is ready", Toast.LENGTH_SHORT).show();
            location_check_scheduler(interval,"checkGroupLocation",googleMap);

//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(37.4233438, -122.0728817))
//                .title("LinkedIn")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(37.4629101,-122.2449094))
//                .title("Facebook")
//                .snippet("Facebook HQ: Menlo Park"));
//
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(37.3092293, -122.1136845))
//                .title("Apple"));

            //       googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.4233438, -122.0728817), 10));
        }

    public void location_check_scheduler(final int interval, final String action,final GoogleMap googleMap){
        try {
            Thread.sleep(10000);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable,interval);
                doAction(action,googleMap);

            }
        },interval);

    }

    private void doAction(String action,GoogleMap googleMap){
            switch (action) {
                case "checkGroupLocation":
                    checkGroupLocation(googleMap);
                    break;
                default:
            }
    }

    private void checkGroupLocation(GoogleMap googleMap){
        try {
           // HashMap<String,String> phone2Contact = new HashMap<>();
            HashMap<String, String> params = new HashMap<>();
            //params.put("url",path + "first_register");
            //params.put("method","POST");
            SharedPreferences sharedPrefs = getBaseContext().getSharedPreferences(
                    PREF_MY_DADDY, Context.MODE_PRIVATE);

            String tkn = sharedPrefs.getString(AUTH_TKN, null);
            ArrayList membersList = new ArrayList();
            Iterator it = phone2contact.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                String tmp = pair.getKey().toString();
                membersList.add(tmp);
            }
            //params.put("android_id", android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
            String phones_string = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                phones_string = String.join(",",membersList);
            }
            Object[] object = new Object[2];


            //params.put("uuid",uniqueID);
            if (tkn != null) {

                params.put("token", tkn);
                params.put("method", "GET");
                object[1] = params;
                URL url = new URL(path + "location/fetch_location?phone="+ phones_string + "&token=" + tkn );
                object[0] = url;
                //JSONObject jsonObject = new JSONObject(params);
                // RequestHandler requestHandler = new RequestHandler();
                //requestHandler.sendPost(url,jsonObject);
                try {
                    handleGroupConnection(object,googleMap);
//                    ServerConnection serverConnection = new ServerConnection(getBaseContext(), DisplayMapWithMarkers.this);
//                    serverConnection.getGroupLocation(object, phone2contact);
                }catch (Exception e){
                    Log.i("ERROR",e.getMessage());
                }
            } else {
                Toast.makeText(getApplicationContext(), "Can't establish connection with server", Toast.LENGTH_SHORT).show();
                /// try to establish connection
                MainActivity mainActivity = new MainActivity();
                mainActivity.appServerInit();

            }
        }catch (Exception e){
            Log.i("ERROR",e.getMessage());
        }
    }

    private void updateMarkerPosition(JSONObject jsonObj,GoogleMap googleMap) throws JSONException {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        JSONObject jsonResultObj = jsonObj.getJSONObject("json_result");
        Iterator<String> temp = jsonResultObj.keys();
        LatLng latLng = null;
        int marersCounter = 0;
        while (temp.hasNext()) {
            marersCounter++;
            String key = temp.next();
            // remove previous marker
            if (googleMapMarkerHash.containsKey(key)){
                Marker oldMarker = googleMapMarkerHash.get(key);
                oldMarker.remove();
            }

            JSONArray groupMemberParams = jsonResultObj.getJSONArray(key);

            JSONObject groupMemberParamsJSONArray = groupMemberParams.getJSONObject(0);
            Double latitude =  (Double) groupMemberParamsJSONArray.get("last_latitude");
            Double longitude = (Double) groupMemberParamsJSONArray.get("last_longitude");

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(0.5f, 0.5f)
                    .title(phone2contact.get(key).toString())
                    .snippet("last seen: " + groupMemberParamsJSONArray.get("last_update_date").toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            latLng = new LatLng(latitude,longitude);
            googleMapMarkerHash.put(key,marker);
            builder.include(latLng);

        }
        CameraUpdate cu = null;
        if (marersCounter == 1){
            if (latLng != null) {
                cu = CameraUpdateFactory.newLatLngZoom(latLng,ZOOM_IN_VALLUE);

            }
        }else{
            LatLngBounds bounds = builder.build();
            cu = CameraUpdateFactory.newLatLngBounds(bounds,BOUNDS_ZOOMM_IN_VALUE);
        }
        googleMap.animateCamera(cu);
    }



    private void handleGroupConnection(Object object,GoogleMap googleMap){
  //
        //ServerConnection serverConnection = new ServerConnection(context,activity);
        //serverConnection.ServerAsyncConnection(DisplayMapWithMarkers.this).exexute(object);
        //ConnectionResponse connectionResponse = new ConnectionResponse();
        new ServerAsyncConnection(DisplayMapWithMarkers.this).execute(object);
        boolean connection_flag = false;
        //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
        for (int sec = 0 ; sec < 3 ; sec++){
            try {
                Thread.sleep(1000);
                Log.i("INFO","ITERATION:" + sec );
                if (connectionResponse.getStatus() != null) {
                    if (connectionResponse.getStatus().equals("failure")){
                        Log.i("INFO",connectionResponse.getError()  );
                        break;
                    }else {
                        connection_flag = true;
                        JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                        try {
                            updateMarkerPosition(jsonObj,googleMap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();
                        try {
                            //jsonObj.put("phone2name",phone2Contact);

                            //  singleToneAuthToen.setToken(jsonObj.get("token").toString());
                        } catch (Exception e) {
                            Log.i("ERROR", e.getMessage());
                        }
                        break;
                    }
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{
                    Log.i("INFO","Connection Status: " + connectionResponse.getStatus());

                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
        if(connection_flag == false ) {
            Toast.makeText(context, "Can't fetch groups location from server", Toast.LENGTH_SHORT).show();
        }
    }

    private int getIntervalFromConfigFile() throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return Integer.parseInt(properties.getProperty("scheduler_location_polling_interval"));
    }
        private JSONObject convertJson2Object(String json){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                Log.i("INFO",jsonObject.getString("token"));
                //JSONObject jsonObject = new JSONObject("\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OTQsImlhdCI6MTYwNDQ3ODc4OCwiZXhwIjoxNjA0NTY1MTg4fQ.eDojL9xwKuyVIB5l3Xz-DalBOzNVl4A4y-pOdi6CXFY\",\"func\":\"get_tmp_tkn\",\"code\":\"1000\",\"error\":\"false\"");
                // return jsonObject;
            }catch (JSONException e){
                Log.i("ERROR",e.getMessage());
            }
            return jsonObject;
        }

        /*private void load_location_from_server(){
            if (selectedGroupName != null) {
                HashMap<String,String> phone2Contact = new HashMap<>();
                Map groupDetails = groupRepo.getGroupParamsByGroupId(Integer.parseInt(groupsHashByNameKey.get(selectedGroupName).toString()));
                Iterator it = groupDetails.entrySet().iterator();
                ArrayList membersList = new ArrayList();
                while (it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    HashMap tmp = (HashMap) pair.getValue();
                    //groupsHashByNameKey.put(tmp.get("name"),tmp.get("id"));

                    membersList.add(tmp.get("member"));
                    phone2Contact.put(tmp.get("member").toString(),tmp.get("member_name").toString());
                }
                try {
                    HashMap<String, String> params = new HashMap<>();
                    //params.put("url",path + "first_register");
                    //params.put("method","POST");
                    SharedPreferences sharedPrefs = getBaseContext().getSharedPreferences(
                            PREF_MY_DADDY, Context.MODE_PRIVATE);

                    String tkn = sharedPrefs.getString(AUTH_TKN, null);
                    //params.put("android_id", android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
                    String phones_string = String.join(",",membersList);
                    Object[] object = new Object[2];


                    //params.put("uuid",uniqueID);
                    if (tkn != null) {

                        params.put("token", tkn);
                        params.put("method", "GET");
                        object[1] = params;
                        URL url = new URL(path + "location/fetch_location?phone="+ phones_string + "&token=" + tkn );
                        object[0] = url;
                        //JSONObject jsonObject = new JSONObject(params);
                        // RequestHandler requestHandler = new RequestHandler();
                        //requestHandler.sendPost(url,jsonObject);
                        ServerConnection serverConnection = new ServerConnection(getBaseContext(), activity);
                        serverConnection.getGroupLocation(object,phone2Contact);
                    } else {
                        Toast.makeText(getApplicationContext(), "Can't establish connection with server", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.i("ERROR",e.getMessage());
                }
            }
        }*/
        private class ServerAsyncConnection extends AsyncTask<Object, Void, HashMap<String,String>> {
            // onPreExecute called before the doInBackgroud start for display
            // progress dialog.
            private TaskCompleted mCallback;

            public ServerAsyncConnection(Context context){
                //this.mCallback = (TaskCompleted) context;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//            pd = ProgressDialog.show(MainActivity.this, "", "Loading", true,
//                    false); // Create and show Progress dialog
            }

            @Override
            protected HashMap<String,String> doInBackground(Object... objects) {
                Log.i("INFO","doInBackground "+ objects[0].toString() );
                Object[] obj = (Object[]) objects[0];
                URL url = (URL)obj[0];
                HashMap<String,String> params = (HashMap<String,String>)obj[1];
                JSONObject jsonObject = new JSONObject(params);
                RequestHandler requestHandler = new RequestHandler(context,activity );
                HashMap postResponse = new HashMap<String,String> ();
                try {
                    String postResponseStr = requestHandler.sendMethod((URL) obj[0], jsonObject, params.get("method"));
                    Log.i("INFO","RESPONSE" + postResponseStr);
                    postResponse.put("status","success");
                    postResponse.put("message",postResponseStr);

                }catch (Exception e){
                    Log.i("ERROR",e.getMessage());
                    postResponse.put("status","failure");
                    postResponse.put("error",e.getMessage());
                }
                Log.i("INFO","EXIT doInBackgraond");
                //mCallback.onTaskComlete(postResponse);
                return postResponse;

            }

            // onPostExecute displays the results of the doInBackgroud and also we
            // can hide progress dialog.
            @Override
            protected void onPostExecute(HashMap<String,String> result) {
                Log.i("INFO","Request Result " + result.get("status").toString());
                //mCallback.onTaskComlete(result);

                connectionResponse.setStatus(result.get("status").toString());
                if (connectionResponse.getStatus().equals("success")) {
                    connectionResponse.setMessage(result.get("message").toString());
                    JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());

                }else {
                    connectionResponse.setError(result.get("error").toString());
                }
                //pd.dismiss();
                //tvData.setText(result);
                // TextView tv_pointParams = (TextView) activity.findViewById(R.id.low_textView_location);
                //((LocationData) Activity.getApplication()).setCurrentAddress(result.get("address"));

            }
        }

    }


