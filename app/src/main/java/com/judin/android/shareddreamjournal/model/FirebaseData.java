package com.judin.android.shareddreamjournal.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public abstract class FirebaseData{
    private String id;

    public FirebaseData() {}

    public String getId() {
        return id;
    }

    // TODO: is it ever needed to set and ID?
    public void setId(String id) {
        this.id = id;
    }
}
