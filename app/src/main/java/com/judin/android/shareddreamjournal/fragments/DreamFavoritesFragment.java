package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.judin.android.shareddreamjournal.R;

public class DreamFavoritesFragment extends Fragment {

    public static DreamFavoritesFragment newInstance() {
        return new DreamFavoritesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dream_favorites, container, false);
    }
}