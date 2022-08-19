package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.model.Dream;

import java.util.ArrayList;
import java.util.List;

public class DreamListFragment extends AbstractDreamListFragment {
    public static final String TAG = "DreamListFragment";

    private DocumentSnapshot mLastVisible;
    private boolean mIsLastReached = false;
    private boolean mIsLoading = false;

    private FirebaseFirestore mFirestore;
    private FirebaseUser mUser;

    public static DreamListFragment newInstance() {
        return new DreamListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void update() {
        Query firstQuery = FirebaseFirestore.getInstance().collection("dreams")
                .orderBy("addedDate", Query.Direction.DESCENDING)
                .limit(QUERY_LIMIT);
        firstQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() < 1) {
                                // No Dreams returned
                                setupAdapter(new ArrayList<Dream>());
//                                stopLoading();
                                return;
                            }
                            final List<Dream> dreams = task.getResult().toObjects(Dream.class);
                            int i = 0;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                dreams.get(i).setId(doc.getId());
                                i++;
                            }
//                            setFavorites(dreams);
                            setupAdapter(dreams);
//                            stopLoading();

                            mLastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = linearLayoutManager.getChildCount();
                                    int totalItemCount = linearLayoutManager.getItemCount();

                                    if ((firstVisibleItemPosition + visibleItemCount >= totalItemCount - LOAD_OFFSET)
                                            && !mIsLastReached && !mIsLoading) {
                                        mIsLoading = true;
                                        mAdapter.addProgressBar();

                                        Query nextQuery = FirebaseFirestore.getInstance()
                                                .collection("dreams")
                                                .orderBy("addedDate", Query.Direction.DESCENDING)
                                                .startAfter(mLastVisible)
                                                .limit(QUERY_LIMIT);
                                        nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    mAdapter.removeProgressBar();
                                                    if (task.getResult().size() >= 1) {
                                                        List<Dream> newDreams = task.getResult().toObjects(Dream.class);
//                                                        setFavorites(newDreams);

                                                        mAdapter.appendDreams(newDreams);
                                                        mIsLoading = false;

                                                        mLastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                        if (task.getResult().size() < QUERY_LIMIT) {
                                                            mIsLastReached = true;
                                                        }
                                                    } else {
                                                        mIsLastReached = true;
                                                    }
                                                } else {
                                                    Log.d(TAG, "Pagination failed");
                                                    mAdapter.removeProgressBar();
                                                }
                                            }
                                        });
                                    }
                                }
                            };
                            mRecyclerView.addOnScrollListener(onScrollListener);
                        } else {
                            //TODO: ErrorHandler
                            Log.d(TAG, "getDreams() failed");
                        }
                    }
                });
    }
}