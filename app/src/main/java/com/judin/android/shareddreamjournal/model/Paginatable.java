package com.judin.android.shareddreamjournal.model;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public interface Paginatable {
    void addProgressBar();
    void removeProgressBar();
    void append(QuerySnapshot result);
}
