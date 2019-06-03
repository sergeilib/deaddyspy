package com.example.a30467984.deaddyspy.alert;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.net.MailTo;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Alert;
import com.example.a30467984.deaddyspy.DAO.AlertLog;
import com.example.a30467984.deaddyspy.DAO.AlertRepo;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.AlertDetails;
import com.example.a30467984.deaddyspy.modules.DateConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 30467984 on 3/13/2019.
 */

public class AlertTools {
    private final Context context;
    private AlertRepo alertRepo;
    private ArrayList<String> alertsList = new ArrayList<>();
    private Map alertsHash;
    private Activity activity;
    private DateConversion dateConversion = new DateConversion();
    //private HashMap alertsStatusHash;


    public AlertTools(Context context,Activity activity) {
        this.context = context;
        this.activity = activity;
        alertRepo = new AlertRepo(context);
    }

    public HashMap fetchAlertsName() {
        alertsHash = alertRepo.getAlertList();
        HashMap alertsStatusHash = new HashMap();
        if (alertsHash.size() > 0) {
            Iterator<String> keySetIterator = alertsHash.keySet().iterator();
            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                //System.out.println("keys " + key + ", value " + alertsHash.get(key));
                String name = ((HashMap<String, String>) alertsHash.get(key)).get("name");
                if (!alertsList.contains(name)) {
                    alertsList.add(name);

                }
                /// build hash of alerts name and status
                if (((HashMap<String, String>) alertsHash.get(key)).get("param").equals("status")) {
                    alertsStatusHash.put(name, ((HashMap<String, String>) alertsHash.get(key)).get("value"));
                }
            }
        }
        return alertsStatusHash;
    }

    public void checkCurrentSpeed(int speed, int limit) {
        Toast.makeText(context, "Start Alert",Toast.LENGTH_SHORT).show();
        if (speed > 0 && limit > 0 && LocationData.lastState.equals("good")) {
            LocationData.lastState = "bad";
            HashMap<String, String> alertsStatusHash = fetchAlertsName();

            if (alertsStatusHash.size() > 0) {
                for (Object name : alertsStatusHash.keySet()) {
                    /// Check each alert status
                    if (alertsStatusHash.get(name).equals("on")) {
                        Map alertDetails = alertRepo.getAlertValuesByName(name.toString());
                        alertDetails.put("alertName",name);
                        if (checkIfAlertOverLimit(speed, limit, alertDetails)) {
                            checkNotifications(alertDetails);
                        }
                    }

                }
            }
        }
    }

    public boolean checkIfAlertOverLimit(int speed, int limit, Map alertDetails) {

        String unit = alertDetails.get("unit").toString();
        int threshold = Integer.parseInt(alertDetails.get("threshold").toString());
        if (unit.equals("%")) {
            threshold = calculatePercentLimit(limit, threshold);
        }
        if ((speed - limit) > threshold) {
            setFrameColor(R.drawable.border_red);
            return true;
        }else{
            setFrameColor(R.drawable.border_yelow);

        }

        return false;
    }

    public void setFrameColor(int color){
        FrameLayout frame = (FrameLayout) activity.findViewById(R.id.high_speed_fragment);
//        ShapeDrawable shapeDrawable  = new ShapeDrawable(new RectShape());
//        shapeDrawable.getPaint().setColor(color);
//        shapeDrawable.getPaint().setStrokeWidth(3);
//        frame.setBackground(shapeDrawable);
        frame.setBackgroundResource(color);
    }

    public int calculatePercentLimit(int speed, int percent) {
        int numericalLimit = speed * percent / 100;
        return numericalLimit;
    }

    /////////////////////////////////////////
    //// CHECK ALERT FOR ALL NOTIFICATIOONS METHODS

    public void checkNotifications(Map alertsDetais) {
        NotificationTools tools = new NotificationTools(context);
        if (alertsDetais.containsKey("sound")) {
            tools.getSoundNotification();
        }

        if(alertsDetais.containsKey("interval")){
            AlertLog alertLog = new AlertLog(context);
            String lastUpdateDate = alertLog.getLastUpdatedByName(alertsDetais.get("alertName").toString());
            if (lastUpdateDate != null) {
                double interval = dateConversion.calculateDatesDiff(dateConversion.getCurrentDateTime(), lastUpdateDate);
                //////////////////////////////////////////////////////////////////
                /// Check if defined interval is grate then calculated interval
                //////////////
                if (interval < Integer.parseInt(alertsDetais.get("interval").toString())) {
                    return;
                }
            }
        }else{
            return;
        }

        if(alertsDetais.containsKey("email")){
            String message = getEmailMessage(alertsDetais);

            tools.sendEmailAuto(alertsDetais.get("email").toString(),"Speed Allert",message);
        }
        if(alertsDetais.containsKey("sms")){
            String message = getSMSMessage();
        }

    }

    public String getEmailMessage(Map alertDetails){

        return "bla";
    }

    public String getSMSMessage(){

        return "bla";
    }
}
