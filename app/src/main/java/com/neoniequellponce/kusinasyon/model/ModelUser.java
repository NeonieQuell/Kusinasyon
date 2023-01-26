package com.neoniequellponce.kusinasyon.model;

public class ModelUser {

    private String mUid;
    private String mEmail;
    private String mDisplayName;
    private String mPhotoUrl;

    //Empty Constructor
    public ModelUser() {

    }

    //Constructor
    public ModelUser(String uid, String email, String displayName, String photoUrl) {
        mUid = uid;
        mEmail = email;
        mDisplayName = displayName;
        mPhotoUrl = photoUrl;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }
}
