package com.example.a30467984.deaddyspy;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static com.example.a30467984.deaddyspy.R.layout.activity_maps;

/**
 * Created by 30467984 on 8/29/2018.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    private ArrayList rideData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_maps);
        Intent i = getIntent();
        rideData = (ArrayList) i.getSerializableExtra("RideData");



        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        if (rideData == null){
                            return;
                        }
                        for (int i = 1; i < rideData.size() - 1;i++) {
                            //  HashMap<String, String> tmpData = ((HashMap<String, String>) rideData.get(i)).get(Point.KEY_longitude);
                            String longitudeSrcStr = ((HashMap<String, String>) rideData.get(i)).get(Point.KEY_longitude);
                            String latitudeSrcStr = ((HashMap<String, String>) rideData.get(i)).get(Point.KEY_latitude);
                            Double longitudeSrc = Double.parseDouble(longitudeSrcStr);
                            Double latitudeSrc = Double.parseDouble(latitudeSrcStr);

                            String longitudeDstStr = ((HashMap<String, String>) rideData.get(i + 1)).get(Point.KEY_longitude);
                            String latitudeDstStr = ((HashMap<String, String>) rideData.get(i + 1)).get(Point.KEY_latitude);
                            Double longitudeDst = Double.parseDouble(longitudeDstStr);
                            Double latitudeDst = Double.parseDouble(latitudeDstStr);

                            googleMap.addPolyline(
                                    new PolylineOptions().add(
                                            new LatLng(latitudeSrc, longitudeSrc),
                                            new LatLng(latitudeDst, longitudeDst)
                                    ).width(6).color(Color.BLUE).geodesic(false));

//                            Set<String> key = tmpData.keySet();
//                            Iterator it = key.iterator();
//                            while (it.hasNext()) {
//                                String hmKey = (String) it.next();
//                            }

                            if (i == 1) {
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitudeSrc, longitudeSrc))
                                        .title("Start")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeSrc, longitudeSrc), 10));
                            }

//                            googleMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(37.4629101, -122.2449094))
//                                    .title("Facebook")
//                                    .snippet("Facebook HQ: Menlo Park"));


                        }
                        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeSrc, -122.0728817), 10));
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
}
