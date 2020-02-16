package com.example.a30467984.deaddyspy.Server;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;

import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.utils.RequestHandler;

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

public class ServerConnection extends Activity{
    private Context context;
    private Activity activity;
    public ServerConnection(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }
    public String getRequest(Object object, String method){

        switch (method){
            case "POST":

                new ServerAsyncConnection().execute(object);
            break;
        }
        return "";
    }

    private class ServerAsyncConnection extends AsyncTask<Object, Void, String> {
        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            pd = ProgressDialog.show(MainActivity.this, "", "Loading", true,
//                    false); // Create and show Progress dialog
        }

        @Override
        protected String doInBackground(Object... objects) {
            Object[] obj = (Object[]) objects[0];
            URL url = (URL)obj[0];
            HashMap<String,String> params = (HashMap<String,String>)obj[1];
            JSONObject jsonObject = new JSONObject(params);
            RequestHandler requestHandler = new RequestHandler(context,activity );
            try {
                requestHandler.sendPost((URL) obj[0], jsonObject);
            }catch (Exception e){
                Log.i("ERROR",e.getMessage());
            }
            return "";

        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        //@Override
        protected void onPostExecute(HashMap result) {
            //pd.dismiss();
            //tvData.setText(result);
            // TextView tv_pointParams = (TextView) activity.findViewById(R.id.low_textView_location);
            //((LocationData) Activity.getApplication()).setCurrentAddress(result.get("address"));

        }
    }
}
