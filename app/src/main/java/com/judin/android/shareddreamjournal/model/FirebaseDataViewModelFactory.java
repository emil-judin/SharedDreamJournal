package com.judin.android.shareddreamjournal.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.Query;

public class FirebaseDataViewModelFactory<D extends FirebaseData> implements ViewModelProvider.Factory {
    private Query query;
    private Class<D> clazz;

    public FirebaseDataViewModelFactory(Query query, Class<D> clazz){
        this.query = query;
        this.clazz = clazz;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FirebaseDataViewModel<D>(query, clazz);
    }
}
