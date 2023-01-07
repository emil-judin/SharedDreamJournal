package com.judin.android.shareddreamjournal.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FirebaseDataViewModel<T extends FirebaseData> extends ViewModel{
    private static final String TAG = "FirebaseDataViewModel";
    private static final int QUERY_LIMIT = 25;

    private Query baseQuery;
    private Class<T> dataClass;
    private DocumentSnapshot lastVisible;
    private boolean isLastReached = false;

    // FirebaseData list
    private MutableLiveData<List<T>> data;
    // Initial loading of list
    private MutableLiveData<Boolean> isInitializing = new MutableLiveData<>();
    // Pagination loading
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();

    public FirebaseDataViewModel(Query baseQuery, Class<T> clazz){
        this.baseQuery = baseQuery;
        dataClass = clazz;
    }

    public MutableLiveData<List<T>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.setValue(new ArrayList<>());
        }
        return data;
    }

    public MutableLiveData<Boolean> getIsInitializing() {
        return isInitializing;
    }

    public MutableLiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    public void fetchData() {
        boolean isFirstQuery = (lastVisible == null);
        Query query = baseQuery.limit(QUERY_LIMIT);
        if (isFirstQuery) {
            isInitializing.setValue(true);
        } else {
            isUpdating.setValue(true);
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (isFirstQuery) {
                        isInitializing.postValue(false);
                    } else {
                        isUpdating.postValue(false);
                    }

                    QuerySnapshot result = task.getResult();
                    if (result.size() < 1) {
                        isLastReached = true;
                        if (isFirstQuery) {
                            // No data to begin
                            data.postValue(new ArrayList<>());
                        } else {
                            // No more data, do special things
                        }
                    } else {
                        // There is at least something
                        lastVisible = result.getDocuments().get(result.size() - 1);

                        final List<T> newData = result.toObjects(dataClass);
                        List<T> currentData = data.getValue();
                        if (currentData == null) {
                            currentData = new ArrayList<>();
                        }

                        List<T> data = Stream.concat(currentData.stream(), newData.stream())
                                .collect(Collectors.toList());
                        FirebaseDataViewModel.this.data.postValue(data);
                    }
                } else {
                    Log.e(TAG, "Error when fetching data");
                    // TODO: HandleErrors
                }
            }
        });
    }

    public boolean isLastReached(){
        return isLastReached;
    }
}
