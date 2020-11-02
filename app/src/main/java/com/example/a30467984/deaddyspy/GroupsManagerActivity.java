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
    private String chosenPhoneLast;
    private HashMap<String,HashMap<String,ArrayList<String>>> groupObj = new HashMap<String, HashMap<String, ArrayList<String>>>();
    private HashMap editParams= new HashMap();

    private Map groupsHash;
    private HashMap groupsHashByNameKey = new HashMap();
    private String selectedGroupName;
    private GroupDetails editGroupDetails = new GroupDetails();

    private GroupMembers groupMembers = new GroupMembers();
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
        groupListView = (ListView)findViewById(R.id.group_window_list);
        HashMap settingsList = settingsRepo.getSettingsList();
        getGroupsList();
        displayGroupsList();

    }

    public void getGroupsList(){
        groupsList.clear();
        groupsHash = groupRepo.getGroupsList();
        ///// create hashmap group nmae to group id
        Iterator it = groupsHash.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            HashMap tmp = (HashMap) groupsHash.get(String.valueOf(pair.getKey()));
            groupsHashByNameKey.put(tmp.get("name"),tmp.get("id"));
        }

        /////////////////////////////////////////////////////////////
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
            builder1.setMessage("Do you want to remove " + selectedGroupName + " group");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            try {
                                groupRepo.deleteGroupMemberByGrouId(Integer.parseInt(groupsHashByNameKey.get(selectedGroupName).toString()));
                                groupRepo.deleteGroup(selectedGroupName);
                            }catch(Exception e){
                                Log.i("ERROR",e.getMessage());
                            }
                            Toast.makeText(GroupsManagerActivity.this, selectedGroupName + " group removed", Toast.LENGTH_SHORT).show();
                            getGroupsList();
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
            //editGroupDetails = fetchGroupDetails(selectedGroupName);
            getContactList(context,Integer.parseInt(groupsHashByNameKey.get(selectedGroupName).toString()));
            editGroupDetails = null;
        }
    }

    public void showGroupOnMap(View view){
        if (selectedGroupName != null) {

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
                //GroupDetails groupDetails = fetchGroupDetails(item);
                //         Map curentAlertParams = alertRepo.getAlertParamsByName(item);
                Log.i("INFO",groupsHashByNameKey.get(item).toString());
                displayGroupDetails(Integer.parseInt(groupsHashByNameKey.get(item).toString()));
            }
        });
    }

//    public GroupDetails fetchGroupDetails(String groupName){
//        Map curentGroupParams = groupRepo.getGroupParamsByName(groupName);
//        GroupDetails groupDetails = new GroupDetails();
//
//        groupDetails.setGroupName(groupName );
//        groupDetails.setGroupID(Integer.parseInt(curentGroupParams.get("group_id").toString()));
//        groupDetails.setMemberName(curentGroupParams.get("member_name").toString());
//
//        return groupDetails;
//    }

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

        final EditText et = dialog.findViewById(R.id.new_group_name_input);
        final String new_group_name;
        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                //new_group_name = s.toString();
                // yourEditText...
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et.getText().toString().isEmpty()) {
                    Toast.makeText(context, "plz enter new group ", Toast.LENGTH_SHORT).show();
                }else{
                    Group group = new Group();
                    group.setName(et.getText().toString());
                   // groupRepo.insert(group);
                    groupsList.add(et.getText().toString());
                    int group_id = groupRepo.insert(group);

                    getContactList(context,group_id);


                }
                dialog.dismiss();
                displayGroupsList();
            }

        });
    }
    ////////////////////////////////////////////////////////////////////////
    /////  getContactist - method for selecting wantable members for grooup
    ///// if contact has more than one number will be ened dialog box to select
    ///// relevant number
    /////////////////////////////////////////////////////////////////////
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
                //Iterator iterator = checked.
                HashMap groupDetails = new HashMap();
                for (int i = 0; i < checked.size() ; i++) {
                    Log.i("INFO",String.valueOf(checked.keyAt(i)));
                    if (checked.valueAt(i)) {
                        if (groupObj.get(String.valueOf(checked.keyAt(i))) != null) {

                            Log.i("INFO", "" + groupObj.get(checked.keyAt(i)));
                            HashMap bb = groupObj.get(String.valueOf(checked.keyAt(i)));
                            Set<String> keys = bb.keySet();

                            Iterator<String> it = keys.iterator();
                            // Displaying keys. Output will not be in any particular order
                            String key = it.next();

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
                }
                dialog.dismiss();
                displayGroupDetails(group_id);
            }

        });
    }


    private String dispalayPhonesPerCntact(final Context context, ArrayList phones){
        final Dialog dialog = new Dialog(context);
        final ArrayList phonesArr = phones;
        dialog.setContentView(R.layout.contacts_list);
        dialog.show();
        final ListView listView=(ListView)dialog.findViewById(R.id.contacts_list_listview);

        ArrayList arrayList=new ArrayList<>(Arrays.asList(phones));
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, phones);
        //list.setAdapter(adapter);
        //adapter=new ArrayAdapter<String>(this,R.layout.si,R.id.textView2,arrayList);
        listView.setAdapter(adapter);
        //txtInput=(EditText)findViewById(R.id.txtinput);
        //Button btadd=(Button)findViewById(R.id.btadd);
        final Button dialogButtonCancel = (Button) dialog.findViewById(R.id.contact_select_cancel);
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
            public void onClick(View v) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++){
                    if (checked.get(i)) {
                         //Log.i("INFO",phonesArr.get(checked.keyAt(i)).toString());
                         chosenPhoneLast = phonesArr.get(checked.keyAt(i)).toString();
                    }

                //chosenPhone = checked.toString();
                //String newitem=txtInput.getText().toString();
                //arrayList.add(newitem);
                    dialog.dismiss();
                }
            }
        });
        return chosenPhoneLast;
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
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        int counter = 0;
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                HashMap groupsDetails = new HashMap();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    nameList.add(name);
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
                    groupObj.put(String.valueOf(counter),groupsDetails);
                    counter++;
                }


            }
        }
        if (cur != null) {
            cur.close();
        }
        return groupObj;
    }

    public void displayGroupDetails(int group_id){
        Map groupDetails = groupRepo.getGroupParamsByGroupId(group_id);
        Iterator it = groupDetails.entrySet().iterator();
        ArrayList membersList = new ArrayList();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            HashMap tmp = (HashMap) pair.getValue();
            //groupsHashByNameKey.put(tmp.get("name"),tmp.get("id"));
            membersList.add(tmp.get("member_name"));
        }
        ListView listView = findViewById(R.id.group_window_details);
//        textView.setText(getString(R.string.alertName)+": " + groupDetails.getGroupName() + "\n");
//        textView.append(getString(R.string.threshod) +": " + groupDetails    s.getAlertThreshold() + " " + alertDetails.getAlertUnit() + "\n");
        ArrayList arrayList=new ArrayList<>(Arrays.asList(membersList));
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, membersList);
        //list.setAdapter(adapter);
        //adapter=new ArrayAdapter<String>(this,R.layout.si,R.id.textView2,arrayList);
        listView.setAdapter(adapter);
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
