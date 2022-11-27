package com.judin.android.shareddreamjournal.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.Query;

public class FirebaseDataViewModelFactory<D extends FirebaseData> implements ViewModelProvider.Factory {
    private Query mQuery;
    private Class<D> mClass;

    public FirebaseDataViewModelFactory(Query query, Class<D> clazz){
        mQuery = query;
        mClass = clazz;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FirebaseDataViewModel<D>(mQuery, mClass);
    }
}
