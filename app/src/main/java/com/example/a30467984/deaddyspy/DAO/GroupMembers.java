package com.example.a30467984.deaddyspy.DAO;

public class GroupMembers {
    public static final String TABLE = "Group_members";

    public static final String KEY_ID = "id";
    public static final String KEY_GROUP_ID = "group_id";
    public static final String KEY_MEMBER = "member";
    public static final String KEY_MEMBER_STATUS = "member_status";
    public static final String KEY_VISIBILITY = "visibility";
    public static final String KEY_INSERT_DATE = "insert_date";
    public static final String KEY_LAST_LATITUDE = "last_latitude";
    public static final String KEY_LAST_LONGITUDE = "last_longitude";

    public int groupID;
    public String member;
    public String memberStatus;
    public Double lastLatitude;

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public Double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(Double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public Double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(Double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getTrip_number() {
        return trip_number;
    }

    public void setTrip_number(int trip_number) {
        this.trip_number = trip_number;
    }

    public Double lastLongitude;
    public String insertDate;
    public int visibility;
    public int trip_number;

}
