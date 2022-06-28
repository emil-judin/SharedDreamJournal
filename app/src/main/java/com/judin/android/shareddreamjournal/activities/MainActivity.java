package com.judin.android.shareddreamjournal.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.judin.android.shareddreamjournal.R;

public class MainActivity extends SingleFragmentActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected Fragment createFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}