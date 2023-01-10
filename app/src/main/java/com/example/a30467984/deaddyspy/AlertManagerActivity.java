package com.example.a30467984.deaddyspy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Alert;
import com.example.a30467984.deaddyspy.DAO.AlertRepo;
import com.example.a30467984.deaddyspy.R;
import com.example.a30467984.deaddyspy.alert.NotificationTools;
import com.example.a30467984.deaddyspy.modules.AlertDetails;
import com.example.a30467984.deaddyspy.modules.NotificationDetails;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by 30467984 on 3/3/2019.
 */

public class AlertManagerActivity extends AppCompatActivity{
    final Context context = this;
    private Button button;
    private ListView alertListView;// = (ListView)findViewById(R.id.alerts_window_list);
    private AlertRepo alertRepo = new AlertRepo(context);
    private ArrayList<String> alertsList = new ArrayList<>();
    private HashSet<String> alertsUniqList = new HashSet<>();
    private ArrayList<String> notificationList = new ArrayList<>();
    private ViewGroup.LayoutParams layoutParams;
    private HashMap alertsDetails = new HashMap();
    private HashMap editParams= new HashMap();
    private Map alertsHash;
    private String selectedAlertName;
    private AlertDetails editAlertDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_configuration);
        alertListView = (ListView)findViewById(R.id.alerts_window_list);
        getAlertList();
//        alertsHash = alertRepo.getAlertList();
//        if(alertsHash.size() > 0){
//            Iterator<String> keySetIterator = alertsHash.keySet().iterator();
//            while (keySetIterator.hasNext()) {
//                String key = keySetIterator.next();
//                //System.out.println("keys " + key + ", value " + alertsHash.get(key));
//                String name = ((HashMap<String, String>) alertsHash.get(key)).get("name");
//                if (!alertsList.contains(name)) {
//                    alertsList.add(name);
//
//                }
//            }
            displayAlertsList();
//        }

    }

    public void getAlertList(){
        alertsList.clear();
        alertsHash = alertRepo.getAlertList();
        if(alertsHash.size() > 0) {
            Iterator<String> keySetIterator = alertsHash.keySet().iterator();
            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                //System.out.println("keys " + key + ", value " + alertsHash.get(key));
                String name = ((HashMap<String, String>) alertsHash.get(key)).get("name");
                if (!alertsList.contains(name)) {
                    alertsList.add(name);

                }
            }
        }
    }

    public void addNewAlert(View view){
        buildAlertDialog();
    }


    public void removeAlert(View view){
        if(selectedAlertName != null){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Do you want to remove " + selectedAlertName + " alert");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            try {
                                alertRepo.deleteAlert(selectedAlertName);
                            }catch(Exception e){
                                Log.i("ERROR",e.getMessage());
                            }
                            Toast.makeText(AlertManagerActivity.this, selectedAlertName + " alert removed", Toast.LENGTH_SHORT).show();
                            getAlertList();
                            displayAlertsList();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

            //alertRepo.deleteAlert(selectedAlertName);
        }
    }

    public void editAlert(View view){
        if(selectedAlertName.equals("")){
            displayAlertDialogOK("Please  select alert");
        }else {
            editAlertDetails = fetchAlertDetails(selectedAlertName);
            buildAlertDialog();
            editAlertDetails = null;
        }
    }


    public void displayAlertsList(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  alertsList){
            @Override
            public View getView (int position, View convertView, ViewGroup parent){
                View view =super.getView(position,convertView,parent);
                layoutParams = view.getLayoutParams();
                layoutParams.height = 110;
                view.setLayoutParams(layoutParams);
                return view;
            }
        };

        alertListView.setAdapter(arrayAdapter);

        alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                selectedAlertName = item;
                Toast.makeText(context, item + "", Toast.LENGTH_SHORT).show();
                AlertDetails alertDetails = fetchAlertDetails(item);
       //         Map curentAlertParams = alertRepo.getAlertParamsByName(item);
                displayAlertDetails(alertDetails);
            }
        });
    }

    public AlertDetails fetchAlertDetails(String alertName){
        Map curentAlertParams = alertRepo.getAlertParamsByName(alertName);
        AlertDetails alertDetails = new AlertDetails();
        NotificationDetails notificationDetails = new NotificationDetails();
        alertDetails.setAlertName(alertName);
        if(curentAlertParams.size() > 0){
            Iterator<String> keySetIterator = curentAlertParams.keySet().iterator();
            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                //System.out.println("keys " + key + ", value " + alertsHash.get(key));
                String name = ((HashMap<String, String>) curentAlertParams.get(key)).get("name");
                String param = ((HashMap<String, String>) curentAlertParams.get(key)).get("param");
                String value = ((HashMap<String, String>) curentAlertParams.get(key)).get("value");
                switch (param) {
                    case "threshold":
                        if (!value.equals("")){
                            alertDetails.setAlertThreshold(Integer.parseInt(value));
                        }
                        break;
                    case "unit":
                        alertDetails.setAlertUnit(value);
                        break;
                    case "status":
                        alertDetails.setAlertStatus(value);
                        break;
                    case "sound":


                        notificationDetails.setSound(value);
                    case "email":
                        notificationDetails.setEmail(value);
                    case "sms":
                        notificationDetails.setSms(value);
                }
                alertDetails.setNotificationDetails(notificationDetails);
            }
        }
        return alertDetails;
    }

    public void buildAlertDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.new_alert_dialoog);

        //dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        //       TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Android custom dialog example!");

        dialog.show();
        final SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBarThreshold);
        final EditText alertName = (EditText) dialog.findViewById(R.id.editTextAlertName);
        alertName.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                // mCrime.setTitle(c.toString());

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
                //alertName.setText("");
            }

            public void afterTextChanged(Editable c) {
                // this one too

            }
        });
        final EditText thresholdET = (EditText) dialog.findViewById(R.id.editTextAlert);
        thresholdET.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                // mCrime.setTitle(c.toString());

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
                if(!c.toString().equals("")) {
                    seekBar.setProgress(Integer.parseInt(thresholdET.getText().toString()));
                }else{
                    seekBar.setProgress(0);
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////
        //// Listener of SEEKKBAR OF ALERT THRESHOLD
        ///////////////////////////////////////////////
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Toast.makeText(context, progress + "", Toast.LENGTH_LONG).show();
                //thresholdET.setText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int length = seekBar.getProgress();
                thresholdET.setText(length + "");
            }
        });

        final EditText emailET = (EditText) dialog.findViewById(R.id.editTextAlertEmail);
        emailET.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                // mCrime.setTitle(c.toString());


            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
