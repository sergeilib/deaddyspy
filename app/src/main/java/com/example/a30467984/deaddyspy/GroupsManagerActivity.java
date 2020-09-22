package com.example.a30467984.deaddyspy;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Alert;
import com.example.a30467984.deaddyspy.DAO.AlertRepo;
import com.example.a30467984.deaddyspy.DAO.Group;
import com.example.a30467984.deaddyspy.DAO.GroupRepo;
import com.example.a30467984.deaddyspy.DAO.Point;
import com.example.a30467984.deaddyspy.DAO.RoutingRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.gps.LocationData;
import com.example.a30467984.deaddyspy.modules.AlertDetails;
import com.example.a30467984.deaddyspy.modules.GroupDetails;
import com.example.a30467984.deaddyspy.modules.NotificationDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class GroupsManagerActivity extends AppCompatActivity {
    final Context context = this;
    private Button button;
    private ListView groupListView;// = (ListView)findViewById(R.id.alerts_window_list);
    private GroupRepo groupRepo = new GroupRepo(context);
    private ArrayList<String> groupsList = new ArrayList<>();
    private HashSet<String> groupsUniqList = new HashSet<>();
    private ViewGroup.LayoutParams layoutParams;
    private HashMap groupsDetails = new HashMap();
    private HashMap editParams= new HashMap();
    private Map groupsHash;
    private String selectedGroupName;
    private GroupDetails editGroupDetails;
    public static final int REQUEST_READ_CONTACTS = 79;
    public ListView list;
    public ArrayList mobileArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DatabaseHelper db = new DatabaseHelper(this);
        setContentView(R.layout.groups_configuration);
        Settings settings = new Settings();
        SettingsRepo settingsRepo = new SettingsRepo(this);
//        settingsRepo.dropSettingsTable();

        HashMap settingsList = settingsRepo.getSettingsList();

    }

    public void getGroupsList(){
        groupsList.clear();
        groupsHash = groupRepo.getGroupsList();
        if(groupsHash.size() > 0) {
            Iterator<String> keySetIterator = groupsHash.keySet().iterator();
            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                //System.out.println("keys " + key + ", value " + alertsHash.get(key));
                String name = ((HashMap<String, String>) groupsHash.get(key)).get("name");
                if (!groupsList.contains(name)) {
                    groupsList.add(name);

                }
            }
        }
    }

    public void addNewGroup(View view){
        buildGroupDialog();
    }


    public void removeGroup(View view){
        if(selectedGroupName != null){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Do you want to remove " + selectedGroupName + " alert");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            try {
                                groupRepo.deleteGroup(selectedGroupName);
                            }catch(Exception e){
                                Log.i("ERROR",e.getMessage());
                            }
                            Toast.makeText(GroupsManagerActivity.this, selectedGroupName + " alert removed", Toast.LENGTH_SHORT).show();
                           // getAlertList();
                            displayGroupsList();
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

    public void editGroup(View view){
        if(selectedGroupName.equals("")){
            displayGroupDialogOK("Please  select group");
        }else {
            editGroupDetails = fetchGroupDetails(selectedGroupName);
            buildGroupDialog();
            editGroupDetails = null;
        }
    }


    public void displayGroupsList(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  groupsList){
            @Override
            public View getView (int position, View convertView, ViewGroup parent){
                View view =super.getView(position,convertView,parent);
                layoutParams = view.getLayoutParams();
                layoutParams.height = 110;
                view.setLayoutParams(layoutParams);
                return view;
            }
        };

        groupListView.setAdapter(arrayAdapter);

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                selectedGroupName = item;
                Toast.makeText(context, item + "", Toast.LENGTH_SHORT).show();
                GroupDetails groupDetails = fetchGroupDetails(item);
                //         Map curentAlertParams = alertRepo.getAlertParamsByName(item);
                //displayGroupDetails(groupsDetails);
            }
        });
    }

    public GroupDetails fetchGroupDetails(String groupName){
        Map curentAlertParams = groupRepo.getGroupParamsByName(groupName);
        GroupDetails groupDetails = new GroupDetails();

        groupDetails.setGroupName(groupName );


        return groupDetails;
    }

    public void buildGroupDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.new_group_dialoog);

        //dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        //       TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Android custom dialog example!");

        dialog.show();

        final EditText groupName = (EditText) dialog.findViewById(R.id.new_group_name_input);
        groupName.addTextChangedListener(new TextWatcher() {

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

        ///////////////////////////////////////////////////////////////////////
        //// Listener of SEEKKBAR OF ALERT THRESHOLD
        ///////////////////////////////////////////////

        final Button dialogButtonCancel = (Button) dialog.findViewById(R.id.group_dialog_cancel_button);
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ////////////////////////////////////////////////
        ///// SAVE NEW ALERT DETAILS
        final Button dialogButtonOk = (Button) dialog.findViewById(R.id.group_dialog_ok_button);
        // if button is clicked, close the custom dialog

        EditText et = dialog.findViewById(R.id.new_group_name_input);
        final String new_group_name = et.getText().toString();
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new_group_name.isEmpty()) {
                    Toast.makeText(context, "plz enter new group ", Toast.LENGTH_SHORT).show();
                }else{
                    Group group = new Group();
                    group.setName(new_group_name);
                    groupRepo.insert(group);
                    groupsList.add(new_group_name);
                    getContactList(context);
                }
                //dialog.dismiss();
 //               displayGroupsList();
            }

        });
    }

    public void getContactList(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.contacts_list);

        //dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        //       TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Android custom dialog example!");

        dialog.show();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllContacts();
        } else {
            requestPermission();
        }
        list = dialog.findViewById(R.id.contacts_list_listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mobileArray);
        list.setAdapter(adapter);

        final Button dialogButtonCancel = (Button) dialog.findViewById(R.id.contact_select_cancel);
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ////////////////////////////////////////////////
        ///// SAVE NEW ALERT DETAILS
        final Button dialogButtonOk = (Button) dialog.findViewById(R.id.contact_select_ok);
        // if button is clicked, close the custom dialog

        //EditText et = dialog.findViewById(R.id.contacts_list_listview);
        //final String new_group_name = et.getText().toString();
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                            TextView textView = (TextView) view.findViewById(android.R.id.text1
                            );
                            String text = textView.getText().toString();
                            Toast.makeText(getBaseContext(), "chosen " + text, Toast.LENGTH_SHORT).show();
                        }
                        });

                //dialog.dismiss();
                //               displayGroupsList();
            }

        });
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    private ArrayList getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                nameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return nameList;
    }

    public void displayGroupDetails(Call.Details groupDetails){
        TextView textView = findViewById(R.id.alert_window_details);
//        textView.setText(getString(R.string.alertName)+": " + groupDetails.getGroupName() + "\n");
//        textView.append(getString(R.string.threshod) +": " + groupDetails    s.getAlertThreshold() + " " + alertDetails.getAlertUnit() + "\n");

    }

    private void displayGroupDialogOK(String message){
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
