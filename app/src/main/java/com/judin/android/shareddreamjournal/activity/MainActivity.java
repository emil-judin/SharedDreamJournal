package com.judin.android.shareddreamjournal.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.judin.android.shareddreamjournal.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}