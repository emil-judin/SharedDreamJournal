package com.judin.android.shareddreamjournal.activity;

import androidx.fragment.app.Fragment;

import com.judin.android.shareddreamjournal.fragment.LoginFragment;
import com.judin.android.shareddreamjournal.R;

import android.os.Bundle;

public class AuthActivity extends SingleFragmentActivity {
    private static final String TAG = "AuthActivity";

    @Override
    protected Fragment createFragment() {
        return LoginFragment.newInstance();
    }
}