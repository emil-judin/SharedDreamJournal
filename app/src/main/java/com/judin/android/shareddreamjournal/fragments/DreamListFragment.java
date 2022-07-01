package com.judin.android.shareddreamjournal.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.judin.android.shareddreamjournal.R;

public class DreamListFragment extends Fragment {

    public static DreamListFragment newInstance() {
        return new DreamListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dream_list, container, false);
    }
}