package com.judin.android.shareddreamjournal.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.activities.MainActivity;
import com.judin.android.shareddreamjournal.exceptions.EmptyInputException;
import com.judin.android.shareddreamjournal.model.User;

// TODO: Consistent error handling

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private static final String REGISTER_FRAGMENT_TAG = "RegisterFragment";
    private static final String RESET_PASSWORD_FRAGMENT_TAG = "ResetPasswordFragment";
    private EditText mEmailEdit, mPasswordEdit;
    private Button mLoginButton;
    private TextView mRegisterHint;
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
                mLoginButton.setEnabled(false);
                mLoginButton.setEnabled(false);

                final String login = mEmailEdit.getText().toString();
                final String password = mPasswordEdit.getText().toString();

                if(login.isEmpty() || password.isEmpty()){
                    handleException(new EmptyInputException());
                    return;
                }

                mAuth.signInWithEmailAndPassword(login, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "User logged in", Toast.LENGTH_LONG).show();
                                // Get user from Firestore if needed for main activity

                                mLoginButton.setEnabled(true);
                                mRegisterHint.setEnabled(true);
                                startMainActivity();
                            } else {
                                handleException(task.getException());
                            }
                        }
                    });
            }
        });

        mRegisterHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginButton.setVisibility(View.INVISIBLE);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment registerFragment = fm.findFragmentByTag(REGISTER_FRAGMENT_TAG);
                if (registerFragment == null) {
                    registerFragment = RegisterFragment.newInstance();
                }
                fm.beginTransaction()
                    .replace(R.id.fragment_container, registerFragment, REGISTER_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
            }
        });

        return v;
    }

    // Needed for username login support
//    private void getUserFromFirestore(final String username, final String password){
//        mFirestore.collection("users")
//            .whereEqualTo("username", username)
//            .get(Source.SERVER)
//            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if(task.isSuccessful()){
//                        QuerySnapshot snapshot = task.getResult();
//                        if(snapshot.isEmpty()){
//                            Toast.makeText(getActivity(), "Failed login", Toast.LENGTH_LONG).show();
//                        } else if(snapshot.size() > 1){
//                            // More than one user with this name in database
//                        } else {
//                            User user = snapshot.toObjects(User.class).get(0);
//                            loginUser(user, password);
//                        }
//                    } else {
//                        //TODO: Error handling
//                    }
//                }
//            });
//    }
//
//    private void loginUser(User user, String password){
//        mAuth.signInWithEmailAndPassword(user.getEmail(), password)
//            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if(task.isSuccessful()){
//                        // Go to main
//                        startMainActivity();
//                    } else {
//                        // Wrong password or connectivity problem
//                        Toast.makeText(getActivity(), "Wrong password or connection", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//    }

    private void handleException(Exception exception){
        try{
            throw exception;
        } catch(EmptyInputException e){
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_required_empty, Toast.LENGTH_LONG).show();
        } catch (FirebaseAuthInvalidUserException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_account_does_not_exist, Toast.LENGTH_LONG).show();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getActivity(), R.string.error_wrong_password, Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        mLoginButton.setEnabled(true);
        mRegisterHint.setEnabled(true);
    }

    private void linkUI(View v){
        mEmailEdit = v.findViewById(R.id.email_edit);
        mPasswordEdit = v.findViewById(R.id.password_edit);
        mLoginButton = v.findViewById(R.id.login_button);
        mRegisterHint = v.findViewById(R.id.register_hint);
    }

    private void startMainActivity(){
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}