package com.neoniequellponce.kusinasyon.model;

public class ModelSharedRecipe {

    private String mKey;
    private String mAuthorUid;

    //Constructor
    public ModelSharedRecipe() {

    }

    public ModelSharedRecipe(String key, String authorUid) {
        mKey = key;
        mAuthorUid = authorUid;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getAuthorUid() {
        return mAuthorUid;
    }

    public void setAuthorUid(String authorUid) {
        mAuthorUid = authorUid;
    }
}
