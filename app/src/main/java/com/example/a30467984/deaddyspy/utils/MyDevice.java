package com.example.a30467984.deaddyspy.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.MainActivity;
import com.example.a30467984.deaddyspy.R;

import java.util.UUID;


public class MyDevice {
    private Context context;
    private Activity activity;
    private String myDevicePhone;
    private static final String PREF_MY_DADDY = "PREF_MY_DADDY";
    private static final String PREF_DEVICE_PHONE = "PREF_DEVICE_PHONE";

    public MyDevice(Context context,Activity activity){
        this.context = context;
        this.activity = activity;

    }

    public String getCountryDialCode(){
        String contryId = null;
        String contryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        contryId = telephonyMngr.getSimCountryIso().toUpperCase();
        String[] arrContryCode=context.getResources().getStringArray(R.array.DialingCountryCode);
        for(int i=0; i<arrContryCode.length; i++){
            String[] arrDial = arrContryCode[i].split(",");
            if(arrDial[1].trim().equals(contryId)){
                contryDialCode = arrDial[0];
                break;
            }
        }
        return contryDialCode;
    }

    public String getPhoneNumber() {
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);


        ///TelephonyManager mTelephony = null;
        if ((int) Build.VERSION.SDK_INT < 23) {
            //this is a check for build version below 23
            mTelephony = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);


        } else {
            //this is a check for build version above 23
            if (ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this.activity,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        101);

//                Log.e("INOF", " permission not granted");
//                if (ContextCompat.checkSelfPermission(this.getBaseContext(), Manifest.permission.READ_PHONE_STATE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    return null;
//                }else {
//                    mTelephony = (TelephonyManager) this.getBaseContext().getSystemService(
//                            Context.TELEPHONY_SERVICE);
//                }


            } else {
                mTelephony = (TelephonyManager) context.getSystemService(
                        Context.TELEPHONY_SERVICE);
                Log.i("INFO", "If Permission is granted");
            }


        }
        if (mTelephony != null) {


            String country_code = mTelephony.getSimCountryIso();
            String networ_oper = mTelephony.getNetworkCountryIso();

            //String code = activity.getApplicationContext().getResources().getConfiguration().locale.getCountry();
            //Locale locale = Locale.getDefault();
            //String country_code_dd = locale.getCountry();
            String phone = mTelephony.getLine1Number();
            return mTelephony.getLine1Number();

        }else{
            return null;
        }

    }

    public String getFullPhoneNumber(){
        String phone = checkPhoneOnDisk();
        if (phone == null) {
            phone = getPhoneNumber();
            if (phone == null) {
                //phone = askForDevicePhoneNumber();
                return null;
            }
        }
        String dialCoode = getCountryDialCode();
        if (phone.startsWith(dialCoode)){
            return phone;
        }
        if(phone.startsWith("0")){
            phone = phone.replaceFirst("^0","");
        }
        return "+" + dialCoode + phone;
    }

//    public String askForDevicePhoneNumber() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
//        alertDialog.setTitle("My Phone number");
//        alertDialog.setMessage("Enter Phone number");
//
////        final EditText input = new EditText(context);
////        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
////                LinearLayout.LayoutParams.MATCH_PARENT,
////                LinearLayout.LayoutParams.MATCH_PARENT);
////        input.setLayoutParams(lp);
//        //alertDialog.setView();
//        //alertDialog.setIcon(R.drawable.key);
//
//        alertDialog.setPositiveButton("YES",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        //myDevicePhone = input.getText().toString();
//                        //if (myDevicePhone.compareTo("") == 0) {
//
//                        //}
//                    }
//                });
//
//        alertDialog.setNegativeButton("NO",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//        alertDialog.show();
//
//        return myDevicePhone;
//    }

    public String checkPhoneOnDisk(){
        SharedPreferences sharedPrefs = context.getSharedPreferences(
                PREF_MY_DADDY, Context.MODE_PRIVATE);
        String phone = sharedPrefs.getString(PREF_DEVICE_PHONE, null);

        return phone;
    }

    public void savePhoneOnDisk(String phone){

        if (phone != null ) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_MY_DADDY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(PREF_DEVICE_PHONE, phone);
            editor.commit();

        }
    }
}
