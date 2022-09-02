package com.judin.android.shareddreamjournal.listener;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.judin.android.shareddreamjournal.model.Paginatable;

public class PaginationScrollListener extends RecyclerView.OnScrollListener{
    private static final String TAG = "PaginationScroll";
    private Paginatable mAdapter;
    private DocumentSnapshot mLastVisible;
    // Needs collection, order and limit
    private final Query mBaseQuery;
    private final int mLoadOffset;

    private boolean mIsLastReached = false;
    private boolean mIsLoading = false;

    // Change to include only basequery?
    public PaginationScrollListener(Paginatable adapter
            , DocumentSnapshot lastVisible
            , Query baseQuery
            , int loadOffset){
        mAdapter = adapter;
        mLastVisible = lastVisible;
        mBaseQuery = baseQuery;
        mLoadOffset = loadOffset;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = linearLayoutManager.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();

        if ((firstVisibleItemPosition + visibleItemCount >= totalItemCount - mLoadOffset)
                && !mIsLastReached && !mIsLoading) {
            mIsLoading = true;
            mAdapter.addProgressBar();

            Query nextQuery = mBaseQuery.startAfter(mLastVisible);
            nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        mAdapter.removeProgressBar();
                        if (task.getResult().size() >= 1) {
                            QuerySnapshot result = task.getResult();
                            mAdapter.append(result);
                            mIsLoading = false;

                            mLastVisible = result.getDocuments().get(result.size() - 1);
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
}