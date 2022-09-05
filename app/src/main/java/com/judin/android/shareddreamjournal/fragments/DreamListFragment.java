package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.judin.android.shareddreamjournal.listener.PaginationScrollListener;
import com.judin.android.shareddreamjournal.model.Dream;

import java.util.ArrayList;
import java.util.List;

public class DreamListFragment extends AbstractDreamListFragment {
    public static final String TAG = "DreamListFragment";
    private Query mBaseQuery;

    public static DreamListFragment newInstance() {
        return new DreamListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mBaseQuery = FirebaseFirestore.getInstance()
                .collection("dreams")
                .orderBy("addedDate", Query.Direction.DESCENDING)
                .limit(QUERY_LIMIT);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initialize() {
        startLoading();
        mBaseQuery.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result.size() < 1) {
                            // No Dreams returned
                            initializeAdapter(new ArrayList<Dream>());
                            stopLoading();
                            return;
                        }
                        final List<Dream> dreams = result.toObjects(Dream.class);
                        for(int i = 0; i < result.size(); i++){
                            // Is this needed?
                            dreams.get(i).setId(result.getDocuments().get(i).getId());
                        }

                        initializeAdapter(dreams);

                        DocumentSnapshot lastVisible = result.getDocuments().get(result.size() - 1);
                        mRecyclerView.addOnScrollListener(new PaginationScrollListener(mAdapter, lastVisible, mBaseQuery, LOAD_OFFSET));
                    } else {
                        //TODO: ErrorHandler
                        Log.d(TAG, "getDreams() failed");
                    }
                    stopLoading();
                }
            });
    }
}