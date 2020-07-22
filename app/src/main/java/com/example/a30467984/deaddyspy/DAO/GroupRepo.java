package com.example.a30467984.deaddyspy.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class GroupRepo {
    private DatabaseHelper databaseHelper;

    public GroupRepo(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public int insert(Group group) {

        //Open connection to write data
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Group.KEY_name, group.name);



        // Inserting Row
        long setting_id = db.insert(Group.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) setting_id;
    }
    public Map getGroupsList() {
        //Open connection to read only
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Group.KEY_ID + "," +
                Group.KEY_name +
                 " FROM " + Group.TABLE;

        //Student student = new Student();
        Map<String, HashMap<String,String>> groupHash = new HashMap<String, HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {


                HashMap<String,String> s = new HashMap<String, String>();
                s.put("id",cursor.getString(cursor.getColumnIndex(Group.KEY_ID)));
                s.put("name",cursor.getString(cursor.getColumnIndex(Group.KEY_name)));
                groupHash.put(cursor.getString(cursor.getColumnIndex(Alert.KEY_ID)),s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return groupHash;

    }

    public Map getGroupParamsByName(String name){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                GroupMembers.KEY_ID + "," +
                GroupMembers.KEY_MEMBER + "," +
                GroupMembers.KEY_MEMBER_STATUS + "," +
                GroupMembers.KEY_VISIBILITY + "," +
                GroupMembers.KEY_LAST_LONGITUDE  +
                GroupMembers.KEY_LAST_LATITUDE  +
                " FROM " + GroupMembers.TABLE +
                " WHERE " +
                GroupMembers.KEY_GROUP_ID + "=?";

        //Student student = new Student();
        //ArrayList<HashMap<String, String>> alertHash = new ArrayList<HashMap<String, String>>();
        Map<String,HashMap<String,String>> groupMemberstHash = new HashMap<String, HashMap<String, String>>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {""+name});
        if (cursor.moveToFirst()) {
            do {


                HashMap<String,String> s = new HashMap<String, String>();
                s.put("group_id",cursor.getString(cursor.getColumnIndex(GroupMembers.KEY_ID)));
                s.put("member",cursor.getString(cursor.getColumnIndex(GroupMembers.KEY_MEMBER)));
                s.put("member_status",cursor.getString(cursor.getColumnIndex(GroupMembers.KEY_MEMBER_STATUS)));
                s.put("visibility",cursor.getString(cursor.getColumnIndex(GroupMembers.KEY_VISIBILITY)));
                groupMemberstHash.put(cursor.getString(cursor.getColumnIndex(GroupMembers.KEY_ID)),s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return groupMemberstHash;
    }

    /*public Map getAlertValuesByName(String name){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Alert.KEY_ID + "," +
                Alert.KEY_Param + "," +
                Alert.KEY_value + "," +
                Alert.KEY_name + "," +
                Alert.KEY_type  +
                " FROM " + Alert.TABLE +
                " WHERE " +
                Alert.KEY_name + "=?";

        //Student student = new Student();
        //ArrayList<HashMap<String, String>> alertHash = new ArrayList<HashMap<String, String>>();
        Map<String,String> alertHash = new HashMap<String, String>();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {""+name});
        if (cursor.moveToFirst()) {
            do {


//                HashMap<String,String> s = new HashMap<String, String>();
//                s.put("value",cursor.getString(cursor.getColumnIndex(Alert.KEY_value)));
//                s.put("type",cursor.getString(cursor.getColumnIndex(Alert.KEY_type)));
//                s.put("param",cursor.getString(cursor.getColumnIndex(Alert.KEY_Param)));
//                s.put("name",cursor.getString(cursor.getColumnIndex(Alert.KEY_name)));
//                alertHash.put(cursor.getString(cursor.getColumnIndex(Alert.KEY_ID)),s);
                alertHash.put(cursor.getString(cursor.getColumnIndex(Alert.KEY_Param)),cursor.getString(cursor.getColumnIndex(Alert.KEY_value)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alertHash;
    }*/

    public void dropGroupTable(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Group.TABLE);
    }

    public void dropGroupMembersTable(){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + GroupMembers.TABLE);
    }

    public void updateGroups(String param,String value) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Group.KEY_name, value);


        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(Group.TABLE, values, Group.KEY_ID + " = ? ",new String[] {param});
        db.close(); // Closing database connection
    }
    public void deleteGroup(String name) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Group.TABLE, Group.KEY_ID + "= ?", new String[] { name });
        db.close(); // Closing database connection
    }

    public void deleteGroupMember(int groupId, int memberID) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(GroupMembers.TABLE, GroupMembers.KEY_ID + "= ?", new String[] { String.valueOf(groupId) });
        db.close(); // Closing database connection
    }
}
