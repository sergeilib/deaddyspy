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
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a30467984.deaddyspy.DAO.Group;
import com.example.a30467984.deaddyspy.DAO.GroupRepo;
import com.example.a30467984.deaddyspy.DAO.Settings;
import com.example.a30467984.deaddyspy.DAO.SettingsRepo;
import com.example.a30467984.deaddyspy.modules.GroupDetails;
import com.example.a30467984.deaddyspy.DAO.GroupMembers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.lang.String.valueOf;

public class GroupsManagerActivity extends AppCompatActivity {
    final Context context = this;
    private Button button;
    private ListView groupListView;// = (ListView)findViewById(R.id.alerts_window_list);
    private GroupRepo groupRepo = new GroupRepo(context);
    private ArrayList<String> groupsList = new ArrayList<>();
    private HashSet<String> groupsUniqList = new HashSet<>();
    private ViewGroup.LayoutParams layoutParams;

    private HashMap<String,HashMap<String,ArrayList<String>>> groupObj = new HashMap<String, HashMap<String, ArrayList<String>>>();
    private HashMap editParams= new HashMap();

    private Map groupsHash;
    private String selectedGroupName;
    private GroupDetails editGroupDetails;
    private GroupMembers groupMembers;
    public static final int REQUEST_READ_CONTACTS = 79;
    public ListView list;
    public ArrayList mobileArray = new ArrayList();
    public  HashMap mobileHash;
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
                   // groupRepo.insert(group);
                    groupsList.add(new_group_name);
                    //int group_id = groupRepo.insert(group);
                    int group_id = 1;
                    getContactList(context,group_id);


                }
                //dialog.dismiss();
 //               displayGroupsList();
            }

        });
    }

    public void getContactList(final Context context, final int group_id){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.contacts_list);

        //dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        //       TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Android custom dialog example!");

        dialog.show();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileHash = getAllContacts();
        } else {
            requestPermission();
        }
        list = dialog.findViewById(R.id.contacts_list_listview);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, mobileArray);
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
        final HashMap<String,String> phonePerContact = new HashMap<>();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1
                );
                list.setItemChecked(position,true);
                String text = textView.getText().toString();
                HashMap bbc = (HashMap) mobileHash.get(String.valueOf(id));

                Set<String> keys =  bbc.keySet();

                //Object arr = groupObj.get(String.valueOf(i)).keySet().toArray();
                Iterator<String> it = keys.iterator();
                // Displaying keys. Output will not be in any particular order
                String key = it.next();
                ArrayList<String> cntactsPhones= new ArrayList<>();
                cntactsPhones = (ArrayList<String>) ((HashMap) mobileHash.get(String.valueOf(id))).get(key);
                if (cntactsPhones.size() > 1 ){
                    String chosenPhone = dispalayPhonesPerCntact(context,cntactsPhones);
                    phonePerContact.put(key,chosenPhone);
                }
                Toast.makeText(getBaseContext(), "chosen " + text, Toast.LENGTH_SHORT).show();
            }
        });

        final Button dialogButtonOk = (Button) dialog.findViewById(R.id.contact_select_ok);
        // if button is clicked, close the custom dialog

        //EditText et = dialog.findViewById(R.id.contacts_list_listview);
        //final String new_group_name = et.getText().toString();
            dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = list.getCheckedItemPositions();
                for (int i = 0; i < checked.size() ; i++)
                    if (checked.get(i)) {
                        if (groupObj.get(valueOf(i)) != null){
                        //String item = mobileHash.get(i).toString();
                            //String item = mobileHash.ke
                            Log.i("INFO","" + groupObj.get(valueOf(i)));
                            HashMap bb = groupObj.get(valueOf(i));

                            Set<String> keys =  bb.keySet();

                            //Object arr = groupObj.get(String.valueOf(i)).keySet().toArray();
                            Iterator<String> it = keys.iterator();
                            // Displaying keys. Output will not be in any particular order
                            String key = it.next();
//                            ArrayList<String> cntactsPhones= new ArrayList<>();
//                            cntactsPhones = groupObj.get(String.valueOf(i)).get(key);
//
//                            for (int j = 0 ; j < cntactsPhones.size();j++) {
//                                String key = groupObj.get(String.valueOf(i)).keySet().toString();
                                //ArrayList contactList = groupObj.get(String.valueOf(i)).get(key);
                                //String phone_num = contactList.get(j).toString();
                                //editGroupDetails.setGroupName(key);
                                groupMembers.setGroupID(group_id);
                                groupMembers.setMember(phonePerContact.get(key));
                                groupMembers.setMemberName(key);
                                groupMembers.setVisibility(true);

                                groupRepo.insertGroupMember(groupMembers);
                                Toast.makeText(getBaseContext(), "chosen " + i + " ; phone: " + phonePerContact.get(key), Toast.LENGTH_SHORT).show();
                            //}
                        }
                        /* do whatever you want with the checked item */
                    }


                //dialog.dismiss();
                //               displayGroupsList();
            }

        });
    }


    private String dispalayPhonesPerCntact(final Context context, ArrayList phones){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.contacts_list);
        dialog.show();
        final ListView listView=(ListView)findViewById(R.id.contacts_list_listview);

        ArrayList arrayList=new ArrayList<>(Arrays.asList(phones));
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, phones);
        //list.setAdapter(adapter);
        //adapter=new ArrayAdapter<String>(this,R.layout.si,R.id.textView2,arrayList);
        listView.setAdapter(adapter);
        //txtInput=(EditText)findViewById(R.id.txtinput);
        //Button btadd=(Button)findViewById(R.id.btadd);
        final Button dialogButtonCancel = (Button) findViewById(R.id.contact_select_cancel);
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final Button dialogButtonOk = (Button) dialog.findViewById(R.id.contact_select_ok);
        final String chosenPhone = null;
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = 0; i < checked.size() ; i++)
                    if (checked.get(i)) {

                    }
                //chosenPhone = checked.toString();
                //String newitem=txtInput.getText().toString();
                //arrayList.add(newitem);


            }
        });
        return chosenPhone;
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
                    mobileHash = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    private HashMap getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int counter = 0;
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                HashMap groupsDetails = new HashMap();
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
                    ArrayList<String> phones = new ArrayList<String>();
                    HashSet<String> uniqNumbers = new HashSet<>();

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        // insert in ist  unique numbers only
                        if (uniqNumbers.contains(phoneNo) == false) {
                            phones.add(phoneNo);
                            uniqNumbers.add(phoneNo);
                        }
                        /////////////////////////////////////
                    }
                    mobileArray.add(name);
                        groupsDetails.put(name,phones);

                    pCur.close();
                }
                groupObj.put(valueOf(counter),groupsDetails);
                counter++;
            }
        }
        if (cur != null) {
            cur.close();
        }
        return groupObj;
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
