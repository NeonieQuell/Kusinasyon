package com.neoniequellponce.kusinasyon.model;

public class ModelIngredient {

    private String mGroup, mName;
    private boolean mIsChecked;

    //Empty Constructor
    public ModelIngredient() {

    }

    //Constructor
    public ModelIngredient(String group, String name) {
        mGroup = group;
        mName = name;
    }

    public String getGroup() {
        return mGroup;
    }

    public String getName() {
        return mName;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }
}
