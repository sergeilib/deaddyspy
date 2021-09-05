package com.example.a30467984.deaddyspy;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.utils.CsvCreator;

import java.lang.reflect.Array;
import java.net.FileNameMap;
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

public class HistoryManagerActivity extends AppCompatActivity {
    private List<String> fieldList = Arrays.asList("date", "speed","limit","latitude","longitude","place");
    RoutingRepo routingRepo = new RoutingRepo(this);
    ArrayList<String> rideList = new ArrayList<String>();
    SparseBooleanArray sparseBooleanArray ;
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

    public void openHistoryListFromServer(View v) {
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
}