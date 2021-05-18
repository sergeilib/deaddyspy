package com.example.a30467984.deaddyspy.background;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.utils.HotSpot.LocalOnlyHotSpotManager;
import com.example.a30467984.deaddyspy.utils.HotSpot.MyOnStartTetheringCallback;
import com.example.a30467984.deaddyspy.utils.HotSpot.MyOreoWifiManager;
import com.example.a30467984.deaddyspy.utils.MyDevice;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

public class BackgroundIntentService extends IntentService {
    private int notificationId = 1;
    Activity activity = new Activity();
    MyDevice myDevice;
    LocalOnlyHotSpotManager localOnlyHotSpotManager;
    @RequiresApi(api = Build.VERSION_CODES.O)
    MyOreoWifiManager mMyOreoWifiManager;
    private static int FOREGROUND_ID=1338;
    private static final String CHANNEL_ID = "control_app";
    Context context;
    HotSpot hotSpot;
    public BackgroundIntentService(){
        
        super("BackgroundIntentService");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("BackgroundIntentService", "Service running");
        // NotificationCompat Builder takes care of backwards compatibility and
        // provides clean API to create rich notifications
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle("Check service intent")
//                .setContentText("Date: " + Calendar.getInstance().getTime());
//
//        // Obtain NotificationManager system service in order to show the notification
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(notificationId, mBuilder.build());
        myDevice = new MyDevice(this.context,activity);
        //localOnlyHotSpotManager = new LocalOnlyHotSpotManager(getApplicationContext());
        //hotSpot = new HotSpot(getApplicationContext());

        // BackgroundHandler backgroundHandler = new BackgroundHandler(this.context,activity,20000,myDevice.getAppUUID());
        try {
            // backgroundHandler.jobsHandler();

            //startActivity(new Intent(Settings.),0);
            carryOn();
        }catch (RuntimeException e){
            Log.i("ERR",e.getMessage());
        }
    }

    private void carryOn() {
        boolean turnOn = true;


        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){

            ///Intent tetheringSettings = new Intent();

            //startActivity(new Intent(Settings.ACTION_SETTINGS));
            ((Activity) getBaseContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ///localOnlyHotSpotManager.turnOnHotspot();
            //hotspotOreo(turnOn);
            //hotSpot.turnOnHotspot();
        } else {
            turnOnHotspotPreOreo(turnOn);
        }
    }



    private boolean turnOnHotspotPreOreo(boolean turnOn) {
        {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            Method[] methods = wifiManager.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("setWifiApEnabled")) {
                    try {
                        if (turnOn) {
                            wifiManager.setWifiEnabled(false); //Turning off wifi because tethering requires wifi to be off
                            method.invoke(wifiManager, null, true); //Activating tethering
                            return true;
                        } else {
                            method.invoke(wifiManager, null, false); //Deactivating tethering
                            wifiManager.setWifiEnabled(true); //Turning on wifi ...should probably be done from a saved setting
                            return true;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                }
            }

            //Error setWifiApEnabled not found
            return false;
        }


    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void hotspotOreo(boolean turnOn){

        if (mMyOreoWifiManager ==null){

            mMyOreoWifiManager = new MyOreoWifiManager(getApplicationContext());
        }

        if (turnOn) {

            //this dont work
            MyOnStartTetheringCallback callback = new MyOnStartTetheringCallback() {
                @Override
                public void onTetheringStarted() {
                    startForeground(FOREGROUND_ID,
                            buildForegroundNotification());
                }

                @Override
                public void onTetheringFailed() {

                }
            };

            mMyOreoWifiManager.startTethering(callback);
        } else{
            mMyOreoWifiManager.stopTethering();
            stopForeground(true);
            stopSelf();
        }

    }

    //****************************************************************************************


    /**
     * Build low priority notification for running this service as a foreground service.
     * @return
     */
    private Notification buildForegroundNotification() {
        //registerNotifChnnl(this.context);

        Intent stopIntent = new Intent(this.context, BackgroundIntentService.class);
        //stopIntent.setAction(context.getString(R.string.intent_action_turnoff));

        PendingIntent pendingIntent = PendingIntent.getService(this.context,0, stopIntent, 0);

        NotificationCompat.Builder b=new NotificationCompat.Builder(this.context,CHANNEL_ID);

        b.setOngoing(true)
                .setContentTitle("WifiHotSpot is On")
//                .addAction(new NotificationCompat.Action(
//                        R.drawable.turn_off,
//                        "TURN OFF HOTSPOT",
//                        pendingIntent
//                ))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE);
        //     .setSmallIcon(R.drawable.notif_hotspot_black_24dp);


        return(b.build());
    }
}
