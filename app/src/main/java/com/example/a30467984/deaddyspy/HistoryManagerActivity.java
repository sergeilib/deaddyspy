package com.example.a30467984.deaddyspy;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.gps.LocationData;

import java.lang.reflect.Array;
import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 30467984 on 8/13/2018.
 */

public class HistoryManagerActivity extends AppCompatActivity {
    RoutingRepo routingRepo = new RoutingRepo(this);

    SparseBooleanArray sparseBooleanArray ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_history);
    }


    public void openLastRide(View v) {

        makeButtonsInvisible();
        FrameLayout historyFR = (FrameLayout) findViewById(R.id.historyFrameLayout);
        LinearLayout tableBG = (LinearLayout) findViewById(R.id.table_background_layout);
        TableLayout tl = (TableLayout) findViewById(R.id.ride_table);
        tl.setVisibility(View.VISIBLE);
        int lastRideNum = routingRepo.getMaxTripNumber();

        final ArrayList lasttripData = routingRepo.getLocationListByTripNumber(lastRideNum);
        buildHeader(lasttripData, tl);

        buildTable(lasttripData, tl);

        Button bt = new Button(this);
        bt.setText("map");

        bt.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
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
        for (int i = 1; i < tripData.size();i++){
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

            while(it.hasNext()){
                String hmKey = (String)it.next();
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

        while(it.hasNext()){
            String hmKey = (String)it.next();
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

        makeButtonsInvisible();
        listview = (ListView)findViewById(R.id.history_list_view);
        listview.setVisibility(View.VISIBLE);
        final ArrayList tripList = routingRepo.getStartingLocationList();
        //final String[] listViewItems = new String[tripList.size()];
        final List<String> listViewItems = new ArrayList<String>();

        for (int i = 0; i < tripList.size();i++){
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

                sparseBooleanArray = listview.getCheckedItemPositions();

                String ValueHolder = "" ;

                int i = 0 ;

                while (i < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(i)) {

                        ValueHolder += listViewItems.get(position) + ",";
                    }

                    i++ ;
                }

                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
                HashMap<String,String> chosenData = (HashMap<String,String>) tripList.get(position);
                String a = chosenData.get("trip_number");
                Toast.makeText(HistoryManagerActivity.this, "ListView Selected Values = " + ValueHolder +
                        " Trip: " + a  , Toast.LENGTH_LONG).show();

            }
        });



    }

    public void makeButtonsInvisible(){
        Button last_ride_bt = findViewById(R.id.last_ride_button);
        Button history_list_bt = findViewById(R.id.history_list_button);
        last_ride_bt.setVisibility(View.GONE);
        history_list_bt.setVisibility(View.GONE);
    }

    public void openMap(View v,ArrayList rideData){
        // Button openMapBT = findViewById(R.id.open_map_button);

        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("RideData", rideData);
        // Starts TargetActivity
        startActivity(i);

    }
}