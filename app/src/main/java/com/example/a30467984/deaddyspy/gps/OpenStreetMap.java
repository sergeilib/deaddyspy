package com.example.a30467984.deaddyspy.gps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 30467984 on 1/31/2019.
 */

public class OpenStreetMap   {
    //https://nominatim.openstreetmap.org/reverse?format=xml&lat=32.1853&lon=34.8279&zoom=20&addressdetails=1
    static String URL_BASE = "https://nominatim.openstreetmap.org/reverse?format=json"; //&lat=32.1853&lon=34.8279';
    static String URL_OVERPASS = "http://overpass-api.de/api/interpreter?data=way"; //197346870);out;"614552447
    private Context context;
    private Activity activity;

    public OpenStreetMap(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }
    private JSONObject currentJobj = new JSONObject();
    public void getOSMData(Double lat, Double lon){
        String suffix = "&lat="+ lat + "&lon=" + lon;
//        try {
//            String urlString = URL_BASE + URLEncoder.encode(suffix, "UTF-8");
//            new GetJSONTask().execute(urlString);
//            Log.i("INFO","bb");
//        }catch (UnsupportedEncodingException e){
//            Log.i("ERROR",e.toString());
//        }
        String urlString = URL_BASE + suffix;
        new GetJSONTask().execute(urlString);
            //JSONObject OSMAddress = getJSONObjectFromURL(urlString);


    //overpass-api.de/api/interpreter?data=way(197346870);out;


    }

    private class GetJSONTask extends AsyncTask<String, Void, HashMap> {
        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd = ProgressDialog.show(MainActivity.this, "", "Loading", true,
//                    false); // Create and show Progress dialog
        }

        @Override
        protected HashMap<String, String> doInBackground(String... urlString) {
            HashMap<String, String> resHashMap = new HashMap<String, String>();
            try {
                HttpURLConnection urlConnection = null;

                //URL url = new URL(URLEncoder.encode(urlString[0],"UTF-8"));
                URL url = new URL(urlString[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //urlConnection.setRequestProperty("Content-Typpe","application/json");
                urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                urlConnection.setReadTimeout(3000 /* milliseconds */ );
                urlConnection.setConnectTimeout(3000 /* milliseconds */ );
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                int statusCode = urlConnection.getResponseCode();
                InputStream inputStream = urlConnection.getErrorStream();
                if (inputStream == null) {
                    inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String sb = bufferedReader.readLine();
                    String jsonString = sb.toString();
                    String osm_id = null;
                    try {
                        JSONObject jObj = new JSONObject(jsonString);
                        osm_id = jObj.getString("osm_id");
                        resHashMap.put("address",jObj.getString("display_name"));
                        //System.out.println("JSON: " + osm_id);
                    }catch (JSONException e){
                        Log.i("ERROR", e.toString());
                        currentJobj = null;
                    }
                    if (osm_id == null){
                        return null;
                    }
                    ///////////////////////////////////////////////////////////////////
                    //BEGIN SECOND HTTP REQUEST
                    ////////////////////////////////////////////////////////////////////
                    //URL url = new URL(URLEncoder.encode(urlString[0],"UTF-8"));
              //      osm_id = "614552447";
                    String urlOPString = URL_OVERPASS + URLEncoder.encode("(" + osm_id +");out;");
                    try {
                        HttpURLConnection urlConnectionOP = null;
                        URL urlOP = new URL(urlOPString);
                        urlConnectionOP = (HttpURLConnection) urlOP.openConnection();
                        urlConnectionOP.setRequestMethod("GET");
                        //urlConnection.setRequestProperty("Content-Typpe","application/json");
                        urlConnectionOP.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                        urlConnectionOP.setReadTimeout(5000 /* milliseconds */);
                        urlConnectionOP.setConnectTimeout(5000 /* milliseconds */);
                        //urlConnectionOP.setDoOutput(true);
                        urlConnectionOP.setDoInput(true);
                        urlConnectionOP.connect();
                        int statusCodeOP = urlConnectionOP.getResponseCode();
                        InputStream inputStreamOP = urlConnection.getErrorStream();
                        if (inputStreamOP == null) {
                            inputStreamOP = urlConnectionOP.getInputStream();
                            InputStreamReader inputStreamReaderOP = new InputStreamReader(inputStreamOP);
                            BufferedReader bufferedReaderOP = new BufferedReader(inputStreamReaderOP);
                            String sbOP = bufferedReaderOP.readLine();
                            //String jsonStringOP = sbOP.toString();
                            StringBuilder xml = new StringBuilder();
                            while ((sbOP = bufferedReaderOP.readLine()) != null ){
                                if (sbOP.matches(".*maxspeed.*")){
                                    Pattern pattern = Pattern.compile("\\d+");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("maxspeed",matcher.group());
                                    }
                                }

                                xml.append(sbOP);
                            }
//                            try {
//                                JSONObject jObjOP = new JSONObject(jsonStringOP);
////                                String osm_id = jObj.getString("osm_id");
//                                //System.out.println("JSON: " + osm_id);
//                            } catch (JSONException e) {
//                                Log.i("ERROR", e.toString());
//                                currentJobj = null;
//                            }


                        }
                    }catch (IOException e){
                        Log.i("ERROR",e.toString());
                    }
                    return resHashMap;
                }else{
                    Log.i("ERROR","Status Code " + statusCode );
                }


                //return new JSONObject(jsonString);

            } catch (IOException e) {
                Log.i("ERROR","Unable to retrieve data. URL may be invalid.");
                return null;
            }
            return null;
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        //@Override
        protected void onPostExecute(HashMap result) {
            //pd.dismiss();
            //tvData.setText(result);
           // TextView tv_pointParams = (TextView) activity.findViewById(R.id.low_textView_location);
            //((LocationData) Activity.getApplication()).setCurrentAddress(result.get("address"));

            if (result.get("maxspeed") != null) {
                LocationData.currentMaxSppeed = Integer.parseInt(result.get("maxspeed").toString());;
            }
            LocationData.currentAddress = "" + result.get("address");
            //tv_pointParams.append("\nLlimit: " + result.get("maxspeed"));
            //tv_pointParams.append("\nAddress: " + result.get("address"));
        }
    }
}
