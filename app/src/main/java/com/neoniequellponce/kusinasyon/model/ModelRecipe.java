package com.neoniequellponce.kusinasyon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ModelRecipe implements Parcelable {

    private String mKey;
    private String mName;
    private String mDescription;
    private String mImageUrl;
    private String mOrigin;
    private String mAuthor;
    private String mAuthorUid;
    private String mPrivacy;
    private String mStatus;
    private String mVisibility;
    private List<String> mInstructions;
    private List<String> mKeyIngredients;
    private List<String> mIngredients;

    //For public recipe, users approval
    private List<String> mVoters;
    private List<String> mApprovers;
    private List<String> mRejectors;

    //For private recipe, key of circle
    private String mCircleKey;

    //Empty Constructor
    public ModelRecipe() {

    }

    //Constructor
    public ModelRecipe(String key, String name, String description, String imageUrl, String origin,
                       String author, String authorUid, String privacy, String status,
                       String visibility, List<String> instructions, List<String> keyIngredients,
                       List<String> ingredients) {
        mKey = key;
        mName = name;
        mDescription = description;
        mImageUrl = imageUrl;
        mOrigin = origin;
        mAuthor = author;
        mAuthorUid = authorUid;
        mPrivacy = privacy;
        mStatus = status;
        mVisibility = visibility;
        mInstructions = instructions;
        mKeyIngredients = keyIngredients;
        mIngredients = ingredients;
    }

    protected ModelRecipe(Parcel in) {
        mKey = in.readString();
        mName = in.readString();
        mDescription = in.readString();
        mImageUrl = in.readString();
        mOrigin = in.readString();
        mAuthor = in.readString();
        mAuthorUid = in.readString();
        mPrivacy = in.readString();
        mStatus = in.readString();
        mVisibility = in.readString();
        mInstructions = in.createStringArrayList();
        mKeyIngredients = in.createStringArrayList();
        mIngredients = in.createStringArrayList();

        mVoters = in.createStringArrayList();
        mApprovers = in.createStringArrayList();
        mRejectors = in.createStringArrayList();

        mCircleKey = in.readString();
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String origin) {
        mOrigin = origin;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAuthorUid() {
        return mAuthorUid;
    }

    public void setAuthorUid(String authorUid) {
        mAuthorUid = authorUid;
    }

    public String getPrivacy() {
        return mPrivacy;
    }

    public void setPrivacy(String privacy) {
        mPrivacy = privacy;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getVisibility() {
        return mVisibility;
    }

    public void setVisibility(String visibility) {
        mVisibility = visibility;
    }

    public List<String> getInstructions() {
        return mInstructions;
    }

    public void setInstructions(List<String> instructions) {
        mInstructions = instructions;
    }

    public List<String> getKeyIngredients() {
        return mKeyIngredients;
    }

    public void setKeyIngredients(List<String> keyIngredients) {
        mKeyIngredients = keyIngredients;
    }

    public List<String> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<String> ingredients) {
        mIngredients = ingredients;
    }

    public List<String> getVoters() {
        return mVoters;
    }

    public void setVoters(List<String> voters) {
        mVoters = voters;
    }

    public List<String> getApprovers() {
        return mApprovers;
    }

    public void setApprovers(List<String> approvers) {
        mApprovers = approvers;
    }

    public List<String> getRejectors() {
        return mRejectors;
    }

    public void setRejectors(List<String> rejectors) {
        mRejectors = rejectors;
    }

    public String getCircleKey() {
        return mCircleKey;
    }

    public void setCircleKey(String circleKey) {
        mCircleKey = circleKey;
    }

    public static final Creator<ModelRecipe> CREATOR = new Creator<ModelRecipe>() {
        @Override
        public ModelRecipe createFromParcel(Parcel in) {
            return new ModelRecipe(in);
        }

        @Override
        public ModelRecipe[] newArray(int size) {
            return new ModelRecipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mImageUrl);
        dest.writeString(mOrigin);
        dest.writeString(mAuthor);
        dest.writeString(mAuthorUid);
        dest.writeString(mPrivacy);
        dest.writeString(mStatus);
        dest.writeString(mVisibility);
        dest.writeStringList(mInstructions);
        dest.writeStringList(mKeyIngredients);
        dest.writeStringList(mIngredients);

        dest.writeStringList(mVoters);
        dest.writeStringList(mApprovers);
        dest.writeStringList(mRejectors);

        dest.writeString(mCircleKey);
    }
}
