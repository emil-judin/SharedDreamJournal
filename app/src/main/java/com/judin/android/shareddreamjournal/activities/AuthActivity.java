package com.judin.android.shareddreamjournal.activities;

import androidx.fragment.app.Fragment;

import com.judin.android.shareddreamjournal.fragments.LoginFragment;

public class AuthActivity extends SingleFragmentActivity {
    private static final String TAG = "AuthActivity";

    @Override
    protected Fragment createFragment() {
        return LoginFragment.newInstance();
    }
}