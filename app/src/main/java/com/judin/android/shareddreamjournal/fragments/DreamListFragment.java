package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.judin.android.shareddreamjournal.listener.PaginationScrollListener;
import com.judin.android.shareddreamjournal.model.Dream;
import com.judin.android.shareddreamjournal.model.FirebaseDataViewModel;
import com.judin.android.shareddreamjournal.model.FirebaseDataViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class DreamListFragment extends AbstractDreamListFragment {
    public static final String TAG = "DreamListFragment";

    public static DreamListFragment newInstance() {
        return new DreamListFragment();
    }

    @Override
    public FirebaseDataViewModel<Dream> initializeViewModel() {
        final Query dreamQuery = FirebaseFirestore.getInstance()
                .collection("dreams")
                .orderBy("addedDate", Query.Direction.DESCENDING);
        final Class<Dream> dreamClass = Dream.class;

        return new ViewModelProvider(this, new FirebaseDataViewModelFactory(dreamQuery, dreamClass)).get(FirebaseDataViewModel.class);
    }

}