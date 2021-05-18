package com.example.a30467984.deaddyspy.utils.HotSpot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.background.BackgroundIntentService;

import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;

public class HotSpotHandler {
    private Context context;

    private static int FOREGROUND_ID=1338;
    private static final String CHANNEL_ID = "control_app";

    @RequiresApi(api = Build.VERSION_CODES.O)
    MyOreoWifiManager mMyOreoWifiManager;

    public void HotSpotHandler(Context context){
        this.context = context;

    }

//    private void carryOn() {
//        boolean turnOn = true;
//
//
//            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
//                hotspotOreo(turnOn);
//            } else {
//                turnOnHotspotPreOreo(turnOn);
//            }
//        }
//
//
//
//    private boolean turnOnHotspotPreOreo(boolean turnOn) {
//        {
//            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//            Method[] methods = wifiManager.getClass().getDeclaredMethods();
//            for (Method method : methods) {
//                if (method.getName().equals("setWifiApEnabled")) {
//                    try {
//                        if (turnOn) {
//                            wifiManager.setWifiEnabled(false); //Turning off wifi because tethering requires wifi to be off
//                            method.invoke(wifiManager, null, true); //Activating tethering
//                            return true;
//                        } else {
//                            method.invoke(wifiManager, null, false); //Deactivating tethering
//                            wifiManager.setWifiEnabled(true); //Turning on wifi ...should probably be done from a saved setting
//                            return true;
//                        }
//                    } catch (Exception e) {
//                        return false;
//                    }
//                }
//            }
//
//            //Error setWifiApEnabled not found
//            return false;
//        }
//
//
//    }
//
//    /**
//     *
//     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void hotspotOreo(boolean turnOn){
//
//        if (mMyOreoWifiManager ==null){
//            mMyOreoWifiManager = new MyOreoWifiManager(this.context);
//        }
//
//        if (turnOn) {
//
//            //this dont work
//            MyOnStartTetheringCallback callback = new MyOnStartTetheringCallback() {
//                @Override
//                public void onTetheringStarted() {
//                    startForeground(FOREGROUND_ID,
//                            buildForegroundNotification());
//                }
//
//                @Override
//                public void onTetheringFailed() {
//
//                }
//            };
//
//            mMyOreoWifiManager.startTethering(callback);
//        } else{
//            mMyOreoWifiManager.stopTethering();
//            stopForeground(true);
//            stopSelf();
//        }
//
//    }
//
//    //****************************************************************************************
//
//
//    /**
//     * Build low priority notification for running this service as a foreground service.
//     * @return
//     */
//    private Notification buildForegroundNotification() {
//        //registerNotifChnnl(this.context);
//
//        Intent stopIntent = new Intent(this.context, BackgroundIntentService.class);
//        //stopIntent.setAction(context.getString(R.string.intent_action_turnoff));
//
//        PendingIntent pendingIntent = PendingIntent.getService(this.context,0, stopIntent, 0);
//
//        NotificationCompat.Builder b=new NotificationCompat.Builder(this.context,CHANNEL_ID);
//
//        b.setOngoing(true)
//                .setContentTitle("WifiHotSpot is On")
////                .addAction(new NotificationCompat.Action(
////                        R.drawable.turn_off,
////                        "TURN OFF HOTSPOT",
////                        pendingIntent
////                ))
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .setCategory(Notification.CATEGORY_SERVICE);
//           //     .setSmallIcon(R.drawable.notif_hotspot_black_24dp);
//
//
//        return(b.build());
//    }


//    private static void registerNotifChnnl(Context context) {
//        if (Build.VERSION.SDK_INT >= 26) {
//            NotificationManager mngr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//            if (mngr.getNotificationChannel(CHANNEL_ID) != null) {
//                return;
//            }
//            //
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    context.getString(R.string.notification_chnnl),
//                    NotificationManager.IMPORTANCE_LOW);
//            // Configure the notification channel.
//            channel.setDescription(context.getString(R.string.notification_chnnl_location_descr));
//            channel.enableLights(false);
//            channel.enableVibration(false);
//            mngr.createNotificationChannel(channel);
//        }
//    }
}
