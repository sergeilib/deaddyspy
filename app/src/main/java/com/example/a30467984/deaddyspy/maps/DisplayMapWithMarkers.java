package com.example.a30467984.deaddyspy.maps;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.R;
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
import java.util.Set;

    /**
     * Created by 30467984 on 8/29/2018.
     */

    public class DisplayMapWithMarkers extends AppCompatActivity implements OnMapReadyCallback {
        SupportMapFragment mapFragment;
        private ArrayList rideData;
        private String groupLocation;
        static private float ZOOM_IN_VALLUE = 13f;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            Intent i = getIntent();
            //rideData = (ArrayList) i.getSerializableExtra("RideData");
            Bundle extras = i.getExtras();
            groupLocation = extras.getString("GroupLocation");
            String phone2contact = extras.getString("phone2ContactName");


            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);



            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    JSONObject jsonObj = convertJson2Object(groupLocation);
                    JSONObject jsonResultObj;
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
                                    .title(key)
                                    .snippet("baa")
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
                            cu = CameraUpdateFactory.newLatLngBounds(bounds,15);
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
    }