//
            }

            public void afterTextChanged(Editable c) {
                // this one too

            }
        });



        ///// CLOSE DIALOG WINDOW //////
        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.alert_radio_group);
        // get selected radio button from radioGroup


        final Button dialogButtonCancel = (Button) dialog.findViewById(R.id.alert_cancel_button);
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ////////////////////////////////////////////////
        ///// SAVE NEW ALERT DETAILS
        final Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog

        ////////////////////////////////////////////////////////////////////////////////
        //// NOTIFICATIN INTERVAL SECTION
        ////////////////////////////////////////////////////////////////////////////
        final SeekBar seekBarAlertInterval = (SeekBar) dialog.findViewById(R.id.seekBarAlertInterval);
        final EditText edittextAlertInterval = (EditText) dialog.findViewById(R.id.editTextAllertInterval);

        edittextAlertInterval.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                // mCrime.setTitle(c.toString());

            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
                if(!c.toString().equals("")) {
                    seekBarAlertInterval.setProgress(Integer.parseInt(edittextAlertInterval.getText().toString()));
                }else{
                    seekBarAlertInterval.setProgress(10);
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////
        //// Listener of SEEKKBAR OF ALERT THRESHOLD
        ///////////////////////////////////////////////
        seekBarAlertInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Toast.makeText(context, progress + "", Toast.LENGTH_LONG).show();
                //thresholdET.setText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBarAlertInterval) {
                int length = seekBarAlertInterval.getProgress();
                edittextAlertInterval.setText(length + "");
            }
        });

        /// FOR EDIT DIALOG bring alert data
        if (editAlertDetails != null){
            alertName.setText(editAlertDetails.getAlertName());
            seekBar.setProgress(editAlertDetails.getAlertThreshold());
            thresholdET.setText(editAlertDetails.getAlertThreshold() + "");
            RadioButton unitRadioButton;
            if(editAlertDetails.getAlertUnit().equals("km")){
                unitRadioButton = dialog.findViewById(R.id.km_radioButton);
            }else{
                unitRadioButton = dialog.findViewById(R.id.percent_radioButton);
            }
            unitRadioButton.setChecked(true);
            //// NOTIFICATION EDIT ////
            /// SOUND NOTIF///
            if (editAlertDetails.getNotificationDetails() != null) {
                if (editAlertDetails.getNotificationDetails().getSound().equals("sound")) {
                    CheckBox soundCheckBoox = dialog.findViewById(R.id.checkboxSoundNotif);
                    soundCheckBoox.setChecked(true);
                }
            }

        }

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thresholdET.getText().toString().equals("")) {
                    displayAlertDialogOK("Please,select alert threshold!");
                }else{
                    dialog.dismiss();
                    Alert alert = new Alert();

                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    final RadioButton radioSelectedButton = (RadioButton) dialog.findViewById(selectedId);
                    Toast.makeText(context, radioSelectedButton.getText(), Toast.LENGTH_SHORT).show();
                    CheckBox soundNotif = (CheckBox) dialog.findViewById(R.id.checkboxSoundNotif);
                    EditText daddyNum = (EditText) dialog.findViewById(R.id.editDaddyNumberAlert);
                    EditText emailET = (EditText) dialog.findViewById(R.id.editTextAlertEmail);;
                    EditText smsET = (EditText) dialog.findViewById(R.id.editTextSMS);
                    EditText intervalET = (EditText) dialog.findViewById(R.id.editTextAllertInterval);

                    // insert in alerts table first aramtr with alert name and detrmined thresholld
                    alert.setName(alertName.getText().toString());
                    alert.setType("alert");
                    alert.setParam("threshold");
                    alert.setValue(thresholdET.getText().toString());
                    alertRepo.insert(alert);
                    // insert second paramtr is percent or numerical
                    alert.setParam("unit");
                    alert.setValue(radioSelectedButton.getText().toString());

                    alertRepo.insert(alert);
                    alert.setParam("status");
                    alert.setValue("on");
                    alertRepo.insert(alert);

                    if (soundNotif.isChecked()) {
                        alert.setType("Notification");
                        alert.setParam("sound");
                        alert.setValue("default");
                        alertRepo.insert(alert);
                    }
                    if (daddyNum.getText() != null){
                        alert.setType("Notification");
                        alert.setParam("daddy_number");
                        alert.setValue(emailET.getText().toString());
                        alertRepo.insert(alert);
                    }
                    if(emailET.getText() != null){
                        alert.setType("Notification");
                        alert.setParam("email");
                        alert.setValue(emailET.getText().toString());
                        alertRepo.insert(alert);
                    }
                    if(smsET.getText() != null){
                        alert.setType("Notification");
                        alert.setParam("sms");
                        alert.setValue(smsET.getText().toString());
                        alertRepo.insert(alert);
                    }
                    if(intervalET.getText() != null){
                        alert.setType("Notification");
                        alert.setParam("interval");
                        alert.setValue(intervalET.getText().toString());
                        alertRepo.insert(alert);
                    }
                    alertsList.add(alertName.getText().toString());
                    displayAlertsList();
                }
            }
        });
    }

    public void displayAlertDetails(AlertDetails alertDetails){
        TextView textView = findViewById(R.id.alert_window_details);
        textView.setText(getString(R.string.alertName)+": " + alertDetails.getAlertName() + "\n");
        textView.append(getString(R.string.threshod) +": " + alertDetails.getAlertThreshold() + " " + alertDetails.getAlertUnit() + "\n");
        if (alertDetails.getNotificationDetails() != null ) {
            textView.append(getString(R.string.notification) + ": \n");
            if(alertDetails.getNotificationDetails().getSound() != null){
                textView.append(  getString(R.string.sound_notification) + ":" + " " + alertDetails.getNotificationDetails().getSound()+"\n");
            }
            if(alertDetails.getNotificationDetails().getDaddyNumber() != null){
                textView.append(  getString(R.string.daddy_number) + ":" + " " + alertDetails.getNotificationDetails().getDaddyNumber()+"\n");
            }
            if(alertDetails.getNotificationDetails().getEmail() != null){
                textView.append(  getString(R.string.email) + ":" + " " + alertDetails.getNotificationDetails().getEmail()+"\n");
            }
            if(alertDetails.getNotificationDetails().getSms() != null){
                textView.append(  getString(R.string.sms) + ":" + " " + alertDetails.getNotificationDetails().getSms()+ "\n");
            }
        }
    }

    private void displayAlertDialogOK(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNegativeButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        return;
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
