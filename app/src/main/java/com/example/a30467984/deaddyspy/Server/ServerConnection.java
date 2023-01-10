package com.example.a30467984.deaddyspy.Server;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.NotificationReceiverActivity;
import com.example.a30467984.deaddyspy.maps.DisplayMapWithMarkers;
import com.example.a30467984.deaddyspy.modules.ConnectionResponse;
import com.example.a30467984.deaddyspy.modules.TaskCompleted;
import com.example.a30467984.deaddyspy.utils.RequestHandler;
import com.example.a30467984.deaddyspy.utils.SingleToneAuthToen;
import com.example.a30467984.deaddyspy.utils.SingleToneServerListOfTrips;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import javax.mail.AuthenticationFailedException;

/**
 * Created by 30467984 on 12/1/2019.
 */

public class ServerConnection extends Activity implements TaskCompleted{
    private Context context;
    private Activity activity;
    private ConnectionResponse connectionResponse;
    private static final String AUTH_TKN = "AUTH_TKN";
    private static final String PREF_MY_DADDY = "PREF_MY_DADDY";
    private static final int REQUEST_FROM_MASTER = 1;

    public ServerConnection(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        connectionResponse = new ConnectionResponse();
    }
   // @Override
//    public void onTaskComplete(HashMap<String,String> result){
//
//    }
    ///////////////////////////////////////////////////////////////////
    /// method handle in first initialization get token from server and store it in SharedPPref
    /////////////////////////////////////////////////////////////////////////////////////////
    public void getInitRequest(Object object){

        ServerAsyncConnection serverAsyncConnection = new ServerAsyncConnection(ServerConnection.this);
        //Log.i("INFO",String.valueOf(serverAsyncConnection.getStatus()));
        try {
            serverAsyncConnection.execute(object);
        }catch (Exception e){
            Log.i("ERROR",e.getMessage());
        }
        /////////////////////////////////////////////
        //// waiting this asynk task ended
        ////////////////////////////////////
        for (int sec = 0 ; sec < 10 ; sec++) {
            try {
                Thread.sleep(1000);
                if (connectionResponse.getStatus() != null) {
                    JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());

                    try {
                        switch (jsonObj.getString("func")) {
                            case "first_register":
                                if (jsonObj.getString("token") != null) {
                                    SharedPreferences sharedPrefs = context.getSharedPreferences(
                                            PREF_MY_DADDY, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPrefs.edit();
                                    editor.putString(AUTH_TKN, jsonObj.getString("token"));
                                    editor.commit();
                                    break;
                                }
                            case "get_tmp_tkn":
                                if (jsonObj.getString("token") != null) {
                                    SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();
                                    singleToneAuthToen.setToken(jsonObj.getString("token"));
                                    break;
                                }
                            case "get_sharing_location":
                                if (jsonObj.getString("token") != null) {

                                    break;
                                }
                        }
                        break;
                    } catch (JSONException e) {
                        Log.i("ERROR", e.getMessage());
                    }

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Log.i("INFO",String.valueOf(serverAsyncConnection.getStatus()));
    }

    public void getAuthRequest(Object object) throws AuthenticationFailedException {
        Log.i("INFO","SUBJECT getAuthRequest: " + object.toString());
        new ServerAsyncConnection(ServerConnection.this).execute(object);
        for (int sec = 0 ; sec < 10 ; sec++){
            try {
                Thread.sleep(1000);
                Log.i("INFO","ITERATION:" + sec );
                if (connectionResponse.getStatus() != null) {
                    if (connectionResponse.getStatus().equals("failure")) {
                        Log.i("INFO", connectionResponse.getError());
                        JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                        try {
                            /////////////////////////////////////////////////////////////////////////////////////////
                            //// IF token expired , get new one
                            if (Integer.parseInt(jsonObj.get("code").toString()) != 1000) {
                                Log.i("INFO", jsonObj.getString("message"));
                                throw new AuthenticationFailedException("Token authentication failed");
                            }
                            /////////////////////////////////////////////////////////////////////
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (AuthenticationFailedException e) {
                            throw new AuthenticationFailedException(e.getMessage());
                        }
                        break;
                    }else {

                        JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                        SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();
                        try {
                            singleToneAuthToen.setToken(jsonObj.get("token").toString());
                        } catch (JSONException e) {
                            Log.i("ERROR", e.getMessage());
                        }
                        break;
                    }
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{

                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void getNumberOfTripsFromServer(Object object){
        Log.i("INFO","SUBJECT getAuthRequest: " + object.toString());
        new ServerAsyncConnection(ServerConnection.this).execute(object);
        for (int sec = 0 ; sec < 10 ; sec++){
            try {
                Thread.sleep(1000);
                Log.i("INFO","ITERATION:" + sec );
                if (connectionResponse.getStatus() != null) {
                    if (connectionResponse.getStatus().equals("failure")){
                        Log.i("INFO",connectionResponse.getError()  );
                        break;
                    }else {
                        HashMap listOfTrips = new HashMap();
                        JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                        SingleToneServerListOfTrips singleToneServerListOfTrips = SingleToneServerListOfTrips.getInstance();
                        for (int i = 0; i < jsonObj.length(); i++) {


                            try {
                                listOfTrips.put(jsonObj.get("trip_number").toString(), jsonObj.get("update_date").toString());


                            } catch (JSONException e) {
                                Log.i("ERROR", e.getMessage());
                            }
                            singleToneServerListOfTrips.setListOfTrips(listOfTrips);
                            break;
                        }
                    }
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{

                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    public void updateDaddyServer(Object object){
        Log.i("INFO","SUBJECT updateDaddyServer: " + object.toString());

        new ServerAsyncConnection(ServerConnection.this).execute(object);

       // JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
        try {
            Log.i("INFO","updateDaddyServer");
            //Log.i("INFO", jsonObj.get("token").toString());
        }catch (Exception j){
            Log.i("ERR",j.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkParentRequestExists(Object object){
        new ServerAsyncConnection(ServerConnection.this).execute(object);
        boolean connection_flag = false;
        //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
        for (int sec = 0 ; sec < 3 ; sec++){
            try {
                Thread.sleep(1000);
                Log.i("INFO","ITERATION:" + sec );
                if (connectionResponse.getStatus() != null) {
                    connection_flag = true;
                    if (connectionResponse.getStatus().equals("failure")){
                        Log.i("INFO",connectionResponse.getError()  );
                        break;
                    }else {
                        connection_flag = true;
                        try {
                            JSONObject jsonObj = new JSONObject(connectionResponse.getMessage());
                            if (jsonObj.get("json_result") != null){
                                if ((jsonObj.getString("json_result")) != null){
                                   /* Intent intent = new Intent(this.context, NotificationReceiverActivity.class);
                                    PendingIntent pIntent = PendingIntent.getActivity(this.context, (int) System.currentTimeMillis(), intent, 0);

                                    // Build notification
                                    // Actions are just fake
                                    Notification noti = new Notification.Builder(this.context)
                                            .setContentTitle("New mail from " + "test@gmail.com")
                                            .setContentText("Subject")
                                            .setContentIntent(pIntent)
                                            .build();
                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(0,noti);*/
                                   final String CHANNEL_ID = "HEADS_UP_NOTIFICATIONS";
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"MyNotif",NotificationManager.IMPORTANCE_HIGH);
                                    getSystemService(NotificationManager.class).createNotificationChannel(channel);
                                    Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                                            .setContentTitle("New mail from " + "test@gmail.com")
                                            .setContentText("Subject")
                                            .setAutoCancel(true);
                                    NotificationManagerCompat.from(this).notify(1,notification.build());
                                }

                            }
                        }catch (JSONException e){

                        }

                        //SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();

                        break;
                    }
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{

                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
        if(connection_flag == false ) {
            Toast.makeText(context, "Can't check parent request", Toast.LENGTH_SHORT).show();
        }
    }


    public void getGroupLocation(Object object, HashMap phone2Contact){
        new ServerAsyncConnection(ServerConnection.this).execute(object);
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

                        //SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();
                        try {
                            //jsonObj.put("phone2name",phone2Contact);
                            Intent i = new Intent(this.context, DisplayMapWithMarkers.class);
                            Bundle extras = new Bundle();
                            extras.putString("GroupLocation",connectionResponse.getMessage());
                            extras.putSerializable("phone2ContactName", phone2Contact);
                            i.putExtras(extras);
                            //i.putExtra("GroupLocation",connectionResponse.getMessage());
                            //i.putExtra("phone2ContactName",phone2Contact);
                            // Starts TargetActivity
                            this.context.startActivity(i);
                          //  singleToneAuthToen.setToken(jsonObj.get("token").toString());
                        } catch (Exception e) {
                            Log.i("ERROR", e.getMessage());
                        }
                        break;
                    }
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{

                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
        if(connection_flag == false ) {
            Toast.makeText(context, "Can't fetch groups location from server", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onTaskComlete(HashMap<String, String> result) {
        Log.i("INFO","Request Resut " + result.get("status").toString());

        connectionResponse.setStatus(result.get("status").toString());
        if (connectionResponse.getStatus().equals("success")) {
            connectionResponse.setMessage(result.get("message").toString());
        }else {
            try {
                JSONObject jsonObject = new JSONObject(result.get("message").toString());
                connectionResponse.setError(jsonObject.getString("message"));
                connectionResponse.setMessage(result.get("message").toString());
            } catch (JSONException e) {
                e.printStackTrace();
                connectionResponse.setError("Unknown failure");
            } catch (NullPointerException e){
                e.printStackTrace();
                connectionResponse.setError(result.get("status").toString());

            }

        }


    }

    public class ServerAsyncConnection extends AsyncTask<Object, Void, HashMap<String,String>> {
        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        private TaskCompleted mCallback;

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
            RequestHandler requestHandler = new RequestHandler(context,activity );
            HashMap postResponse = new HashMap<String,String> ();
            try {
                String postResponseStr = requestHandler.sendMethod((URL) obj[0], jsonObject, params.get("method"));
                Log.i("INFO","RESPONSE" + postResponseStr);
                if(postResponseStr != null) {
                    JSONObject jsonResponse = new JSONObject(postResponseStr);
                    if (jsonResponse.getString("error").equals("true")) {
                        postResponse.put("status", "failure");
                        postResponse.put("message", postResponseStr);
                    } else {
                        postResponse.put("status", "success");
                        postResponse.put("message", postResponseStr);
                }
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


}
