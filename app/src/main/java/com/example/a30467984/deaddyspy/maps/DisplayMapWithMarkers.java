package com.example.a30467984.deaddyspy.maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.a30467984.deaddyspy.DAO.GroupRepo;
import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.Server.ServerConnection;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.Set;

//import static com.example.a30467984.deaddyspy.background.HotSpot.context;

/**
     * Created by 30467984 on 8/29/2018.
     */

    public class DisplayMapWithMarkers extends AppCompatActivity implements OnMapReadyCallback {
        SupportMapFragment mapFragment;
        final Context context = this;
        private ArrayList rideData;
        private String groupLocation;
        private String phone2contact;
        static private float ZOOM_IN_VALLUE = 13f;
        static private int BOUNDS_ZOOMM_IN_VALUE = 15;
        private String path = "https://li780-236.members.linode.com:443/api/";
        private static String uniqueID = null;
        private static final String PREF_MY_DADDY = "PREF_MY_DADDY";
        private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
        private static final String AUTH_TKN = "AUTH_TKN";
        private GroupRepo groupRepo = new GroupRepo(context);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            Intent i = getIntent();
            //rideData = (ArrayList) i.getSerializableExtra("RideData");
            Bundle extras = i.getExtras();
            groupLocation = extras.getString("GroupLocation");
            final HashMap phone2contact = (HashMap)extras.getSerializable("phone2ContactName");


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
                            googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .anchor(0.5f, 0.5f)
                                    .title(phone2contact.get(key).toString())
                                    .snippet("last seen: " + groupMemberParamsJSONArray.get("last_update_date").toString())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            latLng = new LatLng(latitude,longitude);

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }





        @Override
        public void onMapReady(GoogleMap googleMap) {

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
    }


