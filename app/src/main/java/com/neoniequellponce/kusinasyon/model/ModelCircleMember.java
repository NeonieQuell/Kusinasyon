package com.neoniequellponce.kusinasyon.model;

public class ModelCircleMember {

    private String mCircleKey;
    private String mUid;
    private String mRole;

    //Empty Constructor
    public ModelCircleMember() {

    }

    //Constructor
    public ModelCircleMember(String circleKey, String uid, String role) {
        mCircleKey = circleKey;
        mUid = uid;
        mRole = role;
    }

    public String getCircleKey() {
        return mCircleKey;
    }

    public void setCircleKey(String circleKey) {
        mCircleKey = circleKey;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }
}
