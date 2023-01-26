package com.neoniequellponce.kusinasyon.holder;

import com.neoniequellponce.kusinasyon.model.ModelIngredient;

import java.util.ArrayList;
import java.util.List;

public class HolderIngredients {

    private static HolderIngredients sInstance;

    private List<ModelIngredient>
            mBakingProductsList,
            mDairyAndEggsList,
            mBreadAndSaltySnacksList,
            mCondimentsAndRelishesList,
            mFruitsList,
            mGrainsAndCerealsList,
            mHerbsAndSpicesList,
            mMeatsList,
            mOilsAndFatsList,
            mPastaList,
            mSeedsAndNutsList,
            mVegetablesList,
            mSeafoodsAndSeaweedsList,
            mSugarAndSugarProductsList,
            mWinesBeersAndSpiritsList,
            mAddOnsList;

    private List<ModelIngredient> mCheckedIngredientList;

    //Constructor
    private HolderIngredients() {
        mBakingProductsList = new ArrayList<>();
        mDairyAndEggsList = new ArrayList<>();
        mBreadAndSaltySnacksList = new ArrayList<>();
        mCondimentsAndRelishesList = new ArrayList<>();
        mFruitsList = new ArrayList<>();
        mGrainsAndCerealsList = new ArrayList<>();
        mHerbsAndSpicesList = new ArrayList<>();
        mMeatsList = new ArrayList<>();
        mOilsAndFatsList = new ArrayList<>();
        mPastaList = new ArrayList<>();
        mSeedsAndNutsList = new ArrayList<>();
        mVegetablesList = new ArrayList<>();
        mSeafoodsAndSeaweedsList = new ArrayList<>();
        mSugarAndSugarProductsList = new ArrayList<>();
        mWinesBeersAndSpiritsList = new ArrayList<>();
        mAddOnsList = new ArrayList<>();

        mCheckedIngredientList = new ArrayList<>();
    }

    public static HolderIngredients getInstance() {
        if (sInstance == null) sInstance = new HolderIngredients();
        return sInstance;
    }

    public static void setInstanceToNull() {
        if (sInstance != null) sInstance = null;
    }

    public List<ModelIngredient> getBakingProductsList() {
        return mBakingProductsList;
    }

    public void setBakingProductsList(List<ModelIngredient> bakingProductsList) {
        mBakingProductsList = bakingProductsList;
    }

    public List<ModelIngredient> getDairyAndEggsList() {
        return mDairyAndEggsList;
    }

    public void setDairyAndEggsList(List<ModelIngredient> dairyAndEggsList) {
        mDairyAndEggsList = dairyAndEggsList;
    }

    public List<ModelIngredient> getBreadAndSaltySnacksList() {
        return mBreadAndSaltySnacksList;
    }

    public void setBreadAndSaltySnacksList(List<ModelIngredient> breadAndSaltySnacksList) {
        mBreadAndSaltySnacksList = breadAndSaltySnacksList;
    }

    public List<ModelIngredient> getCondimentsAndRelishesList() {
        return mCondimentsAndRelishesList;
    }

    public void setCondimentsAndRelishesList(List<ModelIngredient> condimentsAndRelishesList) {
        mCondimentsAndRelishesList = condimentsAndRelishesList;
    }

    public List<ModelIngredient> getFruitsList() {
        return mFruitsList;
    }

    public void setFruitsList(List<ModelIngredient> fruitsList) {
        mFruitsList = fruitsList;
    }

    public List<ModelIngredient> getGrainsAndCerealsList() {
        return mGrainsAndCerealsList;
    }

    public void setGrainsAndCerealsList(List<ModelIngredient> grainsAndCerealsList) {
        mGrainsAndCerealsList = grainsAndCerealsList;
    }

    public List<ModelIngredient> getHerbsAndSpicesList() {
        return mHerbsAndSpicesList;
    }

    public void setHerbsAndSpicesList(List<ModelIngredient> herbsAndSpicesList) {
        mHerbsAndSpicesList = herbsAndSpicesList;
    }

    public List<ModelIngredient> getMeatsList() {
        return mMeatsList;
    }

    public void setMeatsList(List<ModelIngredient> meatsList) {
        mMeatsList = meatsList;
    }

    public List<ModelIngredient> getOilsAndFatsList() {
        return mOilsAndFatsList;
    }

    public void setOilsAndFatsList(List<ModelIngredient> oilsAndFatsList) {
        mOilsAndFatsList = oilsAndFatsList;
    }

    public List<ModelIngredient> getPastaList() {
        return mPastaList;
    }

    public void setPastaList(List<ModelIngredient> pastaList) {
        mPastaList = pastaList;
    }

    public List<ModelIngredient> getSeedsAndNutsList() {
        return mSeedsAndNutsList;
    }

    public void setSeedsAndNutsList(List<ModelIngredient> seedsAndNutsList) {
        mSeedsAndNutsList = seedsAndNutsList;
    }

    public List<ModelIngredient> getVegetablesList() {
        return mVegetablesList;
    }

    public void setVegetablesAndGreensList(List<ModelIngredient> vegetablesList) {
        mVegetablesList = vegetablesList;
    }

    public List<ModelIngredient> getSeafoodsAndSeaweedsList() {
        return mSeafoodsAndSeaweedsList;
    }

    public void setSeafoodsAndSeaweedsList(List<ModelIngredient> seafoodsAndSeaweedsList) {
        mSeafoodsAndSeaweedsList = seafoodsAndSeaweedsList;
    }

    public List<ModelIngredient> getSugarAndSugarProductsList() {
        return mSugarAndSugarProductsList;
    }

    public void setSugarAndSugarProductsList(List<ModelIngredient> sugarAndSugarProductsList) {
        mSugarAndSugarProductsList = sugarAndSugarProductsList;
    }

    public List<ModelIngredient> getWinesBeersAndSpiritsList() {
        return mWinesBeersAndSpiritsList;
    }

    public void setWinesBeersAndSpiritsList(List<ModelIngredient> winesBeersAndSpiritsList) {
        mWinesBeersAndSpiritsList = winesBeersAndSpiritsList;
    }

    public List<ModelIngredient> getAddOnsList() {
        return mAddOnsList;
    }

    public void setAddOnsList(List<ModelIngredient> addOnsList) {
        mAddOnsList = addOnsList;
    }

    public List<ModelIngredient> getCheckedIngredientList() {
        return mCheckedIngredientList;
    }

    public void setCheckedIngredientList(List<ModelIngredient> checkedIngredientList) {
        mCheckedIngredientList = checkedIngredientList;
    }
}
