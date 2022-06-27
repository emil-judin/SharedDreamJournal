package com.judin.android.shareddreamjournal.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.activity.MainActivity;
import com.judin.android.shareddreamjournal.model.User;

import javax.annotation.Nullable;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    private EditText mEmailEdit, mUsernameEdit, mPasswordEdit, mRepeatPasswordEdit;
    private Button mRegisterButton;

    public RegisterFragment() { }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        return v;
    }

    public void linkUI(View v){
        mEmailEdit = v.findViewById(R.id.email_edit);
        mUsernameEdit = v.findViewById(R.id.username_edit);
        mPasswordEdit = v.findViewById(R.id.password_edit);
        mRepeatPasswordEdit = v.findViewById(R.id.password_repeat_edit);
        mRegisterButton = v.findViewById(R.id.register_button);
    }
}