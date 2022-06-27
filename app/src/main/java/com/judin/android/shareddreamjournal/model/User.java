package com.judin.android.shareddreamjournal.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    @Exclude private String mUid;
    private String mUsername;
    private String mEmail;

    public User() { }

    @Exclude public String getUid() {
        return mUid;
    }

    @Exclude public void setUid(String uid) {
        mUid = uid;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }
}

