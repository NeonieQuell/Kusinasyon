package com.neoniequellponce.kusinasyon.model;

public class ModelOption {

    private int mIcon;
    private String mName;

    //Empty Constructor
    public ModelOption() {

    }

    //Constructor
    public ModelOption(int icon, String name) {
        mIcon = icon;
        mName = name;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
