package com.judin.android.shareddreamjournal.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

// TODO: Consistent error handling

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private EditText mEmailEdit, mPasswordEdit;
    private Button mLoginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public LoginFragment() { }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkUI(v);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Disable buttons on Login attempt
                //TODO: error handler for auth?
                final String login = mEmailEdit.getText().toString();
                final String password = mPasswordEdit.getText().toString();

                mAuth.signInWithEmailAndPassword(login, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "User logged in", Toast.LENGTH_LONG).show();
                                //get User from Firestore if needed for main activity

                                startMainActivity();
                            } else {
                                Exception e = task.getException();
                                if(e instanceof FirebaseAuthException){
                                    FirebaseAuthException fae = (FirebaseAuthException) e;
                                    if(fae.getErrorCode().equals("ERROR_INVALID_EMAIL")){
                                        getUserFromFirestore(login, password);
                                    } else {
                                        //TODO: Error handling
                                    }
                                } else {
                                    //TODO: Error handling
                                }
                            }
                        }
                    });
            }
        });

        return v;
    }

    private void getUserFromFirestore(final String username, final String password){
        mFirestore.collection("users")
                .whereEqualTo("username", username)
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot snapshot = task.getResult();
                            if(snapshot.isEmpty()){
                                Toast.makeText(getActivity(), "Failed login", Toast.LENGTH_LONG).show();
                            } else if(snapshot.size() > 1){
                                // More than one user with this name in database
                            } else {
                                User user = snapshot.toObjects(User.class).get(0);
                                loginUser(user, password);
                            }
                        } else {
                            //TODO: Error handling
                        }
                    }
                });
    }

    private void loginUser(User user, String password){
        mAuth.signInWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Go to main
                            startMainActivity();
                        } else {
                            // Wrong password or connectivity problem
                            Toast.makeText(getActivity(), "Wrong password or connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void linkUI(View v){
        mEmailEdit = v.findViewById(R.id.email_edit);
        mPasswordEdit = v.findViewById(R.id.password_edit_login);
        mLoginButton = v.findViewById(R.id.login_button);
    }

    private void startMainActivity(){
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}