package com.judin.android.shareddreamjournal.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FirebaseDataViewModel<T extends FirebaseData> extends ViewModel{
    private static final String TAG = "FirebaseDataViewModel";
    private static final int QUERY_LIMIT = 25;

    private Query mBaseQuery;
    private Class<T> mDataClass;
    private DocumentSnapshot mLastVisible;
    private boolean mIsLastReached = false;

    // FirebaseData list
    private MutableLiveData<List<T>> mData;
    // Initial loading of list
    private MutableLiveData<Boolean> mIsInitializing = new MutableLiveData<>();
    // Pagination loading
    private MutableLiveData<Boolean> mIsUpdating = new MutableLiveData<>();

    public FirebaseDataViewModel(Query baseQuery, Class<T> clazz){
        mBaseQuery = baseQuery;
        mDataClass = clazz;
    }

    public MutableLiveData<List<T>> getData() {
        if (mData == null) {
            mData = new MutableLiveData<>();
            mData.setValue(new ArrayList<>());
        }
        return mData;
    }

    public MutableLiveData<Boolean> getIsInitializing() {
        return mIsInitializing;
    }

    public MutableLiveData<Boolean> getIsUpdating() {
        return mIsUpdating;
    }

    public void fetchData() {
        boolean isFirstQuery = (mLastVisible == null);
        Query query = mBaseQuery.limit(QUERY_LIMIT);
        if (isFirstQuery) {
            mIsInitializing.setValue(true);
        } else {
            mIsUpdating.setValue(true);
            query = query.startAfter(mLastVisible);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (isFirstQuery) {
                        mIsInitializing.postValue(false);
                    } else {
                        mIsUpdating.postValue(false);
                    }

                    QuerySnapshot result = task.getResult();
                    if (result.size() < 1) {
                        mIsLastReached = true;
                        if (isFirstQuery) {
                            // No data to begin
                            mData.postValue(new ArrayList<>());
                        } else {
                            // No more data, do special things
                        }
                    } else {
                        // There is at least something
                        mLastVisible = result.getDocuments().get(result.size() - 1);

                        final List<T> newData = result.toObjects(mDataClass);
                        List<T> currentData = mData.getValue();
                        if (currentData == null) {
                            currentData = new ArrayList<>();
                        }

                        List<T> data = Stream.concat(currentData.stream(), newData.stream())
                                .collect(Collectors.toList());
                        mData.postValue(data);
                    }
                } else {
                    Log.e(TAG, "Error when fetching data");
                    // TODO: HandleErrors
                }
            }
        });
    }

    public boolean isLastReached(){
        return mIsLastReached;
    }
}
