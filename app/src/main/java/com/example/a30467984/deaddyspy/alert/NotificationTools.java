package com.example.a30467984.deaddyspy.alert;

import android.app.Activity;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.MailTo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.AlertManagerActivity;
import com.example.a30467984.deaddyspy.DAO.AlertLog;
import com.example.a30467984.deaddyspy.DAO.LogParams;
import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.ShowSpeedometer;
import com.example.a30467984.deaddyspy.utils.Mailing;

import java.net.URI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by 30467984 on 3/17/2019.
 */

public class NotificationTools {
    private final Context context;
    public NotificationTools(Context context){
        this.context = context;
    }
    public AlertLog alertLog;


    public void getSoundNotification(){
        try {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Notification mNotif = new Notification.Builder(context).setContentTitle("BZ").setSound(soundUri).build();
            Ringtone ringtone = RingtoneManager.getRingtone(context, soundUri);
            ringtone.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void sendEmail(String mailTo,String subject,String message) {
        Log.i("Send email", "");
        String[] TO = {mailTo};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        //emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            context.startActivity(emailIntent);

        //    Log.i("Finished sending email.", "");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this.context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
      //  updateLogTable("email",mailTo,message);
    }

    public void sendEmailAuto(String mailTo,String subject,String message){
        try {
            Mailing sender = new Mailing(context);
            sender.sendMail(mailTo,subject,message);
            Toast.makeText(context,"Mail has been sent.",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context,"Error! Try again later.",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLogTable(String method,String dest,String message){
        LogParams logPparams = new LogParams();
        logPparams.setDate(getDateTime());
        logPparams.setMethod(method);
        logPparams.setDestination(dest);///?????
        logPparams.setMessage(message);
        alertLog.insert(logPparams);
    }
    private String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
