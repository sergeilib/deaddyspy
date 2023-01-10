package com.example.a30467984.deaddyspy.gps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.Server.HttpsTrustManager;
import com.example.a30467984.deaddyspy.modules.Location;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by 30467984 on 1/31/2019.
 */

public class OpenStreetMap {
    //https://nominatim.openstreetmap.org/reverse?format=xml&lat=32.1853&lon=34.8279&zoom=20&addressdetails=1
    private static String URL_BASE = "https://nominatim.openstreetmap.org/reverse?format=json"; //&lat=32.1853&lon=34.8279';
    private static String URL_OVERPASS = "http://overpass-api.de/api/interpreter?data=way"; //197346870);out;"614552447
    private String SERVER_URL = "https://li780-236.members.linode.com:443/api/";
    private static HashSet currentAreaIdist;
    private static String place_id;
    private Context context;
    private Activity activity;
    private static String current_country = null; // this variable help us to create correct URL for http request

    public OpenStreetMap(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private JSONObject currentJobj = new JSONObject();

    public void getOSMData(Double lat, Double lon) {
       String suffix = "&lat=" + lat + "&lon=" + lon;
       //String suffix = "&lat=32.2077564&lon=34.9632369";
//        try {
//            String urlString = URL_BASE + URLEncoder.encode(suffix, "UTF-8");
//            new GetJSONTask().execute(urlString);
//            Log.i("INFO","bb");
//        }catch (UnsupportedEncodingException e){
//            Log.i("ERROR",e.toString());
//        }
        //String urlString = URL_BASE + suffix;
        try {
            //Toast.makeText(context, "call getOsmData: " + suffix, Toast.LENGTH_SHORT).show();
            new GetJSONTask().execute(suffix).get();
        } catch (Exception e) {
            Toast.makeText(context, "call getOsmData failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //JSONObject OSMAddress = getJSONObjectFromURL(urlString);


        //overpass-api.de/api/interpreter?data=way(197346870);out;


    }

    private class GetJSONTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
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
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            super.onPostExecute(result);
            HashMap<String,String> finalLocation = new HashMap<>();
            //pd.dismiss();
            //tvData.setText(result);
            // TextView tv_pointParams = (TextView) activity.findViewById(R.id.low_textView_location);
            //((LocationData) Activity.getApplication()).setCurrentAddress(result.get("address"));
            boolean previousMotorway = false;
            if(LocationData.currentLocation.containsKey("highway")){
                if (LocationData.currentLocation.get("highway").equals("motorway")) {
                    previousMotorway = true;
                }
            }
            if (result != null) {
                boolean compatibility = false;
                boolean motorLinkExist = false;
                for (int i = 0; i < result.size(); i++) {
                    HashMap<String,String> currentLocation = new HashMap<>();
                    ///////////////////////////////////////////////////////////////////////////////////////////
                    /// in case we get more than one location we should compare with previous location in order to
                    /// continiue correct route
                    ///////////////////////////////////////////////////////////////////////////////////


                    if (result.get(i) != null) {
                        HashMap<String, String> hs = (HashMap<String, String>) result.get(i);
                        if (result.size() > 1) {
                            if (LocationData.currentLocation.containsKey("id")) {
                                if (LocationData.currentLocation.get("id").equals(hs.get("id"))) {
                                    compatibility = true;
                                }
                            }
                            if (LocationData.currentLocation.containsKey("ref")) {
                                if (LocationData.currentLocation.get("ref").equals(hs.get("ref"))) {
                                    compatibility = true;
                                }
                            }else if (LocationData.currentLocation.containsKey("name:en")) {
                                if (LocationData.currentLocation.get("name:en").equals(hs.get("name:en"))) {
                                    compatibility = true;
                                }
                            }
                        }
                        if (hs.containsKey("id")) {
                            currentLocation.put("id", hs.get("id").toString());

                        }
                        if (hs.containsKey("name")) {
                            currentLocation.put("name", hs.get("name").toString());

                        }
                        if (hs.containsKey("name:en")) {
                            currentLocation.put("name:en", hs.get("name:en").toString());
//                            LocationData.currentAddress = "testtttttttttttttttt";
                        }
                        if (hs.containsKey("maxspeed")) {
                            currentLocation.put("currentMaxSpeed",hs.get("maxspeed").toString());

                        }

                        if (hs.containsKey("name:he")) {
                            currentLocation.put("name:he", hs.get("name:he").toString());
                        }
                        if (hs.containsKey("name:ru")) {
                            currentLocation.put("name:ru", hs.get("name:ru").toString());
                        }
                        if (hs.containsKey("ref")) {
                            currentLocation.put("ref", hs.get("ref").toString());
                        }
                        if (hs.containsKey("boundingbox")) {
                            currentLocation.put("boundingbox", hs.get("boundingbox").toString());
                        }
                        if (hs.containsKey("highway")) {
                            currentLocation.put("highway", hs.get("highway").toString());
                        }

                        //LocationData.currentLocation = currentLocation;

                        if (compatibility) {
                            //LocationData.currentLocation = currentLocation;
                            break;
                        }

                        if (currentLocation.get("highway").equals("motorway_link")){
                                motorLinkExist = true;
                                //break;
                        }
                        finalLocation = currentLocation;
                    }

                    //tv_pointParams.append("\nLlimit: " + result.get("maxspeed"));
                    //tv_pointParams.append("\nAddress: " + result.get("address"));
                }
                if (compatibility == false){
                    if (previousMotorway == true){

                        if (motorLinkExist){
                            return;
                        }
                    }
                }
                if (finalLocation.get("ref") != null || finalLocation.get("name") != null) {
                    LocationData.currentLocation = finalLocation;
                    if (finalLocation.get("currentMaxSpeed") != null) {

                        LocationData.currentMaxSppeed = Integer.parseInt(finalLocation.get("currentMaxSpeed"));
                    }
                }
            }else{
                Toast.makeText(context, "end of getOsmData empty", Toast.LENGTH_SHORT).show();
            }
           // Toast.makeText(context, "end of getOsmData" + LocationData.currentLocation.toString(), Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... urlString) {
            ArrayList<HashMap<String, String>> arrayListRes = new ArrayList<HashMap<String, String>>();


            String osm_id = null;
            try {
                if (current_country == null) {
                    HttpsTrustManager.allowAllSSL();
                    HttpsURLConnection urlConnection = null;
                    String osm_url = URL_BASE + urlString[0];
                    //URL url = new URL(URLEncoder.encode(urlString[0],"UTF-8"));
                    // URL url = new URL(urlString[0]);
                    URL url = new URL(osm_url);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    //urlConnection.setRequestProperty("Content-Typpe","application/json");
                    urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                    urlConnection.setReadTimeout(2000 /* milliseconds */);
                    urlConnection.setConnectTimeout(2000 /* milliseconds */);
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

                        String current_place_id = null;
                        String osm_type = null;
                        try {
                            JSONObject jObj = new JSONObject(jsonString);
                            osm_id = jObj.getString("osm_id");
                            current_place_id = jObj.getString("place_id");
                            osm_type = jObj.getString("osm_type");
                            //resHashMap.put("address", jObj.getString("display_name"));
                            //resHashMap.put("boundingbox", jObj.getString("boundingbox"));
                            //String tst = jObj.getString("address");
                            String road = new JSONObject(jObj.getString("address")).getString("road");
                            current_country = new JSONObject(jObj.getString("address")).getString("country_code");
                            //resHashMap.put("address_road", road);
                            //System.out.println("JSON: " + osm_id);
                        } catch (JSONException e) {
                            Log.i("ERROR", e.toString());
                            currentJobj = null;
                        }

                        if (osm_id == null || !osm_type.equals("way")) {
                            if (osm_id != null) {

                            }
                            //return resHashMap;
                        }
                        if (current_place_id.equals(place_id)) {
                            // means no maxspeed changes
                            return null;
                        } else {
                            place_id = current_place_id;
                        }
                    }
                    ///////////////////////////////////////////////////////////////////
                    //BEGIN SECOND HTTP REQUEST
                    ////////////////////////////////////////////////////////////////////
                    //URL url = new URL(URLEncoder.encode(urlString[0],"UTF-8"));
                    //osm_id = "614552447";
                    //                   osm_id = "404263910";

                }
                // String urlOPString = SERVER_URL + URLEncoder.encode("(" + osm_id + ");out;");
                String android_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                String url_suffix = "osm/" + current_country + "/fetch_coordinates_data?android_id=" + android_id + urlString[0];
                String urlOPString = SERVER_URL + url_suffix;
                try {
                    HttpURLConnection urlConnectionOP = null;
                    URL urlOP = new URL(urlOPString);
                    urlConnectionOP = (HttpURLConnection) urlOP.openConnection();
                    urlConnectionOP.setRequestMethod("GET");
                    //urlConnection.setRequestProperty("Content-Typpe","application/json");
                    urlConnectionOP.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                    urlConnectionOP.setReadTimeout(3000 /* milliseconds */);
                    urlConnectionOP.setConnectTimeout(3000 /* milliseconds */);
                    //urlConnectionOP.setDoOutput(true);
                    urlConnectionOP.setDoInput(true);
                    urlConnectionOP.connect();
                    int statusCodeOP = urlConnectionOP.getResponseCode();
                    InputStream inputStreamOP = urlConnectionOP.getErrorStream();
                    if (inputStreamOP == null) {
                        inputStreamOP = urlConnectionOP.getInputStream();
                        InputStreamReader inputStreamReaderOP = new InputStreamReader(inputStreamOP);
                        BufferedReader bufferedReaderOP = new BufferedReader(inputStreamReaderOP);
                        String sbOP = bufferedReaderOP.readLine();
                        //String jsonStringOP = sbOP.toString();

                        String jsonString = sbOP.toString();


                        try {
                            JSONObject jsonObjectGlob = new JSONObject(jsonString);
                            JSONArray jsonArrRes = new JSONArray(jsonObjectGlob.get("json_result").toString());
                            for (int i = 0; i < jsonArrRes.length(); i++) {
                                HashMap<String, String> resHashMap = new HashMap<String, String>();


                                // JSONArray jsonArrayElement = jsonArrRes.getJSONArray(i);
                                //JSONObject jObj = jsonArrayElement.getJSONObject(0);
                                JSONObject jObj = jsonArrRes.getJSONObject(i);
                                //JSONObject jObj = new JSONObject(jsonObjectGlob.get("json_result").toString());

                                resHashMap.put("boundingbox", jObj.getString("bounds"));
                                //resHashMap.put("tags",jObj.getString("tags")).toString();
                                //String tst = jObj.getString("address");
                                if (jObj.has("maxspeed")) {
                                    resHashMap.put("maxspeed", jObj.getString("maxspeed"));
                                }
                                if (jObj.getString("tags") != null) {
                                    String tagsString = jObj.getString("tags");
                                    JSONObject jsonTags = (JSONObject) new JSONObject(tagsString);
                                    //String bStr = (String) b.get("maxspeed").toString();
                                    //if (((JSONObject) new JSONObject(jObj.getString("tags"))).getString("maxspeed") != null) {

                                    //if (jsonTags.has("maxspeed")) {
                                    //    resHashMap.put("maxspeed", new JSONObject(jObj.getString("tags")).getString("maxspeed"));
                                    //}
                                    if (jsonTags.has("ref")) {
                                        resHashMap.put("ref", new JSONObject(jObj.getString("tags")).getString("ref"));
                                    }
                                    if (jsonTags.has("highway")) {
                                        resHashMap.put("highway", new JSONObject(jObj.getString("tags")).getString("highway"));
                                    }
                                    if (jsonTags.has("name")) {
                                        resHashMap.put("name", new JSONObject(jObj.getString("tags")).getString("name"));
                                    }
                                    if (jsonTags.has("name:en")) {
                                        resHashMap.put("name:en", new JSONObject(jObj.getString("tags")).getString("name:en"));
                                    }
                                    if (jsonTags.has("name:he")) {
                                        resHashMap.put("name:he", new JSONObject(jObj.getString("tags")).getString("name:he"));
                                    }
                                    if (jsonTags.has("name:ru")) {
                                        resHashMap.put("name:ru", new JSONObject(jObj.getString("tags")).getString("name:ru"));
                                    }
                                    if (jsonTags.has("oneway")) {
                                        resHashMap.put("oneway", new JSONObject(jObj.getString("tags")).getString("oneway"));
                                    }
                                    if (jsonTags.has("surface")) {
                                        resHashMap.put("surface", new JSONObject(jObj.getString("tags")).getString("surface"));
                                    }
                                    if (jsonTags.has("lanes")) {
                                        resHashMap.put("lanes", new JSONObject(jObj.getString("tags")).getString("lanes"));
                                    }
                                }
                                arrayListRes.add(resHashMap);
                                // resHashMap.put("address_road", road);
                            }
                            //System.out.println("JSON: " + osm_id);
                        } catch (JSONException e) {
                            Log.i("ERROR", e.toString());
                            currentJobj = null;
                        }


                    } else {
                        Log.i("Info", "input stream: " + inputStreamOP.read());
                    }
                } catch (IOException e) {
                    Log.i("ERROR", e.toString());
                }
                return arrayListRes;
                //} else {
                //Log.i("ERROR","Status Code " + statusCode );
                //}


                //return new JSONObject(jsonString);

            } catch (IOException e) {
                Log.i("ERROR", "Unable to retrieve data. URL may be invalid.");
                return null;
            }
            // return null;
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.


    }
}
