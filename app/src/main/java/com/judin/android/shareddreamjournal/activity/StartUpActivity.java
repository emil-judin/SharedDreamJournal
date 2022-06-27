package com.judin.android.shareddreamjournal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.judin.android.shareddreamjournal.R;

public class StartUpActivity extends AppCompatActivity {
    private static final String TAG = "StartupActivity";

    // Maybe remove this activity and instead route directly to Main
    // If then Main fails to login route to Auth Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null){
            currentUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //startMainActivity();
                    startAuthActivity();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(getApplicationContext(), "Can't login. Check your connection", Toast.LENGTH_LONG).show();
                    startAuthActivity();
                    //startMainActivity();
                }
            });
        } else {
            startAuthActivity();
        }
    }

    private void startMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void startAuthActivity(){
        Intent i = new Intent(this, AuthActivity.class);
        startActivity(i);
        finish();
    }
}