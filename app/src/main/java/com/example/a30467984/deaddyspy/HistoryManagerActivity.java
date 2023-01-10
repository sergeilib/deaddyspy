package com.example.a30467984.deaddyspy;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.Server.ServerConnection;
import com.example.a30467984.deaddyspy.Server.ServerConnection.ServerAsyncConnection;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.ConnectionResponse;
import com.example.a30467984.deaddyspy.modules.TaskCompleted;
import com.example.a30467984.deaddyspy.utils.CsvCreator;
import com.example.a30467984.deaddyspy.utils.RequestHandler;
import com.example.a30467984.deaddyspy.utils.SingleToneAuthToen;
import com.example.a30467984.deaddyspy.utils.SingleToneServerListOfTrips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.view.Gravity.BOTTOM;

/**
 * Created by 30467984 on 8/13/2018.
 */

public class HistoryManagerActivity extends Activity implements TaskCompleted {
    private String path = "https://li780-236.members.linode.com:443/api/";
    private List<String> fieldList = Arrays.asList("date", "speed","limit","latitude","longitude","place");
    RoutingRepo routingRepo = new RoutingRepo(this);
    ArrayList<String> rideList = new ArrayList<String>();
    SparseBooleanArray sparseBooleanArray ;
    private ServerConnection serverConnection;
    private ConnectionResponse connectionResponse;
    private Object objectRemoveFromServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_history);
    }


    public void openLastRide(View v) {

        int lastRideNum = routingRepo.getMaxTripNumber();
        if (lastRideNum == 0){
            Toast.makeText(HistoryManagerActivity.this, "No history was found on this device", Toast.LENGTH_LONG).show();
            return;
            //super.onBackPressed();
        }
        makeButtonsInvisible();
        FrameLayout historyFR = (FrameLayout) findViewById(R.id.historyFrameLayout);
        //LinearLayout tableBG = (LinearLayout) findViewById(R.id.table_background_layout);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollTable);
        scrollView.setVisibility(View.VISIBLE);
        TableLayout tl = (TableLayout) findViewById(R.id.ride_table);
        tl.setVisibility(View.VISIBLE);

        final ArrayList lasttripData = routingRepo.getLocationListByTripNumber(lastRideNum);
        buildHeader(lasttripData, tl);

        buildTable(lasttripData, tl);

        Button bt = new Button(this);
        bt.setText("map");

        bt.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, BOTTOM));
        // tableBG.addView(bt);
        historyFR.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMap(v,lasttripData);
                // TODO Auto-generated method stub
            }
        });
    }

    public void buildTable(ArrayList tripData,TableLayout tl){
        for (int i = 0; i < tripData.size();i++){
            HashMap<String,String> tmpData = (HashMap<String,String>) tripData.get(i);
            Set <String> key = tmpData.keySet();
            Iterator it = key.iterator();

            TableRow tr = new TableRow(this);

            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            TextView CountTV = new TextView(this);

            CountTV.setText(""+i);

            CountTV.setTextColor(Color.GRAY);
            CountTV.setBackgroundColor(Color.WHITE);

            CountTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

            CountTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            CountTV.setPadding(5, 5, 5, 0);

            tr.addView(CountTV);
            for (String hmKey : fieldList) {


            //while(it.hasNext()){
                //String hmKey = (String)it.next();
                String hmData = (String) tmpData.get(hmKey);
                TextView valueTV = new TextView(this);

                valueTV.setText(hmData);

                valueTV.setTextColor(Color.GRAY);
                valueTV.setBackgroundColor(Color.WHITE);

                valueTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                trParams.setMargins(1,1,1,1);
                valueTV.setLayoutParams(trParams);

                valueTV.setPadding(5, 5, 5, 0);

                tr.addView(valueTV);
            }
            LinearLayout.LayoutParams params = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(1,1,1,1);
            tl.addView(tr,params);
        }
    }

    public void buildHeader(ArrayList tripData,TableLayout tl){
        HashMap<String,String> tmpData = (HashMap<String,String>) tripData.get(1);
        Set <String> key = tmpData.keySet();
        Iterator it = key.iterator();

        TableRow tr = new TableRow(this);
        //tr.setBackgroundColor(Color.WHITE);
        //tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
         //       TableLayout.LayoutParams.WRAP_CONTENT));

        TextView headerCountTV = new TextView(this);

        headerCountTV.setText("#");

        headerCountTV.setTextColor(Color.GRAY);

        headerCountTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        trParams.setMargins(1,1,1,1);
        headerCountTV.setLayoutParams(trParams);

        headerCountTV.setPadding(5, 5, 5, 0);

        tr.addView(headerCountTV);
        for (String hmKey : fieldList) {
        //while(it.hasNext()){
        //    String hmKey = (String)it.next();
        //    String hmData = (String) tmpData.get(hmKey);
            TextView headerTV = new TextView(this);

            headerTV.setText(hmKey);

            headerTV.setTextColor(Color.GRAY);

            headerTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

            TableRow.LayoutParams trHeaderParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            trHeaderParams.setMargins(1,1,1,1);
            headerTV.setLayoutParams(trHeaderParams);

            headerTV.setPadding(5, 5, 5, 0);

            tr.addView(headerTV);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(1,1,1,1);
        tl.addView(tr,params);

        /** Creating a TextView to add to the row **/

        TextView  textView= new TextView(this);
    }

    public void openHistoryList(View v) {
        final ListView listview;
        final ArrayList tripList = routingRepo.getStartingLocationList();
        if (tripList.size() == 0){
            Toast.makeText(HistoryManagerActivity.this, "No history was found on this device", Toast.LENGTH_LONG).show();
            return;
            //super.onBackPressed();
        }
        makeButtonsInvisible();
        ConstraintLayout cl_history_list = (ConstraintLayout)findViewById(R.id.constraint_history_list);
        cl_history_list.setVisibility(View.VISIBLE);
        listview = (ListView)findViewById(R.id.history_list_view);
        //listview.setVisibility(View.VISIBLE);
        //final ArrayList tripList = routingRepo.getStartingLocationList();
        //final String[] listViewItems = new String[tripList.size()];
        final List<String> listViewItems = new ArrayList<String>();
        //show list in descendind order
        for (int i = tripList.size()-1; i >= 0; i--){
            HashMap<String,String> tmpData = (HashMap<String,String>) tripList.get(i);

            String a = tmpData.get("id");
            listViewItems.add(tmpData.get("date"));

//            LinearLayout.LayoutParams params = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//            params.setMargins(1,1,1,1);
            // tl.addView(tr,params);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,
                        android.R.layout.simple_list_item_multiple_choice,
                         listViewItems );

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                rideList.clear();
                sparseBooleanArray = listview.getCheckedItemPositions();

                String ValueHolder = "" ;

                int i = 0 ;

                while (i < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(i)) {

                        ValueHolder += listViewItems.get(position) + ",";
                        //int a = sparseBooleanArray.keyAt(i);
                        //rideList.add(sparseBooleanArray.keyAt(i));

                        HashMap<String,String> chosenData = (HashMap<String,String>) tripList.get(tripList.size()-1 - sparseBooleanArray.keyAt(i));
                        String a = chosenData.get("trip_number");
                        rideList.add(a);
                    }

                    i++ ;
                }

                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
//                HashMap<String,String> chosenData = (HashMap<String,String>) tripList.get(tripList.size()-1 - position);
//                String a = chosenData.get("trip_number");
//                rideList.add(a);
                Toast.makeText(HistoryManagerActivity.this, "ListView Selected Values = " + ValueHolder , Toast.LENGTH_SHORT).show();

            }
        });

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //// This function get list of trips from the server and display it on screen
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void openHistoryListFromServer(View v) {
        final ListView listview;
        String android_id = android.provider.Settings.Secure.getString(this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        String url_suffix = "location/get_trip_list?android_id=" +  android_id;
        Object object = prepareConnectionObject(url_suffix,null,"GET");

        if (object != null) {
            try {
         //       updateServerTripBackup(object);

                getNumberOfTripsFromServer(object);

            } catch (Exception e) {
                Log.i("ERROR", "CHeckSharingLocation" + e.getMessage());
            }
        } else {
            Toast.makeText(HistoryManagerActivity.this, "Can't connect server", Toast.LENGTH_LONG).show();
            Log.i("INFO", "The token is NULL, Can't connect to server");
            return;
        }

        /*final ArrayList tripList = routingRepo.getStartingLocationList();
        
        if (tripList.size() == 0){
            Toast.makeText(HistoryManagerActivity.this, "No history was found on server", Toast.LENGTH_LONG).show();
            return;
            //super.onBackPressed();
        }
        makeButtonsInvisible();
        ConstraintLayout cl_history_list = (ConstraintLayout)findViewById(R.id.constraint_history_list);
        cl_history_list.setVisibility(View.VISIBLE);
        listview = (ListView)findViewById(R.id.history_list_view);
        //listview.setVisibility(View.VISIBLE);
        //final ArrayList tripList = routingRepo.getStartingLocationList();
        //final String[] listViewItems = new String[tripList.size()];
        final List<String> listViewItems = new ArrayList<String>();
        //show list in descendind order
        for (int i = tripList.size()-1; i >= 0; i--){
            HashMap<String,String> tmpData = (HashMap<String,String>) tripList.get(i);

            String a = tmpData.get("id");
            listViewItems.add(tmpData.get("date"));

//            LinearLayout.LayoutParams params = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//            params.setMargins(1,1,1,1);
            // tl.addView(tr,params);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,
                        android.R.layout.simple_list_item_multiple_choice,
                        listViewItems );

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                rideList.clear();
                sparseBooleanArray = listview.getCheckedItemPositions();

                String ValueHolder = "" ;

                int i = 0 ;

                while (i < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(i)) {

                        ValueHolder += listViewItems.get(position) + ",";
                        //int a = sparseBooleanArray.keyAt(i);
                        //rideList.add(sparseBooleanArray.keyAt(i));

                        HashMap<String,String> chosenData = (HashMap<String,String>) tripList.get(tripList.size()-1 - sparseBooleanArray.keyAt(i));
                        String a = chosenData.get("trip_number");
                        rideList.add(a);
                    }

                    i++ ;
                }

                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
//                HashMap<String,String> chosenData = (HashMap<String,String>) tripList.get(tripList.size()-1 - position);
//                String a = chosenData.get("trip_number");
//                rideList.add(a);
                Toast.makeText(HistoryManagerActivity.this, "ListView Selected Values = " + ValueHolder , Toast.LENGTH_SHORT).show();

            }
        });
*/
    }

    public void makeButtonsInvisible(){
        ConstraintLayout tableBG = (ConstraintLayout) findViewById(R.id.table_background_layout);
        Button last_ride_bt = findViewById(R.id.last_ride_button);
        Button history_list_bt = findViewById(R.id.history_list_button);
        last_ride_bt.setVisibility(View.GONE);
        history_list_bt.setVisibility(View.GONE);
        Button server_last_ride_bt = findViewById(R.id.last_ride_on_server_button);
        Button server_history_list_bt = findViewById(R.id.history_list_on_server_button);
        server_last_ride_bt.setVisibility(View.GONE);
        server_history_list_bt.setVisibility(View.GONE);
        TextView device_tv = findViewById(R.id.textview_on_this_device_id);
        device_tv.setVisibility(View.GONE);
        TextView server_tv = findViewById(R.id.textview_on_server);
        server_tv.setVisibility(View.GONE);
    }
    public void makeHistoryListInvisible(){
        ConstraintLayout history_list_bt = findViewById(R.id.constraint_history_list);
        history_list_bt.setVisibility(View.GONE);
        ConstraintLayout history_list_server_bt = findViewById(R.id.constraint_history_list_from_server);
        history_list_server_bt.setVisibility(View.GONE);

    }

    public void openHistoryItem(View view){
        if (rideList.size() > 1 || rideList.size() == 0 ){
            Toast.makeText(HistoryManagerActivity.this, "Please, choose one item for see the map ", Toast.LENGTH_SHORT).show();
            return;
        }

        makeButtonsInvisible();
        makeHistoryListInvisible();
        FrameLayout historyFR = (FrameLayout) findViewById(R.id.historyFrameLayout);
        //ConstraintLayout historyFR = (ConstraintLayout) findViewById(R.id.constraint_history_list);
       // ConstraintLayout tableBG = (ConstraintLayout) findViewById(R.id.table_background_layout);
        //tableBG.setVisibility(View.VISIBLE);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollTable);
        scrollView.setVisibility(View.VISIBLE);
        TableLayout tl = (TableLayout) findViewById(R.id.ride_table);
        tl.setVisibility(View.VISIBLE);


        final ArrayList lasttripData = routingRepo.getLocationListByTripNumber(Integer.parseInt(rideList.get(0)));
        buildHeader(lasttripData, tl);

        buildTable(lasttripData, tl);

        Button bt = new Button(this);
        bt.setText("map");

        bt.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT|BOTTOM));
        // tableBG.addView(bt);
        historyFR.addView(bt);
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMap(v,lasttripData);
                // TODO Auto-generated method stub
            }
        });
        Button bt_csv = new Button(this);
        bt_csv.setText("share");

        bt_csv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT|BOTTOM));
        // tableBG.addView(bt);
        historyFR.addView(bt_csv);
        bt_csv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CsvCreator csvCreator = new CsvCreator(HistoryManagerActivity.this);
                csvCreator.createCsvFile("trip",fieldList,lasttripData );
                // TODO Auto-generated method stub
            }
        });
    }

    public void removeHistoryItems(final View view){
        if (rideList.size() == 0 ){
            Toast.makeText(HistoryManagerActivity.this, "Please, select one or more items", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(HistoryManagerActivity.this);
        builder1.setMessage("Do you want to remove " + rideList.size() + " items");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        try {
                            for (int i = 0; i < rideList.size();i++){
//                                routingRepo.delete(Integer.parseInt(rideList.get(i)));


                            }
                        }catch(Exception e){
                            Log.i("ERROR",e.getMessage());
                        }
                        Toast.makeText(HistoryManagerActivity.this, rideList.size() + " items removed", Toast.LENGTH_SHORT).show();
                        rideList = null;
                        // reload list after remove
                        openHistoryList(view);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void openMap(View v,ArrayList rideData){
        // Button openMapBT = findViewById(R.id.open_map_button);

        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("RideData", rideData);
        // Starts TargetActivity
        startActivity(i);

    }

    public Object prepareConnectionObject(String url_suffix, JSONObject body,String method){
        String tkn = getSessionToken();
        if (tkn == null){
            //ServerConnection serverConnection = new ServerConnection(context, activity);
            //serverConnection.getAuthRequest(object);
            return null;
        }
        Object[] object = new Object[2];
        URL url = null;
        try {

            url = new URL(path + url_suffix +"&token=" + tkn );
        }catch (MalformedURLException m){
            Log.i("ERR",m.getMessage());
        }
        object[0] = url;

        HashMap<String,String> params = new HashMap<>();


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
        }else{
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

    public String getSessionToken(){
        SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();

        return singleToneAuthToen.getToken();
    }

    /// this method call by http request server in order to get
    /// the number of trips that stored on server and  update date of each trip

    public void getNumberOfTripsFromServer(Object object){
        Log.i("INFO","SUBJECT getAuthRequest: " + object.toString());
        connectionResponse = new ConnectionResponse();
        ServerAsyncConnection  serverAsyncConnection = new ServerAsyncConnection(HistoryManagerActivity.this);
        try{
            serverAsyncConnection.execute(object);
        }catch (Exception e){
            Log.i("ERROR",e.getMessage());
        }
        for (int sec = 0 ; sec < 10 ; sec++){
            try {
                Thread.sleep(1000);
                Log.i("INFO","ITERATION:" + sec );
                if (connectionResponse.getStatus() != null) {
                    if (connectionResponse.getStatus().equals("failure")){
                        Log.i("INFO",connectionResponse.getError()  );
                        break;
                    }else {
                        ArrayList listOfTrips = new ArrayList();
                        JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                        if (Integer.parseInt(jsonObj.get("code").toString()) != 1000) {
                            Toast.makeText(HistoryManagerActivity.this, "Can't get List of trips on server, try later " , Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONArray jsonArray = jsonObj.getJSONArray("json_result");

                        SingleToneServerListOfTrips singleToneServerListOfTrips = SingleToneServerListOfTrips.getInstance();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                HashMap row = new HashMap();
                                row.put("id",jsonArray.getJSONObject(i).getString("trip_number"));
                                row.put("date",jsonArray.getJSONObject(i).getString("update_date"));
                                //Log.i("INFO", "trip number: " + jsonObj.get("trip_number") + " Date : " + jsonObj.get("update_date").toString());
                                //listOfTrips.put(jsonArray.getJSONObject(i).getString("trip_number"), jsonArray.getJSONObject(i).getString("update_date"));
                                listOfTrips.add(row);

                            } catch (JSONException e) {
                                Log.i("ERROR", e.getMessage());
                            }

                        }
                        //singleToneServerListOfTrips.setListOfTrips(listOfTrips);
                        buildServerTripList(listOfTrips);

                        break;
                    }
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{

                }
            }catch (InterruptedException | JSONException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

/*
    public void openHistoryItemFromServer(View view){
        if (rideList.size() > 1 || rideList.size() == 0 ){
            Toast.makeText(HistoryManagerActivity.this, "Please, choose one item for see the map ", Toast.LENGTH_SHORT).show();
            return;
        }

        makeButtonsInvisible();
        makeHistoryListInvisible();
        FrameLayout historyFR = (FrameLayout) findViewById(R.id.historyFrameLayout);
        //ConstraintLayout historyFR = (ConstraintLayout) findViewById(R.id.constraint_history_list);
        // ConstraintLayout tableBG = (ConstraintLayout) findViewById(R.id.table_background_layout);
        //tableBG.setVisibility(View.VISIBLE);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollTable);
        scrollView.setVisibility(View.VISIBLE);
        TableLayout tl = (TableLayout) findViewById(R.id.ride_table);
        tl.setVisibility(View.VISIBLE);
        // getSelectedTripFromServer(Integer.parseInt(rideList.get(0)));

    }
*/

    public void getSelectedTripFromServer(View view){
        if (rideList.size() > 1 || rideList.size() == 0 ){
            Toast.makeText(HistoryManagerActivity.this, "Please, choose one item for see the map ", Toast.LENGTH_SHORT).show();
            return;
        }

        String android_id = android.provider.Settings.Secure.getString(this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        String url_suffix = "location/get_selected_trip?android_id=" +  android_id +"&trip_number=" + Integer.parseInt(rideList.get(0));
        Object object = prepareConnectionObject(url_suffix,null,"GET");

        if (object != null) {

        } else {
            Toast.makeText(HistoryManagerActivity.this, "Can't connect server", Toast.LENGTH_LONG).show();
            Log.i("INFO", "The token is NULL, Can't connect to server");
            return;
        }

        Log.i("INFO","SUBJECT getAuthRequest: " + object.toString());
        connectionResponse = new ConnectionResponse();
        ServerAsyncConnection  serverAsyncConnection = new ServerAsyncConnection(HistoryManagerActivity.this);
        try{
            serverAsyncConnection.execute(object);
        }catch (Exception e){
            Log.i("ERROR",e.getMessage());
        }
        for (int sec = 0 ; sec < 10 ; sec++){
            try {
                Thread.sleep(1000);
                Log.i("INFO","ITERATION:" + sec );
                if (connectionResponse.getStatus() != null) {
                    if (connectionResponse.getStatus().equals("failure")){
                        Log.i("INFO",connectionResponse.getError()  );
                        break;
                    }else {
                        ArrayList listOfTrips = new ArrayList();
                        JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                        if (Integer.parseInt(jsonObj.get("code").toString()) != 1000) {
                            Toast.makeText(HistoryManagerActivity.this, "Can't get List of trips on server, try later " , Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONArray jsonArray = jsonObj.getJSONArray("json_result");
                        final ArrayList selectedTripData = new ArrayList();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                HashMap row = new HashMap();
                                row.put("id", i + 1 );
                                row.put("longitude",jsonArray.getJSONObject(i).getString("longitude"));
                                row.put("latitude",jsonArray.getJSONObject(i).getString("latitude"));
                                row.put("date",jsonArray.getJSONObject(i).getString("update_date"));
                                row.put("limit",jsonArray.getJSONObject(i).getString("speed_limit"));
                                row.put("place",jsonArray.getJSONObject(i).getString("place"));
                                row.put("speed",jsonArray.getJSONObject(i).getString("speed"));
                               // row.put("trip_number",jsonArray.getJSONObject(i).getString("trip_number"));

//                student.put("name", cursor.getString(cursor.getColumnIndex(Location.KEY_name)));
                            //    locationList.add(location);
                        //        row.put("id",jsonArray.getJSONObject(i).getString("trip_number"));
                         //       row.put("date",jsonArray.getJSONObject(i).getString("update_date"));
                                //Log.i("INFO", "trip number: " + jsonObj.get("trip_number") + " Date : " + jsonObj.get("update_date").toString());
                                //listOfTrips.put(jsonArray.getJSONObject(i).getString("trip_number"), jsonArray.getJSONObject(i).getString("update_date"));
                                 selectedTripData.add(row);
                             //   final ArrayList lasttripData = routingRepo.getLocationListByTripNumber(Integer.parseInt(rideList.get(0)));

                            } catch (Exception e) {
                                Log.i("ERROR", e.getMessage());
                            }

                        }
                        makeButtonsInvisible();
                        makeHistoryListInvisible();
                        FrameLayout historyFR = (FrameLayout) findViewById(R.id.historyFrameLayout);
                        //ConstraintLayout historyFR = (ConstraintLayout) findViewById(R.id.constraint_history_list);
                        // ConstraintLayout tableBG = (ConstraintLayout) findViewById(R.id.table_background_layout);
                        //tableBG.setVisibility(View.VISIBLE);
                        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollTable);
                        scrollView.setVisibility(View.VISIBLE);
                        TableLayout tl = (TableLayout) findViewById(R.id.ride_table);
                        tl.setVisibility(View.VISIBLE);
                        buildHeader(selectedTripData, tl);

                        buildTable(selectedTripData, tl);

                        Button bt = new Button(this);
                        bt.setText("map");

                        bt.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.LEFT|BOTTOM));
                        // tableBG.addView(bt);
                        historyFR.addView(bt);
                        bt.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                openMap(v,selectedTripData);
                                // TODO Auto-generated method stub
                            }
                        });
                        Button bt_csv = new Button(this);
                        bt_csv.setText("share");

                        bt_csv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT|BOTTOM));
                        // tableBG.addView(bt);
                        historyFR.addView(bt_csv);
                        bt_csv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                CsvCreator csvCreator = new CsvCreator(HistoryManagerActivity.this);
                                csvCreator.createCsvFile("trip",fieldList,selectedTripData );
                                // TODO Auto-generated method stub
                            }
                        });
                        //singleToneServerListOfTrips.setListOfTrips(listOfTrips);
                        //buildServerTripList(listOfTrips);

                        break;
                    }
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{

                }
            }catch (InterruptedException | JSONException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    /// this method get server http response with number of trips and dates

    private void buildServerTripList(ArrayList listOfTrip){
        final ListView listview;
        final ArrayList tripList = listOfTrip;
        makeButtonsInvisible();
        ConstraintLayout cl_history_list = (ConstraintLayout)findViewById(R.id.constraint_history_list_from_server);
        cl_history_list.setVisibility(View.VISIBLE);
        listview = (ListView)findViewById(R.id.history_list_view_from_server);
        //listview.setVisibility(View.VISIBLE);
        //final ArrayList tripList = routingRepo.getStartingLocationList();
        //final String[] listViewItems = new String[tripList.size()];
        final List<String> listViewItems = new ArrayList<String>();
        //show list in descending order
        for (int i = tripList.size()-1; i >= 0; i--){
            HashMap<String,String> tmpData = (HashMap<String,String>) tripList.get(i);

            String a = tmpData.get("id");
            listViewItems.add(tmpData.get("date"));

//            LinearLayout.LayoutParams params = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//            params.setMargins(1,1,1,1);
            // tl.addView(tr,params);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,
                        android.R.layout.simple_list_item_multiple_choice,
                        listViewItems );

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                rideList.clear();
                sparseBooleanArray = listview.getCheckedItemPositions();

                String ValueHolder = "" ;

                int i = 0 ;

                while (i < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(i)) {

                        ValueHolder += listViewItems.get(position) + ",";
                        //int a = sparseBooleanArray.keyAt(i);
                        //rideList.add(sparseBooleanArray.keyAt(i));

                        HashMap<String,String> chosenData = (HashMap<String,String>) tripList.get(tripList.size()-1 - sparseBooleanArray.keyAt(i));
                        String a = chosenData.get("id");
                        rideList.add(a);
                    }

                    i++ ;
                }

                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
//                HashMap<String,String> chosenData = (HashMap<String,String>) tripList.get(tripList.size()-1 - position);
//                String a = chosenData.get("trip_number");
//                rideList.add(a);
                Toast.makeText(HistoryManagerActivity.this, "ListView Selected Values = " + ValueHolder , Toast.LENGTH_SHORT).show();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void removeHistoryItemsFromServer(final View view){
        if (rideList.size() == 0 ){
            Toast.makeText(HistoryManagerActivity.this, "Please, select one or more items", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(HistoryManagerActivity.this);
        builder1.setMessage("Do you want to remove " + rideList.size() + " items");
        builder1.setCancelable(true);
        String android_id = android.provider.Settings.Secure.getString(this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        String url_suffix = "location/remove_history_trips?android_id=" +  android_id +"&trip_number=" + String.join(",",rideList);
        objectRemoveFromServer = prepareConnectionObject(url_suffix,null,"POST");
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        try {

                            if (objectRemoveFromServer != null) {

                            } else {
                                Toast.makeText(HistoryManagerActivity.this, "Can't connect server", Toast.LENGTH_LONG).show();
                                Log.i("INFO", "The token is NULL, Can't connect to server");
                                return;
                            }

                            Log.i("INFO","SUBJECT getAuthRequest: " + objectRemoveFromServer.toString());
                            connectionResponse = new ConnectionResponse();
                            ServerAsyncConnection  serverAsyncConnection = new ServerAsyncConnection(HistoryManagerActivity.this);
                            try{
                                serverAsyncConnection.execute(objectRemoveFromServer);
                            }catch (Exception e){
                                Log.i("ERROR",e.getMessage());
                            }
                            for (int sec = 0 ; sec < 10 ; sec++) {
                                try {
                                    Thread.sleep(1000);
                                    Log.i("INFO", "ITERATION:" + sec);
                                    if (connectionResponse.getStatus() != null) {
                                        if (connectionResponse.getStatus().equals("failure")) {
                                            Log.i("INFO", connectionResponse.getError());
                                            break;
                                        } else {

                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }catch(Exception e){
                            Log.i("ERROR",e.getMessage());
                        }
                        Toast.makeText(HistoryManagerActivity.this, rideList.size() + " items removed", Toast.LENGTH_SHORT).show();
                        rideList = null;
                        // reload list after remove
                        //openHistoryList(view);

                        String android_id = android.provider.Settings.Secure.getString(HistoryManagerActivity.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                        String url_suffix = "location/get_trip_list?android_id=" +  android_id;
                        Object object = prepareConnectionObject(url_suffix,null,"GET");

                        if (object != null) {
                            try {
                                //       updateServerTripBackup(object);

                                getNumberOfTripsFromServer(object);

                            } catch (Exception e) {
                                Log.i("ERROR", "CHeckSharingLocation" + e.getMessage());
                            }
                        } else {
                            Toast.makeText(HistoryManagerActivity.this, "Can't connect server", Toast.LENGTH_LONG).show();
                            Log.i("INFO", "The token is NULL, Can't connect to server");
                            return;
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////// SERVER CALL PART //////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onTaskComlete(HashMap<String, String> result) {
        Log.i("INFO","Request Resut " + result.get("status").toString());

        connectionResponse.setStatus(result.get("status").toString());
        if (connectionResponse.getStatus().equals("success")) {
            connectionResponse.setMessage(result.get("message").toString());
        }else {
            connectionResponse.setError(result.get("error").toString());
        }
    }

    public class ServerAsyncConnection extends AsyncTask<Object, Void, HashMap<String,String>> {
        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        public TaskCompleted mCallback;

        public ServerAsyncConnection(Context context){
            this.mCallback = (TaskCompleted) context;
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
            RequestHandler requestHandler = new RequestHandler(HistoryManagerActivity.this.getApplicationContext(),HistoryManagerActivity.this );
            HashMap postResponse = new HashMap<String,String> ();
            try {
                String postResponseStr = requestHandler.sendMethod((URL) obj[0], jsonObject, params.get("method"));
                Log.i("INFO","RESPONSE" + postResponseStr);
                if(postResponseStr != null) {
                    postResponse.put("status", "success");
                    postResponse.put("message", postResponseStr);
                }else{
                    postResponse.put("status","failure");
                    postResponse.put("error","No response from server");
                }

            }catch (Exception e){
                Log.i("ERROR",e.getMessage());
                postResponse.put("status","failure");
                postResponse.put("error",e.getMessage());
            }
            Log.i("INFO","EXIT doInBackgraond");
            mCallback.onTaskComlete(postResponse);
            return postResponse;

        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(HashMap<String,String> result) {
            Log.i("INFO","Request Result " + result.get("status").toString());
            //mCallback.onTaskComlete(result);
            /*connectionResponse.setStatus(result.get("status").toString());
            if (connectionResponse.getStatus().equals("success")) {
                connectionResponse.setMessage(result.get("message").toString());
            }else {
                connectionResponse.setError(result.get("error").toString());
            }*/
            //pd.dismiss();
            //tvData.setText(result);
            // TextView tv_pointParams = (TextView) activity.findViewById(R.id.low_textView_location);
            //((LocationData) Activity.getApplication()).setCurrentAddress(result.get("address"));

        }
    }
    private JSONObject convertJson2Object(String json){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
           // Log.i("INFO",jsonObject.getString("token"));
            //JSONObject jsonObject = new JSONObject("\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6OTQsImlhdCI6MTYwNDQ3ODc4OCwiZXhwIjoxNjA0NTY1MTg4fQ.eDojL9xwKuyVIB5l3Xz-DalBOzNVl4A4y-pOdi6CXFY\",\"func\":\"get_tmp_tkn\",\"code\":\"1000\",\"error\":\"false\"");
            // return jsonObject;
        }catch (JSONException e){
            Log.i("ERROR",e.getMessage());
        }
        return jsonObject;
    }
}