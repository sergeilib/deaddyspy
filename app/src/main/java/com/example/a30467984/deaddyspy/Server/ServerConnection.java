package com.example.a30467984.deaddyspy.Server;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;

import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.ConnectionResponse;
import com.example.a30467984.deaddyspy.modules.TaskCompleted;
import com.example.a30467984.deaddyspy.utils.RequestHandler;
import com.example.a30467984.deaddyspy.utils.SingleToneAuthToen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 30467984 on 12/1/2019.
 */

public class ServerConnection extends Activity implements TaskCompleted{
    private Context context;
    private Activity activity;
    private ConnectionResponse connectionResponse;
    private static final String AUTH_TKN = "AUTH_TKN";
    private static final String PREF_MY_DADDY = "PREF_MY_DADDY";

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

    public void getAuthRequest(Object object){

        new ServerAsyncConnection(ServerConnection.this).execute(object);
        for (int sec = 0 ; sec < 10 ; sec++){
            try {
                Thread.sleep(1000);
                Log.i("INFO","ITERATION:" + sec );
                if (connectionResponse.getStatus() != null) {
                    JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                    SingleToneAuthToen singleToneAuthToen = SingleToneAuthToen.getInstance();
                    try {
                        singleToneAuthToen.setToken(jsonObj.get("token").toString());
                    }catch (JSONException e){
                        Log.i("ERROR",e.getMessage());
                    }
                    break;
                    //JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
                }else{

                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }



    }

    public void updateDaddyServer(Object object){
        new ServerAsyncConnection(ServerConnection.this).execute(object);

        JSONObject jsonObj = convertJson2Object(connectionResponse.getMessage());
        try {
            Log.i("INFO","updateDaddyServer");
            Log.i("INFO", jsonObj.get("token").toString());
        }catch (JSONException j){
            Log.i("ERR",j.getMessage());
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
            connectionResponse.setError(result.get("error").toString());
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
