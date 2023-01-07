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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.judin.android.shareddreamjournal.R;
import com.judin.android.shareddreamjournal.activities.MainActivity;
import com.judin.android.shareddreamjournal.exceptions.EmptyInputException;

// TODO: Consistent error handling

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private static final String REGISTER_FRAGMENT_TAG = "RegisterFragment";
    private static final String FORGOT_PASSWORD_FRAGMENT_TAG = "ForgotPasswordFragment";
    private EditText emailEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private TextView registerHintTextView;
    private TextView forgotPasswordHintTextView;
    private FirebaseAuth auth;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkUI(v);

        loginButton.setOnClickListener(view -> {
            loginButton.setEnabled(false);
            registerHintTextView.setEnabled(false);
            forgotPasswordHintTextView.setEnabled(false);

            final String login = emailEdit.getText().toString();
            final String password = passwordEdit.getText().toString();

            if(login.isEmpty() || password.isEmpty()){
                handleException(new EmptyInputException());
                return;
            }

            auth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "User logged in", Toast.LENGTH_LONG).show();
                        // Get user from Firestore if needed for main activity

                        loginButton.setEnabled(true);
                        registerHintTextView.setEnabled(true);
                        forgotPasswordHintTextView.setEnabled(true);
                        startMainActivity();
                    } else {
                        handleException(task.getException());
                    }
                });
        });

        registerHintTextView.setOnClickListener(view -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Fragment registerFragment = fm.findFragmentByTag(REGISTER_FRAGMENT_TAG);
            if (registerFragment == null) {
                registerFragment = RegisterFragment.newInstance();
            }
            fm.beginTransaction()
                .replace(R.id.fragment_container, registerFragment, REGISTER_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
        });

        forgotPasswordHintTextView.setOnClickListener(view -> {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Fragment forgotPasswordFragment = fm.findFragmentByTag(FORGOT_PASSWORD_FRAGMENT_TAG);
            if (forgotPasswordFragment == null) {
                forgotPasswordFragment = ForgotPasswordFragment.newInstance();
            }
            fm.beginTransaction()
                    .replace(R.id.fragment_container, forgotPasswordFragment, FORGOT_PASSWORD_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
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

        loginButton.setEnabled(true);
        registerHintTextView.setEnabled(true);
        forgotPasswordHintTextView.setEnabled(true);
    }

    private void linkUI(View v){
        emailEdit = v.findViewById(R.id.email_edit);
        passwordEdit = v.findViewById(R.id.password_edit);
        loginButton = v.findViewById(R.id.login_button);
        registerHintTextView = v.findViewById(R.id.register_hint);
        forgotPasswordHintTextView = v.findViewById(R.id.forgot_password_hint);
    }

    private void startMainActivity(){
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}