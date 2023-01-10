package com.example.a30467984.deaddyspy.alert;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.net.MailTo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.AlertManagerActivity;
import com.example.a30467984.deaddyspy.DAO.Alert;
import com.example.a30467984.deaddyspy.DAO.AlertLog;
import com.example.a30467984.deaddyspy.DAO.AlertRepo;
import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.HistoryManagerActivity;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.ShowSpeedometer;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.AlertDetails;
import com.example.a30467984.deaddyspy.modules.ConnectionResponse;
import com.example.a30467984.deaddyspy.modules.DateConversion;
import com.example.a30467984.deaddyspy.modules.TaskCompleted;
import com.example.a30467984.deaddyspy.utils.Common;
import com.example.a30467984.deaddyspy.utils.CsvCreator;
import com.example.a30467984.deaddyspy.utils.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static android.view.Gravity.BOTTOM;

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

    public void checkCurrentSpeed(Point point, int limit) {
        int speed = point.speed;
        //Toast.makeText(context, "Start Alert",Toast.LENGTH_SHORT).show();
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
                            checkNotifications(alertDetails,point,limit);
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

    public void checkNotifications(Map alertsDetais, Point point,int limit) {
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
        ////////////////////////////////////////////////////////////////////
        //// if configured daddy number  app will update the server in case of speed alert
        /// //////////////////////////////////////////////////////////////////////////
        if(alertsDetais.containsKey("daddy_number")){
            String message = getDaddyNumMessage(alertsDetais);

            //tools.sendEmailAuto(alertsDetais.get("daddy_number").toString(),"Speed Allert",message);
            speedAlertUpdateServer(point,limit);
        }

        if(alertsDetais.containsKey("email")){
            String message = getEmailMessage(alertsDetais);

            tools.sendEmailAuto(alertsDetais.get("email").toString(),"Speed Allert",message);
        }
        if(alertsDetais.containsKey("sms")){
            String message = getSMSMessage();
        }


    }

    public String getDaddyNumMessage(Map alertDetails){
        return "bla";
    }

    public String getEmailMessage(Map alertDetails){

        return "bla";
    }

    public String getSMSMessage(){

        return "bla";
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// update server with points atributes and limit
    //////////////////////////////////////////////////////////////////////////
    private void speedAlertUpdateServer(Point point, int limit){
        String android_id = android.provider.Settings.Secure.getString(this.context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        String url_suffix = "location/get_selected_trip?android_id=" +  android_id;
        Object object = prepareConnectionObject(url_suffix,null,"POST");

        if (object != null) {

        } else {
            Toast.makeText(this.context, "Can't connect server", Toast.LENGTH_LONG).show();
            Log.i("INFO", "The token is NULL, Can't connect to server");
            return;
        }

        Log.i("INFO","SUBJECT getAuthRequest: " + object.toString());
        ConnectionResponse connectionResponse = new ConnectionResponse();
        ServerAsyncConnection serverAsyncConnection = new ServerAsyncConnection(context);
        try{
            serverAsyncConnection.execute(object);
        }catch (Exception e){
            Log.i("ERROR",e.getMessage());
        }


    }

    public Object prepareConnectionObject(String url_suffix, JSONObject body,String method){
        String tkn = Common.getSessionToken();
        if (tkn == null){
            //ServerConnection serverConnection = new ServerConnection(context, activity);
            //serverConnection.getAuthRequest(object);
            return null;
        }
        Object[] object = new Object[2];
        URL url = null;
        try {

            url = new URL(Common.getBaseUrlFromConfigProperties(context) + url_suffix +"&token=" + tkn );
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
            RequestHandler requestHandler = new RequestHandler(context,AlertTools.this.activity );
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
