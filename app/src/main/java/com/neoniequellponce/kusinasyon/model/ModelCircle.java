package com.neoniequellponce.kusinasyon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ModelCircle implements Parcelable {

    private String mKey;
    private String mInviteCode;
    private String mName;
    private String mAdmin;

    //Empty Constructor
    public ModelCircle() {

    }

    //Constructor
    public ModelCircle(String key, String inviteCode, String name, String admin) {
        mKey = key;
        mInviteCode = inviteCode;
        mName = name;
        mAdmin = admin;
    }

    protected ModelCircle(Parcel in) {
        mKey = in.readString();
        mInviteCode = in.readString();
        mName = in.readString();
        mAdmin = in.readString();
    }

    public static final Creator<ModelCircle> CREATOR = new Creator<ModelCircle>() {
        @Override
        public ModelCircle createFromParcel(Parcel in) {
            return new ModelCircle(in);
        }

        @Override
        public ModelCircle[] newArray(int size) {
            return new ModelCircle[size];
        }
    };

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getInviteCode() {
        return mInviteCode;
    }

    public void setInviteCode(String inviteCode) {
        mInviteCode = inviteCode;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAdmin() {
        return mAdmin;
    }

    public void setAdmin(String admin) {
        mAdmin = admin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mKey);
        dest.writeString(mInviteCode);
        dest.writeString(mName);
        dest.writeString(mAdmin);
    }
}
