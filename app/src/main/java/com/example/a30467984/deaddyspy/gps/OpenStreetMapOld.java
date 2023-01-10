package com.example.a30467984.deaddyspy.gps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 30467984 on 1/31/2019.
 */

public class OpenStreetMapOld {
    //https://nominatim.openstreetmap.org/reverse?format=xml&lat=32.1853&lon=34.8279&zoom=20&addressdetails=1
    private static String URL_BASE = "https://nominatim.openstreetmap.org/reverse?format=json"; //&lat=32.1853&lon=34.8279';
    private static String URL_OVERPASS = "http://overpass-api.de/api/interpreter?data=way"; //197346870);out;"614552447
    private static HashSet currentAreaIdist;
    private static String place_id;
    private Context context;
    private Activity activity;

    public OpenStreetMapOld(Context context, Activity activity){
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
                urlConnection.setReadTimeout(2000 /* milliseconds */ );
                urlConnection.setConnectTimeout(2000 /* milliseconds */ );
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
                    String current_place_id = null;
                    String osm_type = null;
                    try {
                        JSONObject jObj = new JSONObject(jsonString);
                        osm_id = jObj.getString("osm_id");
                        current_place_id = jObj.getString("place_id");
                        osm_type = jObj.getString("osm_type");
                        resHashMap.put("address",jObj.getString("display_name"));
                        resHashMap.put("boundingbox",jObj.getString("boundingbox"));
                        //String tst = jObj.getString("address");
                        String road = new JSONObject(jObj.getString("address")).getString("road");
                        resHashMap.put("address_road",road);
                        //System.out.println("JSON: " + osm_id);
                    }catch (JSONException e){
                        Log.i("ERROR", e.toString());
                        currentJobj = null;
                    }

                    if (osm_id == null || !osm_type.equals("way")){
                        if(osm_id != null){

                        }
                        return resHashMap;
                    }
                    ///////////////////////////////////////////////////////////////////
                    //BEGIN SECOND HTTP REQUEST
                    ////////////////////////////////////////////////////////////////////
                    //URL url = new URL(URLEncoder.encode(urlString[0],"UTF-8"));
                    //osm_id = "614552447";
 //                   osm_id = "404263910";
                    if (current_place_id.equals(place_id)){
                        // means no maxspeed changes
                        return null;
                    }else{
                        place_id = current_place_id;
                    }
                    String urlOPString = URL_OVERPASS + URLEncoder.encode("(" + osm_id +");out;");
                    try {
                        HttpURLConnection urlConnectionOP = null;
                        URL urlOP = new URL(urlOPString);
                        urlConnectionOP = (HttpURLConnection) urlOP.openConnection();
                        urlConnectionOP.setRequestMethod("GET");
                        //urlConnection.setRequestProperty("Content-Typpe","application/json");
                        urlConnectionOP.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                        urlConnectionOP.setReadTimeout(2000 /* milliseconds */);
                        urlConnectionOP.setConnectTimeout(2000 /* milliseconds */);
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
                                if (sbOP.matches(".*<tag k=\"ref.*")){
                                    Pattern pattern = Pattern.compile("v=\"(\\w+)\"");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("ref",matcher.group(1));
                                    }
                                }
                                if (sbOP.matches(".*maxspeed.*")){
                                    Pattern pattern = Pattern.compile("\\d+");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("maxspeed",matcher.group());
                                    }
                                }
                                if (sbOP.matches(".*k=\"highway.*")){
                                    Pattern pattern = Pattern.compile("v=\"(.*?)\"");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("highway",matcher.group(1));
                                    }
                                }
                                if (sbOP.matches(".*k=\"name:en.*")){
                                    Pattern pattern = Pattern.compile("v=\"(.*?)\"");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("name:en",matcher.group(1));
                                    }
                                }
                                if (sbOP.matches(".*k=\"name:he.*")){
                                    Pattern pattern = Pattern.compile("v=\"(.*?)\"");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("name:he",matcher.group(1));
                                    }
                                }
                                if (sbOP.matches(".*k=\"name:ru.*")){
                                    Pattern pattern = Pattern.compile("v=\"(.*?)\"");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("name:ru",matcher.group(1));
                                    }
                                }
                                if (sbOP.matches(".*k=\"oneway.*")){
                                    Pattern pattern = Pattern.compile("v=\"(.*?)\"");   // the pattern to search for
                                    Matcher matcher = pattern.matcher(sbOP);
                                    if (matcher.find()){
                                        resHashMap.put("oneway",matcher.group(1));
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
            if (result != null) {
                if (result.get("maxspeed") != null) {
                    LocationData.currentMaxSppeed = Integer.parseInt(result.get("maxspeed").toString());

                }
                //LocationData.currentAddress = "" + result.get("address");
                //LocationData.currentAddress = "" + (context.getString(R.string.road)) + result.get("ref") + "\n" +
                //        result.get("name:en") + "\n" + result.get("name:he") + "\n" + result.get("name:ru");
                if (result.get("ref") != null) {
                    if(result.get("ref").toString() != null) {
                        LocationData.currentLocation.put("road", result.get("ref").toString());
                    }else{
                        LocationData.currentLocation.put("road", result.get("address_road").toString());
                    }
                }else{
                    LocationData.currentLocation.put("road", result.get("address_road").toString());
                }
                if(result.get("name:en") != null){
                    LocationData.currentLocation.put("name:en", result.get("name:en").toString());
                }
                if(result.get("name:he") != null){
                    LocationData.currentLocation.put("name:he", result.get("name:he").toString());
                }
                if(result.get("name:ru") != null){
                    LocationData.currentLocation.put("name:ru", result.get("name:ru").toString());
                }
                if(result.get("boundingbox").toString() !=null){
                    LocationData.currentLocation.put("boundingbox",result.get("boundingbox").toString());
                }
                //tv_pointParams.append("\nLlimit: " + result.get("maxspeed"));
                //tv_pointParams.append("\nAddress: " + result.get("address"));
            }
        }
    }
}
