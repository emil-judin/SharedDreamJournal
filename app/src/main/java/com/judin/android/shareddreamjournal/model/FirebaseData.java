package com.judin.android.shareddreamjournal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public abstract class FirebaseData{
    private String mId;

    public FirebaseData() {}

    public String getId() {
        return mId;
    }

    // TODO: is it ever needed to set and ID?
    public void setId(String id) {
        mId = id;
    }
}
