package com.neoniequellponce.kusinasyon.model;

import java.util.List;

public class ModelIngredientGroup {

    private String mName;
    private List<String> mMembers;

    //Empty Constructor
    public ModelIngredientGroup() {

    }

    //Constructor
    public ModelIngredientGroup(String name, List<String> members) {
        mName = name;
        mMembers = members;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getMembers() {
        return mMembers;
    }

    public void setMembers(List<String> members) {
        mMembers = members;
    }
}
