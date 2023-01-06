package com.judin.android.shareddreamjournal.fragments;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.judin.android.shareddreamjournal.model.Dream;
import com.judin.android.shareddreamjournal.model.FirebaseDataViewModel;
import com.judin.android.shareddreamjournal.model.FirebaseDataViewModelFactory;

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